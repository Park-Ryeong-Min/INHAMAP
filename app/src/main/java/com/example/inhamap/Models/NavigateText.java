package com.example.inhamap.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NavigateText {

    private long curNodeID;
    private long prevNodeID;
    private long nextNodeID;
    private String text;

    public NavigateText(){
        // default constructor
    }

    public NavigateText(long prev, long cur, long next, String text){
        this.curNodeID = cur;
        this.prevNodeID = prev;
        this.nextNodeID = next;
        this.text = text;
    }

    public long getCurNodeID() {
        return curNodeID;
    }

    public void setCurNodeID(long curNodeID) {
        this.curNodeID = curNodeID;
    }

    public long getPrevNodeID() {
        return prevNodeID;
    }

    public void setPrevNodeID(long prevNodeID) {
        this.prevNodeID = prevNodeID;
    }

    public long getNextNodeID() {
        return nextNodeID;
    }

    public void setNextNodeID(long nextNodeID) {
        this.nextNodeID = nextNodeID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
