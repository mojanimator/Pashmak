package com.app.pashmak.Model;

/**
 * Created by mmr on 21/02/2018.
 */

public class Billboard {
    private long id;
    private double lat;
    private double lng;
    private int county_id;
    private String address;
    private long contractor_id;
    private String status;
    private String imageLink;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getCounty_id() {
        return county_id;
    }

    public void setCounty_id(int county_id) {
        this.county_id = county_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getContractor_id() {
        return contractor_id;
    }

    public void setContractor_id(long contractor_id) {
        this.contractor_id = contractor_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }


}
