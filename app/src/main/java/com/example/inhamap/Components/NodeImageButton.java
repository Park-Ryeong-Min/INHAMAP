package com.example.inhamap.Components;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.inhamap.Models.NodeItem;
import com.example.inhamap.R;

/**
 * Created by myown on 2018. 4. 24..
 */

public class NodeImageButton extends AppCompatImageButton {

    private long nodeID;

    public NodeImageButton(Context context) {
        super(context);
    }

    public NodeImageButton(Context context, NodeItem item){
        super(context);
        this.nodeID = item.getNodeID();
        boolean isEdge = false;
        switch (item.getNodeStatus()){
            case 0:{
                this.setBackgroundResource(R.drawable.default_door);
                break;
            }
            case 1:{
                isEdge = true;
                this.setBackgroundResource(R.drawable.edge_image);
                break;
            }
            case 2:{
                this.setBackgroundResource(R.drawable.node_icon_3);
                break;
            }
            case 3:{
                this.setBackgroundResource(R.drawable.start_door);
                break;
            }
            case 4:{
                this.setBackgroundResource(R.drawable.destination_door);
                break;
            }
            default:{
                this.setBackgroundResource(R.drawable.node_icon_1);
                break;
            }
        }

        // 버튼의 영역을 30dp 로 고정
        FrameLayout.LayoutParams p;
        float d = context.getResources().getDisplayMetrics().density;
        if(isEdge){
            final int width1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            final int height1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            p = new FrameLayout.LayoutParams(width1, height1);
            p.setMargins((int)(d*item.getMarginLeft()) - 5, (int)(d*item.getMarginTop()) - 5, 0, 0);
        }else{
            final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
            final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
            p = new FrameLayout.LayoutParams(width, height);
            p.setMargins((int)(d*item.getMarginLeft()) - 25, (int)(d*item.getMarginTop()) - 25, 0, 0);
        }
        this.setLayoutParams(p);
    }

    public void setBackgroundImageByStatus(int status){
        switch (status){
            case 0:{
                this.setBackgroundResource(R.drawable.default_door);
                break;
            }
            case 1:{
                this.setBackgroundResource(R.drawable.edge_image);
                break;
            }
            case 2:{
                this.setBackgroundResource(R.drawable.node_icon_3);
                break;
            }
            case 3:{
                this.setBackgroundResource(R.drawable.start_door);
                break;
            }
            case 4:{
                this.setBackgroundResource(R.drawable.destination_door);
                break;
            }
            default:{
                this.setBackgroundResource(R.drawable.node_icon_1);
                break;
            }
        }
    }

    public long getNodeID(){
        return this.nodeID;
    }
}
