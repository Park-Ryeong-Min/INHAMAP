package com.example.inhamap.Utils;

import com.example.inhamap.Models.NodeItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by myown on 2018. 4. 30..
 */

public class NodeListMaker {
    private ArrayList<NodeItem> items;

    public NodeListMaker(JSONObject input){
        this.items = new ArrayList<NodeItem>();
        try{
            JSONArray arr = input.getJSONArray("nodeList");
            for(int i = 0; i < arr.length(); i++){
                JSONObject tmp = arr.getJSONObject(i);
                long id = tmp.getLong("nodeID");
                int status = tmp.getInt("nodeStatus");
                String name = tmp.getString("nodeName");
                double lat = tmp.getDouble("latitude");
                double lng = tmp.getDouble("longitude");
                int left = tmp.getInt("screenLeftPosition");
                int top = tmp.getInt("screenTopPosition");
                int elev = tmp.getInt("elevator");
                int slp = tmp.getInt("slope");
                this.items.add(new NodeItem(status, left, top, name, lat, lng, id, elev, slp));
            }
        }catch (JSONException jsonEx){
            jsonEx.printStackTrace();
        }
    }

    public ArrayList<NodeItem> getItems(){
        return this.items;
    }
}
