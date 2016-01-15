package com.iamasoldier6.soldierweather.model;

/**
 * Created by Iamasoldier6 on 1/15/16.
 */
public class City {
    private int id;
    private String cityName;
    private int provinceId;

    public int getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityName(String name) {
        cityName = name;
    }

    public void setProvinceId(int id) {
        provinceId = id;
    }
}

