package com.example.inhamap.Models;

/**
 * Created by myown on 2018. 4. 24..
 */

public class NodeItem {

    private int nodeStatus;
    private int marginTop;
    private int marginLeft;
    private String nodeName;
    private double nodeLongitude;
    private double nodeLatitude;
    private long nodeID;

    public NodeItem(int status, int left, int top, String name, double lat, double lng){
        this.nodeStatus = status;
        this.marginTop = top;
        this.marginLeft = left;
        this.nodeName = name;
        this.nodeLongitude = lng;
        this.nodeLatitude = lat;
        this.nodeID = 0;
    }

    public NodeItem(int status, int left, int top, String name, double lat, double lng, long id){
        this.nodeStatus = status;
        this.marginTop = top;
        this.marginLeft = left;
        this.nodeName = name;
        this.nodeLongitude = lng;
        this.nodeLatitude = lat;
        this.nodeID = id;
    }

    public int getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(int nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public double getNodeLongitude() {
        return nodeLongitude;
    }

    public void setNodeLongitude(double nodeLongitude) {
        this.nodeLongitude = nodeLongitude;
    }

    public double getNodeLatitude() {
        return nodeLatitude;
    }

    public void setNodeLatitude(double nodeLatitude) {
        this.nodeLatitude = nodeLatitude;
    }

    public long getNodeID() {
        return nodeID;
    }

    public void setNodeID(long nodeID) {
        this.nodeID = nodeID;
    }
}
