package com.example.inhamap.Models;

public class VoiceText {
    private long postNodeID;
    private long curNodeID;
    private long nextNodeID;
    private String voiceText;

    public VoiceText(){
        // default constructor
    }

    public VoiceText(long postID, long curID, long nextID, String text){
        this.postNodeID = postID;
        this.curNodeID = curID;
        this.nextNodeID = nextID;
        this.voiceText = text;
    }

    public long getPostNodeID() {
        return postNodeID;
    }

    public void setPostNodeID(long postNodeID) {
        this.postNodeID = postNodeID;
    }

    public String getVoiceText() {
        return voiceText;
    }

    public void setVoiceText(String voiceText) {
        this.voiceText = voiceText;
    }

    public long getCurNodeID() {
        return curNodeID;
    }

    public void setCurNodeID(long curNodeID) {
        this.curNodeID = curNodeID;
    }

    public long getNextNodeID() {
        return nextNodeID;
    }

    public void setNextNodeID(long nextNodeID) {
        this.nextNodeID = nextNodeID;
    }
}
