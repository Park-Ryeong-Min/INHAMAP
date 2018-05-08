package com.example.inhamap.Models;

public class VoicePathElement {

    private long nodeID;
    private double latitude;
    private double longitude;
    private long postNodeID;
    private String voiceText;

    public VoicePathElement(){
        // default constructor
    }

    public VoicePathElement(long id, double lat, double lng, long postID, String text){
        this.nodeID = id;
        this.latitude = lat;
        this.longitude = lng;
        this.postNodeID = postID;
        this.voiceText = text;
    }

    public long getNodeID() {
        return nodeID;
    }

    public void setNodeID(long nodeID) {
        this.nodeID = nodeID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
}
