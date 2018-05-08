package com.example.inhamap.Commons;

/**
 * Created by myown on 2018. 4. 28..
 */

public class DefaultValue {
    public final static double INFINITE_DISTANCE_DOUBLE_VALUE = Double.MAX_VALUE - 1f;

    public final static double LEFT_TOP_GPS_LATITUDE = 37.4538256039D;
    public final static double LEFT_TOP_GPS_LONGITUDE = 126.6507463529D;
    public final static double RIGHT_TOP_GPS_LATITUDE = 37.4507439766D;
    public final static double RIGHT_TOP_GPS_LONGITUDE = 126.6580198332D;
    public final static double LEFT_BOTTOM_GPS_LATITUDE = 37.4495520677D;
    public final static double LEFT_BOTTOM_GPS_LONGITUDE = 126.6478938236D;
    public final static double RIGHT_BOTTOM_GPS_LATITUDE = 37.4465061990D;
    public final static double RIGHT_BOTTOM_GPS_LONGITUDE = 126.6552527993D;

    public final static int MAP_WIDTH = 1458 / 2;
    public final static int MAP_HEIGHT = 1074 / 2;

    public final static double DISTANCE_ON_MAP_LEFT_TO_RIGHT = Math.sqrt(
            (LEFT_TOP_GPS_LATITUDE - RIGHT_TOP_GPS_LATITUDE)*(LEFT_TOP_GPS_LATITUDE - RIGHT_TOP_GPS_LATITUDE)
            + (LEFT_TOP_GPS_LONGITUDE - RIGHT_TOP_GPS_LONGITUDE)*(LEFT_TOP_GPS_LONGITUDE - RIGHT_TOP_GPS_LONGITUDE));
    public final static double DISTANCE_ON_MAP_TOP_TO_BOTTOM = Math.sqrt(
            (LEFT_TOP_GPS_LATITUDE - LEFT_BOTTOM_GPS_LATITUDE)*(LEFT_TOP_GPS_LATITUDE - LEFT_BOTTOM_GPS_LATITUDE)
            + (LEFT_TOP_GPS_LONGITUDE - LEFT_BOTTOM_GPS_LONGITUDE)*(LEFT_TOP_GPS_LONGITUDE - LEFT_BOTTOM_GPS_LONGITUDE));

    public final static double STANDARD_LEFT_DOUBLE = 6.3567146f;
    public final static double STANDARD_TOP_DOUBLE = 0.67844737f;
    public final static float STANDARD_LEFT_DIP = 1167f;
    public final static float STANDARD_TOP_DIP = 50f;

    public final double DIVISOR_X_LATITUDE = 0.000004227197f;
    public final double DIVISOR_Y_LONGITUDE = 0.0000033670891f;
}
