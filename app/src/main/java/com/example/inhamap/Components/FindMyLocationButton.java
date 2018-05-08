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

import com.example.inhamap.Utils.ValueConverter;

public class FindMyLocationButton extends AppCompatButton implements View.OnClickListener, LocationListener{

    private TestDrawingView drawingView;
    private Context context;
    private double latitude;
    private double longitude;

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
                drawingView.drawLocation(this.latitude, this.longitude);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10, 100f, this);
        }catch (SecurityException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("LOCATION_MANAGER", "Location changed.");
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        float[] pos = ValueConverter.latlngToDip(this.latitude, this.longitude);
        Log.e("DIP", Float.toString(pos[0]) + " , " + Float.toString(pos[1]));
        drawingView.drawLocation(this.latitude, this.longitude);
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
