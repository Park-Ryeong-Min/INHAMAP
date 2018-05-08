package com.example.inhamap.Models;

import java.util.ArrayList;

public class AdjacentNode {
    private long adjacentNodeID;
    private int edgeStatus;
    private ArrayList<VoiceText> voice;

    public AdjacentNode(){
        // default constructor
        this.voice = new ArrayList<VoiceText>();
    }

    public AdjacentNode(long id, int status, ArrayList<VoiceText> voice){
        this.adjacentNodeID = id;
        this.edgeStatus = status;
        this.voice = voice;
    }

    public long getAdjacentNodeID() {
        return adjacentNodeID;
    }

    public void setAdjacentNodeID(long adjacentNodeID) {
        this.adjacentNodeID = adjacentNodeID;
    }

    public int getEdgeStatus() {
        return edgeStatus;
    }

    public void setEdgeStatus(int edgeStatus) {
        this.edgeStatus = edgeStatus;
    }

    public ArrayList<VoiceText> getVoice() {
        return voice;
    }

    public void setVoice(ArrayList<VoiceText> voice) {
        this.voice = voice;
    }
}
