package com.example.inhamap.Runnables;

import com.example.inhamap.Commons.GlobalApplication;
import com.example.inhamap.Models.EdgeList;

public class DrawControlFromOtherThread implements Runnable {

    private int status;
    private EdgeList edge;

    public DrawControlFromOtherThread(){
        this.status = -1;
    }

    public void allEdgeClearOnView(){
        this.status = 0;
    }

    public void redrawEdgesOnView(EdgeList e){
        this.status = 1;
        this.edge = e;
    }

    @Override
    public void run() {
        switch (this.status){
            case 0:{
                GlobalApplication.view.clearEdges();
                break;
            }
            case 1:{
                GlobalApplication.view.drawEdges(this.edge);
            }
        }
    }
}
