package com.example.inhamap.PathFindings;

import android.content.Context;
import android.util.Log;

import com.example.inhamap.Models.EdgeList;
import com.example.inhamap.Models.NodeItem;
import com.example.inhamap.Models.VoicePathElement;
import com.example.inhamap.Utils.JSONFileParser;

import org.json.JSONObject;

import java.util.ArrayList;

public class PassingNodeListMaker {

    private ArrayList<NodeItem> items;
    private Context context;
    private JSONObject json;
    private ArrayList<VoicePathElement> voiceElements;

    public PassingNodeListMaker(Context context){
        init(context);
    }

    public PassingNodeListMaker(Context context, ArrayList<NodeItem> items){
        this.items = items;
        init(context);
    }

    private void init(Context context){
        this.items = new ArrayList<NodeItem>();
        this.voiceElements = new ArrayList<VoicePathElement>();
        this.context = context;
        JSONFileParser parser = new JSONFileParser(this.context, "node_data");
        this.json = parser.getJSON();
    }

    public void setItems(ArrayList<NodeItem> items){
        this.items = items;
    }

    public void getPassingNodes(){
        if(this.items == null){
            Log.e("NODE_MAKER", "ArrayList items is null.");
            return;
        }else{
            
        }
    }

}
