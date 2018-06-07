package com.example.inhamap.PathFindings;

import android.util.Log;

import com.example.inhamap.Commons.DefaultValue;
import com.example.inhamap.Commons.GlobalApplication;
import com.example.inhamap.Models.AdjacentEdge;
import com.example.inhamap.Models.EdgeList;
import com.example.inhamap.Models.NavigateText;
import com.example.inhamap.Models.NodeItem;
import com.example.inhamap.Models.TupleLong;
import com.example.inhamap.Utils.JSONFileParser;
import com.example.inhamap.Utils.NavigateTextListMaker;
import com.example.inhamap.Utils.ValueConverter;

import org.json.JSONObject;

import java.util.ArrayList;

public class NavigatePath {

    // 길을 찾는데 필요한 지도 상에 등록된 node, edge 정보들
    private EdgeList allEdges;
    private ArrayList<NodeItem> allNodes;

    // 길을 찾고난 후 해당하는 node, edge 정보들
    // 경로 설정을 실시간으로 해야 하기 때문에 이 값들은 언제든지 바뀔 수 있다.
    private EdgeList pathEdges;
    private ArrayList<NodeItem> passingNodes;
    private FindPath pathFinding;
    private long destNodeID;
    private ArrayList<NavigateText> texts;
    private ArrayList<NavigateText> naviTexts;

    // 네비게이션 알고리즘 개선 v1.2
    private NodeItem ptr;

    // constructor
    public NavigatePath(){
        init();
    }

    public NavigatePath(long start, long dest){
        init();
        this.destNodeID = dest;
        findPath(start, dest);
    }

    private void init(){
        this.allEdges = GlobalApplication.edgesExceptStairs;
        this.allNodes = GlobalApplication.nodesExceptStairs;
        this.texts = GlobalApplication.navigateTexts;
    }

    public void findPath(long start, long dest){
        this.pathFinding = new FindPath(this.allNodes, this.allEdges, start, dest);
        this.pathEdges = this.pathFinding.getPaths();
        this.passingNodes = this.pathFinding.getPassingNodes();
        getNavigateTextList();
    }

    private void getNavigateTextList(){
        this.naviTexts = new ArrayList<NavigateText>();
        ArrayList<TupleLong> tuples = new ArrayList<TupleLong>();
        long prev = 0;
        long cur = this.passingNodes.get(0).getNodeID();
        for(int i = 0; i < this.passingNodes.size(); i++){
            cur = this.passingNodes.get(i).getNodeID();
            if(i + 1 < this.passingNodes.size()){
                long next = this.passingNodes.get(i+1).getNodeID();
                tuples.add(new TupleLong(prev, cur, next));
                prev = cur;
            }
        }
        tuples.add(new TupleLong(prev, cur, 0));

        for(int i = 0; i < tuples.size(); i++){
            //Log.e("TUPLE", Long.toString(tuples.get(i).getPrev()) + " -> " + Long.toString(tuples.get(i).getCur()) + " -> " + Long.toString(tuples.get(i).getNext()));
            TupleLong t = tuples.get(i);
            this.naviTexts.add(getNavigateText(t.getPrev(), t.getCur(), t.getNext()));
        }

    }

    private NavigateText getNavigateText(long p, long c, long n){
        for(int i = 0; i < texts.size(); i++){
            NavigateText tmp = texts.get(i);
            if(tmp.getPrevNodeID() == p && tmp.getCurNodeID() == c && tmp.getNextNodeID() == n){
                return tmp;
            }
        }
        return null;
    }



