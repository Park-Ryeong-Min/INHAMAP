package com.example.inhamap.Commons;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.inhamap.Components.NodeImageButton;
import com.example.inhamap.Components.TestDrawingView;
import com.example.inhamap.Models.NodeItem;
import com.example.inhamap.Models.VoicePathElement;
import com.example.inhamap.PathFindings.PassingNodeListMaker;
import com.example.inhamap.Utils.JSONFileParser;
import com.example.inhamap.Utils.NodeListMaker;
import com.example.inhamap.Utils.ValueConverter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myown on 2018. 4. 18..
 */

public class GlobalApplication extends Application implements LocationListener{

    public static double myLocationLatitude;
    public static double myLocationLongitude;
    public static float myLocationLeft;
    public static float myLocationTop;
    public static long myLocationNodeID;
    public static TestDrawingView view;
    public static boolean navigationLock;

    public static ArrayList<NodeItem> items;

    public LocationManager locationManager;

    // 어플리케이션이 최초 실행되면 호출되는 함수
    // 사용 전 AndroidManifest.xml 에 등록해서 (android:name) 사용해야함.
    private Socket mSocket;
    {
        try{
            mSocket = IO.socket(URL.SERVER_URL);
            mSocket.connect();
        }catch (URISyntaxException e){
            e.printStackTrace();
        }
        Log.d("SOCKET_CONNECTION", "socket connected");
    }

    public Socket getSocket(){
        return mSocket;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("GLOBAL", "Location changed.");
        location = getBestLocation(locationManager);
        myLocationLatitude = location.getLatitude();
        myLocationLongitude = location.getLongitude();
        //

        double d = DefaultValue.INFINITE_DISTANCE_DOUBLE_VALUE;
        int left = -1;
        int top = -1;
        long id = -1;

        for(int i = 0; i < items.size(); i++){
            double nodeLat = items.get(i).getNodeLatitude();
            double nodeLng = items.get(i).getNodeLongitude();

            double dist = ValueConverter.distance(myLocationLatitude, nodeLat, myLocationLongitude, nodeLng);
            if(d >= dist){
                d = dist;
                left = items.get(i).getMarginLeft();
                top = items.get(i).getMarginTop();
                id = items.get(i).getNodeID();
            }
        }

        myLocationLeft = (float) left;
        myLocationTop = (float) top;
        if(id != -1){
            myLocationNodeID = id;
        }
        if(view != null){
            Toast.makeText(getApplicationContext(), "Location Changed." + Float.toString(myLocationLeft) + " , " + Float.toString(myLocationTop), Toast.LENGTH_LONG).show();
            //Log.e("CHANGE", Float.toString(myLocationLeft) + " , " + Float.toString(myLocationTop));
            view.drawLocation(myLocationLeft, myLocationTop);
        }
        navigationLock = true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e("GLOBAL", "Status changed.");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e("GLOBAL", "Provider enabled.");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e("GLOBAL", "Provider disabled.");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("GLOBAL", "Global Application is on created.");
        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Location myLocation;
        try{
            myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 0.3f, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 30f, this);
        }catch (SecurityException ex){
            ex.printStackTrace();
        }
        JSONObject json = new JSONFileParser(getApplicationContext(), "node_data").getJSON();
        this.items = new NodeListMaker(json).getItems();
        myLocationNodeID = 0;
    }

    private Location getBestLocation(LocationManager lm){
        List<String> providers = lm.getProviders(true);
        Location bestLocation = null;
        for(String provider : providers){
            try {
                Location l = lm.getLastKnownLocation(provider);
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

}
