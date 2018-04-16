package com.app.pashmak.Model;

import java.util.ArrayList;

/**
 * Created by Mojtaba Rajabi on 10/04/2018.
 */

public class ProvinceCounties {
    private String provinceName;
    private ArrayList<County> counties = new ArrayList<>();

    public String getProvinceName() {
        return provinceName;
    }


    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void addCounty(County c) {
        this.counties.add(c);
    }

    public ArrayList<County> getCounties() {
        return counties;
    }

    public County getCounty(int pos) {
        return counties.get(pos);
    }
}
