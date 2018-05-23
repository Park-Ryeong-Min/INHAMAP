package com.example.inhamap.PathFindings;

import com.example.inhamap.Commons.DefaultValue;
import com.example.inhamap.Models.AdjacentEdge;
import com.example.inhamap.Models.EdgeList;
import com.example.inhamap.Models.NodeItem;
import com.example.inhamap.Models.VoicePathElement;
import com.example.inhamap.Utils.ValueConverter;

import java.util.ArrayList;

public class GuidePath {

    private EdgeList edge;
    private ArrayList<NodeItem> node; // ordered passing node
    private long destNodeID;
    private ArrayList<VoicePathElement> voice;

    public GuidePath(){
        // default constructor
    }

    public GuidePath(EdgeList edge, ArrayList<NodeItem> node, long dest, ArrayList<VoicePathElement> voice){
        this.edge = edge;
        this.node = node;
        this.destNodeID = dest;
        this.voice = voice;
    }

    private double getAverageDistanceByTwoNodes(double lat, double lng, NodeItem n1, NodeItem n2){
        double ret = 0D;
        ret += ValueConverter.distance(lat, n1.getNodeLatitude(), lng, n1.getNodeLongitude());
        ret += ValueConverter.distance(lat, n2.getNodeLatitude(), lng, n2.getNodeLongitude());

        return ret / 2D;
    }

    private int findEdgeNumber(double lat, double lng){
        int ret = 0;
        double avgDist = DefaultValue.INFINITE_DISTANCE_DOUBLE_VALUE;
        for(int i = 0; i < this.edge.size(); i++){
            AdjacentEdge e = this.edge.getEdge(i);
            NodeItem n1 = e.getNodes()[0];
            NodeItem n2 = e.getNodes()[1];

            double avg = getAverageDistanceByTwoNodes(lat, lng, n1, n2);
            if(avg <= avgDist){
                ret = i;
                avgDist = avg;
            }
        }
        return ret;
    }

    private int getStatusFromPath(double lat, double lng, int edgeNum){
        // 출발에 인접 : return -1
        // 노드의 중간 : return 0
        // 도착에 인접 : return 1
        NodeItem start = this.node.get(edgeNum);
        NodeItem end = this.node.get(edgeNum + 1);

        double d1 = ValueConverter.distance(lat, start.getNodeLatitude(), lng, start.getNodeLongitude());
        double d2 = ValueConverter.distance(lat, end.getNodeLatitude(), lng, end.getNodeLongitude());

        if(d1 <= d2){
            // d1 이 더 작음 d1 의 값을 비교해서 값이 거의 0에 수렴하면? return -1
            if(d1 <= 0.0000001D){
                return -1;
            }else{
                return 0;
            }
        }else{
            // d2 가 더 작음
            if(d2 <= 0.0000001D){
                return 1;
            }else{
                return 0;
            }
        }
    }

    public GuideOutput getResult(double lat, double lng){
        int edgeNum = findEdgeNumber(lat, lng);
        int status = getStatusFromPath(lat, lng, edgeNum);

        long startID = this.node.get(edgeNum).getNodeID();
        long endID = this.node.get(edgeNum + 1).getNodeID();

        String text = "";
        if(status == -1){
            text = "전방으로 진행하십시오.";
        }else if(status == 0){
            if(voice != null) {
                for (int i = 0; i < voice.size(); i++) {
                    if (voice.get(i).getNodeID() == startID && voice.get(i).getNextNodeID() == endID) {
                        text = voice.get(i).getVoiceText();
                    }
                }
            }else{
                text = "전방으로 진행하십시오.";
            }
            if(text == ""){
                text = "전방으로 진행하십시오.";
            }
        }else{
            text = "교차로에 도착하였습니다.";
        }
        GuideOutput ret = new GuideOutput(text, edgeNum, status);
        return ret;
    }
    
}
