package com.example.inhamap.Threads;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.example.inhamap.Activities.MainActivity;
import com.example.inhamap.Commons.GlobalApplication;
import com.example.inhamap.Models.AdjacentEdge;
import com.example.inhamap.Models.EdgeList;
import com.example.inhamap.Models.NodeItem;
import com.example.inhamap.Models.VoicePathElement;
import com.example.inhamap.PathFindings.FindPath;
import com.example.inhamap.PathFindings.GuideOutput;
import com.example.inhamap.PathFindings.GuidePath;
import com.example.inhamap.PathFindings.NavigatePath;
import com.example.inhamap.PathFindings.PassingNodeListMaker;
import com.example.inhamap.Utils.TextSpeaker;
import com.example.inhamap.Utils.ValueConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VoiceNavigatingThread extends Thread {

    private Context context;
    private ArrayList<NodeItem> nodes; // passing nodes
    private EdgeList edges;
    private LocationManager locationManager;
    private ArrayList<VoicePathElement> voice;

    private double lat;
    private double lng;
    private String provider;
    private long destNodeID;
    private long startNodeID;
    private boolean flag;
    private TextToSpeech tts;

    public VoiceNavigatingThread(Context context){
        // default constructor
        this.context = context;
        init();
    }

    public VoiceNavigatingThread(Context context, long start, long dest){
        this.context = context;
        this.startNodeID = start;
        this.destNodeID = dest;
        init();
    }

    public VoiceNavigatingThread(Context context, TextToSpeech tts, long start, long dest){
        this.context = context;
        this.startNodeID = start;
        this.destNodeID = dest;
        this.tts = tts;
        init();
    }

    public VoiceNavigatingThread(Context context, ArrayList<NodeItem> node, EdgeList edge, ArrayList<VoicePathElement> voice, long start, long dest){
        this.context = context;
        this.nodes = node;
        this.edges = edge;
        this.startNodeID = start;
        this.destNodeID = dest;
        init();
    }

    public VoiceNavigatingThread(Context context, ArrayList<NodeItem> node, EdgeList edge, long dest){

    }

    private void init(){
        this.locationManager = (LocationManager)this.context.getSystemService(Context.LOCATION_SERVICE);
        lat = 0.0D;
        lng = 0.0D;
        this.provider = "gps";
        this.flag = false;
        try {
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.3f, listener);
        }catch (SecurityException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        //super.run();
        // 먼저 도착 노드의 ID를 얻어낸다.
        // 도착 노드의 ID는 생성자로부터 받아낸다.

        // 리스트를 돌린다
        // -> 언제까지? -> isArrivalDestination 값이 true 일 때까지
        //GuidePath guide = new GuidePath(edges, nodes, destNodeID, voice);
        NavigatePath navigate = new NavigatePath(startNodeID, destNodeID);
        boolean finish = true;
        boolean first = true;

        Log.e("THREAD", "Navigation start.");
        while(first) {
            tts.speak("경로 안내를 시작합니다.", TextToSpeech.QUEUE_FLUSH, null);
            while (tts.isSpeaking());
            //new TextSpeaker(context, "경로 안내를 시작합니다.");
            first = false;
        }


        while(finish){
            /*
            if(tts == null){
                Log.e("TTS", "tts object is null.");
            }
            if(first){
                tts.speak("경로 안내를 시작합니다.", TextToSpeech.QUEUE_FLUSH, null);
                while(tts.isSpeaking()){
                    Log.e("TTS", "TTS is speaking");
                }
                first = false;
            }
            */

            // 현재 위치를 검색하면 3가지 경우가 나온다.
            // case 1: 출발 위치와 나의 위치가 근접한 경우 (출발)
            // -> 어떻게 가야하는 지에 대해서 음성 안내를 실시함
            // case 2: 도착 위치와 나의 위치가 근접한 경우 (도착)
            // -> 다음 Edge 로 값을 이동함
            // case 3: 경우 1과 경우 2 둘 중에 어느 것도 해당되지 않는 경우
            // -> 일단 기다린다 + 경로 이탈 등의 상황에 대해서 검출하고 핸들링한다.
            //Log.e("GPS", Double.toString(lat) + " , " + Double.toString(lng));
            //Log.e("GPS", Double.toString(GlobalApplication.myLocationLatitude) + " , " + Double.toString(GlobalApplication.myLocationLongitude));

            boolean endFlag = false;
            while (GlobalApplication.navigationLock){
                Log.e("EVENT", "Find event");
                double myLat = GlobalApplication.myLocationLatitude;
                double myLng = GlobalApplication.myLocationLongitude;

                navigate.setPtr(myLat, myLng);
                if(navigate.getNodePtr(navigate.getPtr()) != null){
                    // 헌재 위치한 노드가 경로에 있는 거임
                    NodeItem next = navigate.getNextNode();
                    if(next == null) {
                        // 다음 노드가 없는 경우?
                        // 다음 노드가 null 이면
                        if(navigate.isPtrArrived()){
                            // 도착했음.
                            tts.speak("목적지에 도착하였습니다. 안내를 종료합니다.", TextToSpeech.QUEUE_FLUSH, null);
                            while(tts.isSpeaking());
                            endFlag = true;
                        }else{
                            tts.speak("경로 탐색 중 문제가 발생하였습니다.", TextToSpeech.QUEUE_FLUSH, null);
                            while(tts.isSpeaking());
                        }
                    }else{
                        tts.speak("전방으로 진행하십시오.", TextToSpeech.QUEUE_FLUSH, null);
                        while(tts.isSpeaking());
                    }
                }else{
                    // 현재 위치한 노드가 경로에 없는 거임
                    tts.speak("경로를 이탈하였습니다. 경로를 재탐색합니다.", TextToSpeech.QUEUE_FLUSH, null);
                    while(tts.isSpeaking());
                    navigate.reFindPathMyLocationToDestination(myLat, myLng);
                }
                GlobalApplication.navigationLock = false;
            }

            if(endFlag){
                break;
            }
            //Log.d("THREAD", "Infinity loop test.");
        }
    }

    private final LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lng = location.getLongitude();
            lat = location.getLatitude();
            flag = true;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("THREAD", "status changed. " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("THREAD", "provider enabled. " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private Location getBestLocation(){
        List<String> providers = this.locationManager.getProviders(true);
        Location bestLocation = null;
        for(String provider : providers){
            try {
                Location l = this.locationManager.getLastKnownLocation(provider);
                if(l == null){
                    continue;
                }
                if(bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()){
                    bestLocation = l;
                }
            }catch (SecurityException ex){
                ex.printStackTrace();
            }
        }
        return bestLocation;
    }

    private boolean isArrivalDestination(long dest){
        Location tmp = getBestLocation();
        double lat = tmp.getLatitude();
        double lng = tmp.getLongitude();
        long id = ValueConverter.getNearestNodeItem(lat, lng).getNodeID();
        if(id == dest){
            return true;
        }else{
            return false;
        }
    }
}
