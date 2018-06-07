package com.example.inhamap.Utils;

import android.util.Log;

import com.example.inhamap.Models.NavigateText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NavigateTextListMaker {
    private ArrayList<NavigateText> texts;

    public NavigateTextListMaker(JSONObject json){
        this.texts = new ArrayList<NavigateText>();
        try {
            JSONArray arr = json.getJSONArray("nodeList");
            for(int i = 0; i < arr.length(); i++){
                JSONObject node = arr.getJSONObject(i);
                long cur = node.getLong("nodeID");
                JSONArray adjList = node.getJSONArray("voiceText");
                for(int j = 0; j < adjList.length(); j++){
                    JSONObject adjNode = adjList.getJSONObject(j);
                    long prev = adjNode.getLong("prevNodeID");
                    long next = adjNode.getLong("nextNodeID");
                    String text = adjNode.getString("text");
                    texts.add(new NavigateText(prev, cur, next, text));
                    //Log.e("NAVI_TEXT", Long.toString(prev) + " -> " + Long.toString(cur) + " -> " + Long.toString(next));
                }
            }
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    public ArrayList<NavigateText> getTexts() {
        return this.texts;
    }
}
