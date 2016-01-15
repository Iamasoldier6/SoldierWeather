package com.iamasoldier6.soldierweather.model;

/**
 * Created by Iamasoldier6 on 1/15/16.
 */
public class District {
    private int id;
    private String districtName;
    private int cityId;

    public int getId() {
        return id;
    }

    public String getDistrictName() {
        return districtName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDistrictName(String name) {
        districtName = name;
    }

    public void setCityId(int id) {
        cityId = id;
    }
}

