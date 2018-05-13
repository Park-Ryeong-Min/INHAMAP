package com.example.inhamap.Commons;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.inhamap.Models.NodeItem;
import com.example.inhamap.Utils.JSONFileParser;
import com.example.inhamap.Utils.NodeListMaker;
import com.example.inhamap.Utils.ValueConverter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by myown on 2018. 4. 18..
 */

public class GlobalApplication extends Application implements LocationListener{

    public static double myLocationLatitude;
    public static double myLocationLongitude;
    public static float myLocationLeft;
    public static float myLocationTop;

    private ArrayList<NodeItem> items;

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
        myLocationLatitude = location.getLatitude();
        myLocationLongitude = location.getLongitude();
        Toast.makeText(getApplicationContext(), "Location Changed." + Double.toString(myLocationLatitude) + " , " + Double.toString(myLocationLongitude), Toast.LENGTH_LONG).show();

        double d = DefaultValue.INFINITE_DISTANCE_DOUBLE_VALUE;
        int left = -1;
        int top = -1;

        for(int i = 0; i < items.size(); i++){
            double nodeLat = items.get(i).getNodeLatitude();
            double nodeLng = items.get(i).getNodeLongitude();

            double dist = ValueConverter.distance(myLocationLatitude, nodeLat, myLocationLongitude, nodeLng);
            if(d >= dist){
                d = dist;
                left = items.get(i).getMarginLeft();
                top = items.get(i).getMarginTop();
            }
        }

        myLocationLeft = (float) left;
        myLocationTop = (float) top;
        //Log.e("GLOBAL", Float.toString(myLocationLeft) + " , " + Float.toString(myLocationTop));
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
        LocationManager locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Location myLocation;
        try{
            myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 0.3f, this);
        }catch (SecurityException ex){
            ex.printStackTrace();
        }
        JSONObject json = new JSONFileParser(getApplicationContext(), "node_data").getJSON();
        this.items = new NodeListMaker(json).getItems();
    }
}
