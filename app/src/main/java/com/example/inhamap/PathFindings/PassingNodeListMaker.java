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
            long last = 0;
            VoicePathElement first = new VoicePathElement(0,
                    this.items.get(0).getNodeLatitude(),
                    this.items.get(0).getNodeLongitude(),
                    0,
                    this.items.get(0).getNodeID(),
                    "경로 안내를 시작합니다.");
            this.voiceElements.add(first);
            for(int i = 0; i < this.items.size(); i++){
                if(i == 0){
                    this.voiceElements.add(
                            new VoicePathElement(
                                    this.items.get(0).getNodeID(), // 현재 Node ID
                                    this.items.get(0).getNodeLatitude(), // Latitude
                                    this.items.get(0).getNodeLongitude(), // Longitude
                                    0, // Post
                                    this.items.get(1).getNodeID(), // Next
                                    getVoiceText(0, this.items.get(0).getNodeID()) // Text
                            )
                    );
                    last = this.items.get(i).getNodeID();
                }else if(i == this.items.size() - 1){
                    this.voiceElements.add(
                            new VoicePathElement(
                                    this.items.get(i).getNodeID(),
                                    this.items.get(i).getNodeLatitude(),
                                    this.items.get(i).getNodeLongitude(),
                                    last,
                                    0,
                                    getVoiceText(last, this.items.get(i).getNodeID())
                            )
                    );
                    last = this.items.get(i).getNodeID();
                }else{
                    this.voiceElements.add(
                            new VoicePathElement(
                                    this.items.get(i).getNodeID(),
                                    this.items.get(i).getNodeLatitude(),
                                    this.items.get(i).getNodeLongitude(),
                                    last,
                                    this.items.get(i+1).getNodeID(),
                                    getVoiceText(last, this.items.get(i).getNodeID())
                            )
                    );
                    last = this.items.get(i).getNodeID();
                }
            }
            if(last != 0) {
                this.voiceElements.add(new VoicePathElement(
                        0,
                        this.items.get(this.items.size() - 1).getNodeLatitude(),
                        this.items.get(this.items.size() - 1).getNodeLongitude(),
                        last,
                        0,
                        "음성 안내를 종료합니다."
                ));
            }
        }
    }

    private String getVoiceText(long post, long cur){
        for(int i = 0; i < this.voices.size(); i++){
            VoiceText tmp = this.voices.get(i);
            if(tmp.getPostNodeID() == post && tmp.getCurNodeID() == cur){
                return tmp.getVoiceText();
            }
        }
        return null;
    }

    public ArrayList<VoicePathElement> getVoiceElements() {
        return voiceElements;
    }
}