    // 현재 어느 Edge 위에 있는가?
    public AdjacentEdge whichEdgeUserOn(double lat, double lng){

        double avgDistance = DefaultValue.INFINITE_DISTANCE_DOUBLE_VALUE;
        AdjacentEdge ret = null;

        for(int i = 0; i < this.allEdges.size(); i++){
            AdjacentEdge e = this.allEdges.getEdge(i);
            NodeItem n1 = e.getNodes()[0];
            NodeItem n2 = e.getNodes()[1];

            double distN1 = ValueConverter.distance(n1.getNodeLatitude(), lat, n1.getNodeLongitude(), lng);
            distN1 /= 2D;
            double distN2 = ValueConverter.distance(n2.getNodeLatitude(), lat, n2.getNodeLongitude(), lng);
            distN2 /= 2D;

            double avgDist = distN1 + distN2;
            if(avgDist <= avgDistance){
                ret = e;
                avgDistance = avgDist;
            }
        }

        return ret;
    }

    // 현재 사용자가 설정된 경로 위에 있는가?
    public boolean isUserOnPath(AdjacentEdge user, double lat, double lng){
        AdjacentEdge e = user;
        Log.e("IS_ON_PATH", Long.toString(e.getNodes()[0].getNodeID()) + " - " + Long.toString(e.getNodes()[1].getNodeID()));
        for(int i = 0; i < this.pathEdges.size(); i++){
            AdjacentEdge tmp = this.pathEdges.getEdge(i);
            if(e.isEqual(tmp)){
                return true;
            }
        }
        return false;
    }

    // whichEdgeUserOn 함수를 이용해서 ptr 설정하고 난 후 처리를 시도해야한다.
    // ptr 이 Edge 를 나타내고, ptr 을 통해서 다음 노드에 도착했는지를 알아낸다.
    // 다음 Node 에 대한 힌트는 passingNodes 를 통해서 접근이 가능함.
    // ptr 의 개념이 모호함. node 기준? edge 기준?

    // 현재 ptr 이 어디인가? -> 현재 ptr 이 목적지에 도착했는가? (line : 102)
    public AdjacentEdge getPtr(AdjacentEdge e){
        for(int i = 1; i < this.passingNodes.size(); i++){
            // i-1 과 i 의 관계를 정의한다.
            // i-1 번 node 는 해당 edge 에서 출발 노드에 해당하고, i 번 node 는 해당 edge 에서 도착 노드에 해당한다.
            // 그리고 return 하는 AdjacentEdge 에 대하여 [0] 은 출발 node 이고, [1] 은 도착 node 이다.
            AdjacentEdge tmp = new AdjacentEdge(this.passingNodes.get(i-1), this.passingNodes.get(i));
            if(tmp.isEqual(e)){
                return tmp;
            }
        }
        return null;
    }

    // 현재 ptr 이 목적지에 도착했는가?
    public boolean isPtrArrivedAtDestination(AdjacentEdge e){
        NodeItem destNode = e.getNodes()[1];
        if(destNode.getNodeID() == this.destNodeID){
            return true;
        }else{
            return false;
        }
    }

    // 현재 Edge 에서 상태가 어떤 상태인가?
    // 현재 좌표를 기준으로 상태를 어떻게 체크할 것인가??
    public int getStatusOnEdge(AdjacentEdge e, double lat, double lng){
        // e 의 getNodes() 를 기준으로, [0] 은 출발 노드이고, [1] 은 도착 노드이다.
        // 그렇다면, 출발, 내 위치, 도착 3점 사이의 경우를 생각할 수 있다.
        // 출발점이 A, 내 위치가 B, 도착점이 C 라고 가정하자.
        // 선분 AB 의 길이를 구하고, 선분 BC 의 길이를 구한다.
        // 둘 중의 더 짧은 경우를 구한다.
        NodeItem start = e.getNodes()[0];
        double startLat = start.getNodeLatitude();
        double startLng = start.getNodeLongitude();

        NodeItem dest = e.getNodes()[1];
        double destLat = dest.getNodeLatitude();
        double destLng = dest.getNodeLongitude();

        double startToMine = ValueConverter.distance(startLat, lat, startLng, lng);
        double destToMine = ValueConverter.distance(destLat, lat, destLng, lng);

        if(startToMine < destToMine){
            // 일단 현재 위치가 출발 노드에 인접함
            if(startToMine <= 0.000005D){
                // 출발 노드에 인접한 상태
                return -1;
            }else{
                return 0;
            }
        }else{
            // 일단 현재 위치가 도착 노드에 인접함
            if(destToMine <= 0.000005D){
                // 도착 노드에 인접한 상태
                return 1;
            }else{
                return 0;
            }
        }
    }

