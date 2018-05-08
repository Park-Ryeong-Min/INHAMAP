package com.example.inhamap.PathFindings;

import android.content.Context;
import android.util.Log;

import com.example.inhamap.Models.EdgeList;
import com.example.inhamap.Models.NodeItem;
import com.example.inhamap.Models.VoicePathElement;
import com.example.inhamap.Models.VoiceText;
import com.example.inhamap.Utils.JSONFileParser;
import com.example.inhamap.Utils.NodeListMaker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class PassingNodeListMaker {

    private ArrayList<NodeItem> items;
    private Context context;
    private JSONObject json;
    private ArrayList<VoicePathElement> voiceElements;
    private ArrayList<NodeItem> allNodes;
    private ArrayList<VoiceText> voices;

    public PassingNodeListMaker(Context context, ArrayList<NodeItem> items){
        this.items = items;
        Collections.reverse(this.items); // 뒤집기
        init(context);
        getPassingNodes();
    }

    private void init(Context context){
        this.voiceElements = new ArrayList<VoicePathElement>();
        this.voices = new ArrayList<VoiceText>();
        this.context = context;
        JSONFileParser parser = new JSONFileParser(this.context, "node_data");
        this.json = parser.getJSON();
        this.allNodes = new NodeListMaker(this.json).getItems();

        // voice text 불러오기
        try{
            JSONArray arr1 = this.json.getJSONArray("nodeList");
            for(int i = 0; i < arr1.length(); i++){
                JSONObject tmp1 = arr1.getJSONObject(i);
                long cur = tmp1.getLong("nodeID");
                JSONArray arr2 = tmp1.getJSONArray("adjacent");
                for(int j = 0; j < arr2.length(); j++){
                    JSONObject tmp2 = arr2.getJSONObject(j);
                    long next = tmp2.getLong("nodeID");
                    JSONArray arr3 = tmp2.getJSONArray("voice");
                    for(int k = 0; k < arr3.length(); k++){
                        JSONObject tmp3 = arr3.getJSONObject(k);
                        long post = tmp3.getLong("postNodeID");
                        String t = tmp3.getString("voiceText");
                        VoiceText e = new VoiceText(post, cur, next, t);
                        this.voices.add(e);
                    }
                }
            }
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        //Log.e("PASSING_NODE", Integer.toString(this.voices.size()));
    }

    public void setItems(ArrayList<NodeItem> items){
        this.items = items;
        Collections.reverse(this.items);
    }

    public void getPassingNodes(){
        if(this.items == null){
            Log.e("NODE_MAKER", "ArrayList items is null.");
            return;
        }else{
            long tmpNodeID = items.get(0).getNodeID();
            VoicePathElement first = new VoicePathElement();
            first.setNodeID(items.get(0).getNodeID());
            first.setLatitude(items.get(0).getNodeLatitude());
            first.setLongitude(items.get(0).getNodeLongitude());
            first.setPostNodeID(0);
            first.setVoiceText("경로 탐색을 시작합니다.");
            voiceElements.add(first);
            for(int i = 1; i < items.size(); i++){
                long curNodeID = items.get(i).getNodeID();
                double curNodeLat = items.get(i).getNodeLatitude();
                double curNodeLng = items.get(i).getNodeLongitude();
                long postNodeID = tmpNodeID;
                tmpNodeID = curNodeID;
                String text = "";
                for(int j = 0; j < this.voices.size(); j++){
                    if(curNodeID == this.voices.get(j).getCurNodeID() && postNodeID == this.voices.get(j).getPostNodeID()){
                        text = this.voices.get(j).getVoiceText();
                        break;
                    }
                }
                this.voiceElements.add(new VoicePathElement(curNodeID, curNodeLat, curNodeLng, postNodeID, text));
            }
        }
    }

    public ArrayList<VoicePathElement> getVoiceElements() {
        return voiceElements;
    }
}
