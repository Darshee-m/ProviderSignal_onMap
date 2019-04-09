package com.example.strengthbutton;

public class Data {
    String currentTime;
    double lat;
    double longi;
    String provider;
    int strength;

    public Data(String currentTime, double lat, double longi, String provider, int strength) {
        this.currentTime= currentTime;
        this.lat = lat;
        this.longi = longi;
        this.provider = provider;
        this.strength = strength;
    }

    public Data() {
    }
}
