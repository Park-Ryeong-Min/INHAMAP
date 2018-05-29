package com.example.inhamap.Models;

import com.example.inhamap.Commons.DefaultValue;

/**
 * Created by myown on 2018. 4. 28..
 */

public class AdjacentEdge {

    // member variables
    private NodeItem[] nodes;
    private double distance;

    // default constructor
    public AdjacentEdge(){
        this.nodes = new NodeItem[2];
        this.distance = DefaultValue.INFINITE_DISTANCE_DOUBLE_VALUE;
    }

    // constructor using params two NodeItem.
    public AdjacentEdge(NodeItem n1, NodeItem n2){
        this.nodes = new NodeItem[2];
        this.nodes[0] = n1;
        this.nodes[1] = n2;
        this.distance = distance(this.nodes[0].getNodeLatitude(), this.nodes[0].getNodeLongitude(), this.nodes[1].getNodeLatitude(), this.nodes[1].getNodeLongitude());
    }

    // constructor using params two NodeItem and distance between two NodeItems.
    public AdjacentEdge(NodeItem n1, NodeItem n2, double d){
        this.nodes = new NodeItem[2];
        this.nodes[0] = n1;
        this.nodes[1] = n2;
        this.distance = d;
    }

    /*
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private double getDistance(double lat1, double lat2, double lng1, double lng2){
        double theta = lng1 - lng2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;
        return dist;
    }
    */

    public double distance(double lat1, double lat2,
                           double lng1, double lng2){
        double a = (lat1-lat2)*distPerLat(lat1);
        double b = (lng1-lng2)*distPerLng(lat1);
        return Math.sqrt(a*a+b*b);
    }

    private double distPerLng(double lat){
        return 0.0003121092*Math.pow(lat, 4)
                +0.0101182384*Math.pow(lat, 3)
                -17.2385140059*lat*lat
                +5.5485277537*lat+111301.967182595;
    }

    private double distPerLat(double lat){
        return -0.000000487305676*Math.pow(lat, 4)
                -0.0033668574*Math.pow(lat, 3)
                +0.4601181791*lat*lat
                -1.4558127346*lat+110579.25662316;
    }

    public NodeItem[] getNodes() {
        return nodes;
    }

    public void setNodes(NodeItem[] nodes) {
        this.nodes = nodes;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isEqual(AdjacentEdge e){
        if(this.getDistance() != e.getDistance()){
            return false;
        }

        NodeItem n1 = this.getNodes()[0];
        NodeItem n2 = this.getNodes()[1];

        NodeItem n3 = e.getNodes()[0];
        NodeItem n4 = e.getNodes()[1];

        if((n1.getNodeID() == n3.getNodeID()) && (n2.getNodeID() == n4.getNodeID())){
            return true;
        }else if((n1.getNodeID() == n4.getNodeID()) && (n2.getNodeID() == n3.getNodeID())){
            return true;
        }else{
            return false;
        }
    }
}
