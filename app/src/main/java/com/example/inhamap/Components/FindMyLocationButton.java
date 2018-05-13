package com.example.inhamap.Components;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.inhamap.Models.NodeItem;
import com.example.inhamap.Utils.JSONFileParser;
import com.example.inhamap.Utils.NodeListMaker;
import com.example.inhamap.Utils.ValueConverter;

import org.json.JSONObject;

import java.util.ArrayList;

public class FindMyLocationButton extends AppCompatButton implements View.OnClickListener, LocationListener{

    private TestDrawingView drawingView;
    private Context context;
    private double latitude;
    private double longitude;
    private ArrayList<NodeItem> items;

    public FindMyLocationButton(Context context){
        super(context);
        init(context);
    }

    public FindMyLocationButton(Context context, AttributeSet attr){
        super(context, attr);
        init(context);
    }

    public void setDrawingView(TestDrawingView view){
        this.drawingView = view;
    }

    private void init(Context context){
        this.setOnClickListener(this);
        this.context = context;
        JSONObject json = new JSONFileParser(this.context, "node_data").getJSON();
        this.items = new NodeListMaker(json).getItems();
    }

    @Override
    public void onClick(View v) {
        Log.e("BUTTON_CLICK", "Find my Location");
        LocationManager locationManager = (LocationManager)this.context.getSystemService(Context.LOCATION_SERVICE);
        try{
            Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(myLocation != null){
                this.latitude = myLocation.getLatitude();
                this.longitude = myLocation.getLongitude();
                float[] pos = ValueConverter.latlngToDip(this.latitude, this.longitude, items);
                drawingView.drawLocation(pos[0], pos[1]);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10, 100f, this);
        }catch (SecurityException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("LOCATION_MANAGER", "Location changed.");
        Toast.makeText(this.context, "Location changed.", Toast.LENGTH_LONG).show();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        float[] pos = ValueConverter.latlngToDip(this.latitude, this.longitude, items);
        Log.e("DIP", Float.toString(pos[0]) + " , " + Float.toString(pos[1]));
        drawingView.drawLocation(pos[0], pos[1]);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e("LOCATION_MANAGER", "Status changed.");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e("LOCATION_PROVIDER", "Provider enabled.");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e("LOCATION_PROVIDER", "Provider disabled.");
    }
}
