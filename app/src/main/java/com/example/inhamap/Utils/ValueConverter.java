package com.example.inhamap.Utils;

import android.util.Log;

import com.example.inhamap.Commons.DefaultValue;

public class ValueConverter {

    public ValueConverter(){
        // default constructor
    }

    public static float[] latlngToDip(double lat, double lng){
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
    }
}
