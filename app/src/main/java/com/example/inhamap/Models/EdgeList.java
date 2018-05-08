package com.example.inhamap.Models;

import android.util.Log;

import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * Created by myown on 2018. 4. 28..
 */

public class EdgeList {

    private ArrayList<AdjacentEdge> list;

    public EdgeList(){
        this.list = new ArrayList<AdjacentEdge>();
    }

    public boolean addEdge(NodeItem data1, NodeItem data2){
        boolean ret = true;
        for(int i = 0; i < this.list.size(); i++){
            NodeItem nodes[] = this.list.get(i).getNodes();
            if((isEqual(nodes[0], data1) && isEqual(nodes[1], data2) || (isEqual(nodes[1], data1) && isEqual(nodes[0], data2)))){
                // 같은게 있으면 거른다
                ret = false;
            }
        }
        if(ret){
            // 같은게 없음
            //Log.e("ADD", Long.toString(data1.getNodeID()) + " - " + Long.toString(data2.getNodeID()));
            AdjacentEdge e = new AdjacentEdge(data1, data2);
            this.list.add(e);
        }
        return ret;
    }

    private boolean isEqual(NodeItem a, NodeItem b){
        if(a.getNodeLongitude() == b.getNodeLongitude() && a.getNodeLatitude() == b.getNodeLatitude() && a.getNodeID() == b.getNodeID()){
            return true;
        }
        return false;
    }

    public AdjacentEdge getEdge(int i){
        return this.list.get(i);
    }

    public AdjacentEdge getEdge(double lat1, double lng1, double lat2, double lng2){
        for(int i = 0; i < this.list.size(); i++){
            NodeItem tmp[] = this.list.get(i).getNodes();
            if(((tmp[0].getNodeLongitude() == lng1) && (tmp[0].getNodeLatitude() == lat1)) && ((tmp[1].getNodeLongitude() == lng2) && (tmp[1].getNodeLatitude() == lat2))){
                return this.list.get(i);
            }
            if(((tmp[1].getNodeLongitude() == lng1) && (tmp[1].getNodeLatitude() == lat1)) || ((tmp[0].getNodeLongitude() == lng2) && (tmp[0].getNodeLatitude() == lat2))){
                return this.list.get(i);
            }
        }
        return null;
    }

    public boolean isEmpty(){
        return this.list.isEmpty();
    }

    public void removeEdge(int i){
        this.list.remove(i);
    }

    public void removeEdge(long id1, long id2){
        for(int i = 0; i < this.list.size(); i++){
            NodeItem tmp[] = this.list.get(i).getNodes();
            if((tmp[0].getNodeID() == id1) && (tmp[1].getNodeID() == id2)){
                this.removeEdge(i);
                return;
            }
            if((tmp[1].getNodeID() == id1) && (tmp[0].getNodeID() == id2)){
                this.removeEdge(i);
                return;
            }
        }
    }

    public void removeEdge(AdjacentEdge e){
        this.list.remove(e);
    }

    public int size(){
        return this.list.size();
    }
}
