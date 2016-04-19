package com.sample.gameday.models;

/**
 * Created by abhi on 18/04/16.
 */
public class Facility {

    private String id;
    private String name;
    private String city;
    private boolean isVerified;
    private Double latitue;
    private Double longitude;


    public Facility(String id, String name, String city, boolean isVerified, Double latitue, Double longitude) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.isVerified = isVerified;
        this.latitue = latitue;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Double getLatitue() {
        return latitue;
    }

    public void setLatitue(Double latitue) {
        this.latitue = latitue;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
