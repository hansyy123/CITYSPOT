package com.example.cityspot;

public class Attraction {
    private String name;
    private String location;
    private String details;
    private int imageResId;
    private double lat;
    private double lon;

    public Attraction(String name, String location, String details, int imageResId, double lat, double lon) {
        this.name = name;
        this.location = location;
        this.details = details;
        this.imageResId = imageResId;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getDetails() { return details; }
    public int getImageResId() { return imageResId; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
}