package com.example.inhamap.Components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.inhamap.Models.AdjacentEdge;
import com.example.inhamap.Models.EdgeList;
import com.example.inhamap.Models.NodeItem;
import com.example.inhamap.PathFindings.FindPath;
import com.example.inhamap.R;
import com.example.inhamap.Utils.EdgeListMaker;
import com.example.inhamap.Utils.JSONFileParser;
import com.example.inhamap.Utils.NodeListMaker;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

public class OptionSelectDialog extends Dialog {

    private String title;
    private String content;

    private Context context;

    private TextView titleTextView;
    private TextView contentTextView;
    private Button basicRouteSelectButton;
    private Button exceptStairSelectButton;

    private TestDrawingView mapDrawingView;
    private long startNodeID;
    private long destinationNodeID;

    private ArrayList<NodeItem> allNodes;
    private ArrayList<NodeItem> list;
    private EdgeList edges;
    private JSONObject mapData;

    public OptionSelectDialog(Context context, String title, String content, TestDrawingView view, long start, long dest){
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.title = title;
        this.content = content;
        this.mapDrawingView = view;
        this.startNodeID = start;
        this.destinationNodeID = dest;

        initData();
    }

    public OptionSelectDialog(Context context, TestDrawingView view, long start, long dest){
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.title = "경로 탐색 옵션";
        this.content = "경로를 탐색할 때,\n 계단을 제외한 평지를 선택하려면 오른쪽 버튼을\n 계단을 포함하려면 왼쪽 버튼을 선택하십시오.";
        this.mapDrawingView = view;
        this.startNodeID = start;
        this.destinationNodeID = dest;

        initData();
    }

    private void initData(){
        this.list = new ArrayList<NodeItem>();
        this.allNodes = new ArrayList<NodeItem>();

        JSONFileParser json = new JSONFileParser(context, "node_data_v2");
        this.mapData = json.getJSON();
        NodeListMaker list = new NodeListMaker(this.mapData);
        //EdgeListMaker edges = new EdgeListMaker(this.mapData);
        //this.edges = edges.getEdges();
        ArrayList<NodeItem> items = list.getItems();
        for(int i = 0; i < items.size(); i++){
            this.allNodes.add(items.get(i));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면을 흐리게 표현
        WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        windowLayoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(windowLayoutParams);

        setContentView(R.layout.option_select_dialog);

        this.titleTextView = (TextView)findViewById(R.id.option_select_dialog_title);
        this.contentTextView = (TextView)findViewById(R.id.option_select_dialog_content);
        this.basicRouteSelectButton = (Button)findViewById(R.id.option_button_basic);
        this.exceptStairSelectButton = (Button)findViewById(R.id.option_button_except_stair);

        this.titleTextView.setText(this.title);
        this.contentTextView.setText(this.content);

        this.basicRouteSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // find basic shortest path.
                // 아래 3줄 코드를 함께 사용해야 검색이 가능
                edges = new EdgeListMaker(mapData, 0).getEdges();
                addNodes(edges);
                FindPath find = new FindPath(list, edges, startNodeID, destinationNodeID);
                EdgeList path = find.getPaths();
                //mapDrawingView.drawingPath(path);
                mapDrawingView.drawEdges(path);

                /*
                find.logPath();
                find.logDistance();
                find.logSumOfDistance();
                */

                dismiss();
            }
        });

        this.exceptStairSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // find shortest path except stairs.
                // 아래 3줄 코드를 함께 사용해야 검색이 가능
                edges = new EdgeListMaker(mapData, 1).getEdges();
                addNodes(edges);
                FindPath find = new FindPath(list, edges, startNodeID, destinationNodeID);

                EdgeList path = find.getPaths();
                //mapDrawingView.drawingPath(path);
                mapDrawingView.drawEdges(path);

                /*
                find.logPath();
                find.logDistance();
                find.logSumOfDistance();
                */

                dismiss();
            }
        });
    }

    private void addNodes(EdgeList edges){
        for(int i = 0; i < edges.size(); i++){
            AdjacentEdge e = edges.getEdge(i);
            long n1 = e.getNodes()[0].getNodeID();
            long n2 = e.getNodes()[1].getNodeID();
            boolean c1 = false;
            boolean c2 = false;
            for(int j = 0; j < list.size(); j++){
                if(list.get(j).getNodeID() == n1){
                    c1 = true;
                }
            }
            for(int j = 0; j < list.size(); j++){
                if(list.get(j).getNodeID() == n2){
                    c2 = true;
                }
            }
            if(!c1){
                for(int j = 0; j < allNodes.size(); j++){
                    if(allNodes.get(j).getNodeID() == n1){
                        list.add(allNodes.get(j));
                    }
                }
            }
            if(!c2){
                for(int j = 0; j < allNodes.size(); j++){
                    if(allNodes.get(j).getNodeID() == n2){
                        list.add(allNodes.get(j));
                    }
                }
            }
        }
    }
}
