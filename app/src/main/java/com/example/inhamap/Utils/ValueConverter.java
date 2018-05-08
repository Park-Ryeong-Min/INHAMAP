package com.example.inhamap.Utils;

import android.util.Log;

import com.example.inhamap.Commons.DefaultValue;
import com.example.inhamap.Models.NodeItem;

import org.json.JSONObject;

import java.util.ArrayList;

public class ValueConverter {

    public ValueConverter(){
        // default constructor
    }

    public static float[] latlngToDip(double lat, double lng, ArrayList<NodeItem> items){
        /*
        float[] ret = new float[2];
        ret[0] = 0f;
        ret[1] = 0f;

        double divLat = DefaultValue.LEFT_TOP_GPS_LATITUDE - DefaultValue.LEFT_BOTTOM_GPS_LATITUDE;
        divLat /= (double)DefaultValue.MAP_HEIGHT;

        double divLng = DefaultValue.RIGHT_TOP_GPS_LONGITUDE - DefaultValue.LEFT_TOP_GPS_LONGITUDE;
        divLng /= (double)DefaultValue.MAP_WIDTH;

        int w = 0;
        int h = 0;

        double distLat = DefaultValue.INFINITE_DISTANCE_DOUBLE_VALUE;
        double distLng = DefaultValue.INFINITE_DISTANCE_DOUBLE_VALUE;

        for(int i = 0; i < DefaultValue.MAP_WIDTH; i++){
            for(int j = 0; j < DefaultValue.MAP_HEIGHT; j++){

                //Log.e("BEFORE_CALC", Double.toString(DefaultValue.LEFT_TOP_GPS_LATITUDE));
                //Log.e("BEFORE_CALC", Double.toString(DefaultValue.LEFT_TOP_GPS_LONGITUDE));

                double curLat = DefaultValue.LEFT_TOP_GPS_LATITUDE - ((double)j)*divLat;
                double curLng = DefaultValue.LEFT_TOP_GPS_LONGITUDE + ((double)i)*divLng;

                //Log.e("CALC", Double.toString(curLat));
                //Log.e("CALC", Double.toString(curLng));

                double dLat = lat - curLat;
                double dLng = lng - curLng;
                if(dLat < 0){
                    dLat *= -1D;
                }
                if(dLng < 0){
                    dLng *= -1D;
                }
                if(distLat > dLat && distLng > dLng){
                    w = i;
                    h = j;
                    distLat = dLat;
                    distLng = dLng;
                }
            }
        }

        ret[0] = (float)w;
        ret[1] = (float)h;

        return ret;
        */

        double d = DefaultValue.INFINITE_DISTANCE_DOUBLE_VALUE;
        int left = -1;
        int top = -1;

        for(int i = 0; i < items.size(); i++){
            double nodeLat = items.get(i).getNodeLatitude();
            double nodeLng = items.get(i).getNodeLongitude();

            double dist = distance(lat, nodeLat, lng, nodeLng);
            if(d >= dist){
                d = dist;
                left = items.get(i).getMarginLeft();
                top = items.get(i).getMarginTop();
            }
        }

        float[] ret = new float[2];
        ret[0] = (float) left;
        ret[1] = (float) top;
        return ret;
    }

    public static double distance(double lat1, double lat2,
                           double lng1, double lng2){
        double a = (lat1-lat2)*distPerLat(lat1);
        double b = (lng1-lng2)*distPerLng(lat1);
        return Math.sqrt(a*a+b*b);
    }

    private static double distPerLng(double lat){
        return 0.0003121092*Math.pow(lat, 4)
                +0.0101182384*Math.pow(lat, 3)
                -17.2385140059*lat*lat
                +5.5485277537*lat+111301.967182595;
    }

    private static double distPerLat(double lat){
        return -0.000000487305676*Math.pow(lat, 4)
                -0.0033668574*Math.pow(lat, 3)
                +0.4601181791*lat*lat
                -1.4558127346*lat+110579.25662316;
    }
}