    // 경로 재탐색하는 함수
    // 나의 위치 -> 목적지
    public void reFindPathMyLocationToDestination(double lat, double lng){
        NodeItem nearestNode = ValueConverter.getNearestNodeItem(lat, lng);
        long startNodeID = nearestNode.getNodeID();
        findPath(startNodeID, destNodeID);
        printPassingNode();
    }

    public FindPath getPathFinding(){
        return this.pathFinding;
    }

    // 현재 어느 노드에 있는가?
    // required params : NodeItem n
    // 현재 사용자가 위치한 Node 가 경로위에 있지 않으면 null 을 리턴함.
    public NodeItem getNodePtr(NodeItem n){
        for(int i = 0; i < this.passingNodes.size(); i++){
            if(this.passingNodes.get(i).isEqual(n)){
                return this.passingNodes.get(i);
            }
        }
        return null;
    }

    // 현재 위치한 노드가 도착 노드인가?
    // return 0 : 현재 위치한 노드는 진행중인 노드이다.
    // return 1 : 현재 위치한 노드는 도착 노드이다.
    public int getPtrStatus(NodeItem n){
        NodeItem destNode = this.passingNodes.get(this.passingNodes.size() - 1);
        if(destNode.isEqual(n)){
            return 1;
        }else{
            return 0;
        }
    }

    // ptr 변수를 따로 만들어냄
    // 그리고 get, set 함수도 따로 만들어버림
    public void setPtr(NodeItem n){
        this.ptr = n;
    }

    // method overriding
    public void setPtr(double lat, double lng){
        this.ptr = ValueConverter.getNearestNodeItem(lat, lng);
    }

    public String getNavigateText(){
        for(int i = 0 ; i < naviTexts.size(); i++){
            if(ptr.getNodeID() == naviTexts.get(i).getCurNodeID()){
                return naviTexts.get(i).getText();
            }
        }
        return "오류가 발생하였습니다.";
    }

    public boolean isPtrOnPath(){
        for(int i = 0; i < passingNodes.size(); i++){
            long tmp = passingNodes.get(i).getNodeID();
            if(tmp == ptr.getNodeID()){
                return true;
            }

        }
        return false;
    }

    public NodeItem getPtr() {
        return this.ptr;
    }

    public NodeItem getNextNode(){
        int idx = -1;
        for(int i = 0; i < this.passingNodes.size(); i++){
            if(this.ptr.isEqual(this.passingNodes.get(i))){
                idx = i;
                break;
            }
        }
        if(idx == -1){
            return null;
        }
        Log.e("GET_NEXT", Integer.toString(idx) + " , " + Integer.toString(this.passingNodes.size()));
        if(idx == this.passingNodes.size() - 1){
            return null;
        }
        return this.passingNodes.get(idx + 1);
    }

    public boolean isPtrArrived(){
        if(this.ptr.isEqual(this.passingNodes.get(this.passingNodes.size() - 1))){
            return true;
        }else{
            return false;
        }
    }

    // for Log
    private void printPassingNode(){
        String logText = "";
        for(int i = 0; i < this.passingNodes.size(); i++){
            logText += Long.toString(this.passingNodes.get(i).getNodeID());
            if(i != this.passingNodes.size() - 1){
                logText += " -> ";
            }
        }
        Log.e("REFIND", logText);
    }

    public ArrayList<NavigateText> getNaviTexts() {
        return naviTexts;
    }
}
