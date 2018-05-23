package com.example.inhamap.PathFindings;

import android.util.Log;

import com.example.inhamap.Commons.DefaultValue;
import com.example.inhamap.Models.AdjacentEdge;
import com.example.inhamap.Models.EdgeList;
import com.example.inhamap.Models.NodeItem;

import java.util.ArrayList;

/**
 * Created by myown on 2018. 4. 28..
 */

public class FindPath {

    // member variables
    private ArrayList<NodeItem> nodes;
    private EdgeList edges;
    private EdgeList paths;
    private long startNodeID;
    private long destinationNodeID;
    private int size;
    private double[][] adj;
    private Node[] memo;
    private double[] dist;
    private int[] check;
    private int[] track;
    private ArrayList<ArrayList<Integer>> pathNode;
    private ArrayList<NodeItem> passingNodes;

    // this class not support default constructor
    // required params 'nodes' : All node data to construct map
    // required params 'edges' : All edge between two nodes in map
    // required params 'start' and 'dest' : Node ID about start node and destination node
    public FindPath(ArrayList<NodeItem> nodes, EdgeList edges, long start, long dest){
        this.passingNodes = new ArrayList<NodeItem>();
        this.nodes = nodes;
        this.size = this.nodes.size();
        this.adj = new double[this.size][this.size];
        this.memo = new Node[this.size];
        this.dist = new double[this.size];
        this.check = new int[this.size];
        this.track = new int[this.size];
        this.edges = edges;
        this.startNodeID = start;
        this.destinationNodeID = dest;
        this.paths = new EdgeList();

        this.pathNode = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < size; i++){
            this.pathNode.add(new ArrayList<Integer>());
        }

        for(int i = 0; i < this.size; i++){
            this.memo[i] = new Node(i, this.nodes.get(i).getNodeID());
        }

        for(int i = 0; i < this.size; i++){
            for(int j = 0; j < this.size; j++){
                adj[i][j] = DefaultValue.INFINITE_DISTANCE_DOUBLE_VALUE;
            }
        }

        for(int i = 0; i < this.size; i++){
            this.dist[i] = DefaultValue.INFINITE_DISTANCE_DOUBLE_VALUE;
            this.check[i] = 0;
            this.track[i] = -1;
        }

        for(int i = 0; i < this.edges.size(); i++){
            AdjacentEdge tmp = this.edges.getEdge(i);
            //Log.e("FIND_ROUTE", Double.toString(tmp.getDistance()));
            NodeItem n1 = tmp.getNodes()[0];
            NodeItem n2 = tmp.getNodes()[1];
            int n1Idx = nodeID2Index(n1.getNodeID());
            int n2Idx = nodeID2Index(n2.getNodeID());
            adj[n1Idx][n2Idx] = tmp.getDistance();
            adj[n2Idx][n1Idx] = tmp.getDistance();
            //Log.e("FIND_ROUTE", Long.toString(n1.getNodeID()) + " -> " + Long.toString(n2.getNodeID()));
        }

        dijkstra();
    }

    private int nodeID2Index(long id){
        for(int i = 0; i < size; i++){
            if(memo[i].nodeID == id){
                return i;
            }
        }
        return -1;
    }

    private void dijkstra(){
        this.dist[nodeID2Index(startNodeID)] = 0;
        for(int k = 0; k < this.size; k++){
            double m = DefaultValue.INFINITE_DISTANCE_DOUBLE_VALUE + 1.0f;
            int x = -1;
            for(int i = 0; i < this.size; i++){
                if(this.check[i] == 0 && m > this.dist[i]){
                    m = this.dist[i];
                    x = i;
                }
            }
            //Log.e("CHECK", Integer.toString(x));
            this.check[x] = 1;
            for(int i = 0; i < this.size; i++){
                //Log.e("DIST", "dist[i] : " + Double.toString(this.dist[i]) + " , dist[x] : " + Double.toString(this.dist[x]) + " , adj[x][i] : " + Double.toString(this.adj[x][i]) + " , ressult : " + Double.toString(this.dist[x] + this.adj[x][i]));
                if(this.dist[i] > this.dist[x] + this.adj[x][i]){
                    this.dist[i] = this.dist[x] + this.adj[x][i];
                    this.track[i] = x;
                }
            }
        }
        findRoute(nodeID2Index(destinationNodeID));
    }

    private void findRoute(int x){
        // build path by reverse recursive search to destination node until start node.
        //Log.e("FIND_ROUTE", Long.toString(this.memo[x].nodeID));
        this.passingNodes.add(findByNodeID(this.memo[x].nodeID));
        if(this.memo[x].nodeID == startNodeID){
            return;
        }
        if(this.track[x] == -1){
            return;
        }else{
            paths.addEdge(findByNodeID(this.memo[x].nodeID), findByNodeID(this.memo[this.track[x]].nodeID));
            findRoute(this.track[x]);
        }
    }

    private NodeItem findByNodeID(long id){
        for(int i = 0; i < this.size; i++){
            if(this.nodes.get(i).getNodeID() == id){
                return this.nodes.get(i);
            }
        }
        return null;
    }

    public EdgeList getPaths(){
        return this.paths;
    }

    // use private class for structured data
    private class Node{
        public int index;
        public long nodeID;

        public Node(int idx, long id){
            this.index = idx;
            this.nodeID = id;
        }
    }

    public ArrayList<NodeItem> getPassingNodes() {
        return passingNodes;
    }

    public void setPassingNodes(ArrayList<NodeItem> passingNodes) {
        this.passingNodes = passingNodes;
    }
}
