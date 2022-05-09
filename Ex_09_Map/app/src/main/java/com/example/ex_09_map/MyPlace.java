package com.example.ex_09_map;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class MyPlace {
    String title;
    LatLng latLng;
    double latitude, longitute;
    int color;

    double [] arPos;

    MyPlace(String title, double latitude, double longitute, int color){
        this.title = title;
        this.latLng = new LatLng(latitude, longitute);
        this.latitude = latitude;
        this.longitute = longitute;
        this.color = color;
    }

    int rate = 5;
    void setArPosition(Location currentLoaction, float [] mePos){
        arPos = new double[]{
                (mePos[0] + currentLoaction.getLongitude() - longitute) * rate,
                (mePos[1] + currentLoaction.getLatitude() - latitude) * rate,
                mePos[2]
        };
    }
}
