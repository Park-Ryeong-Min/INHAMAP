package com.example.inhamap.PathFindings;

public class FindDirectionAngle {

    private double criteriaLatitude;
    private double criteriaLongitude;

    public FindDirectionAngle(double cLat, double cLng){
        this.criteriaLatitude = cLat;
        this.criteriaLongitude = cLng;
    }

    public int getDirectionStatus(double lat, double lng){
        // return 0 : 뒤 쪽
        // return 1 : 앞 쪽
        // return 2 : 왼쪽
        // return 3 : 오른쪽

        return 0;
    }
}
