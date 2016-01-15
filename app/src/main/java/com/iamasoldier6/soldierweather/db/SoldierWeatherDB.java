package com.iamasoldier6.soldierweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iamasoldier6.soldierweather.model.City;
import com.iamasoldier6.soldierweather.model.District;
import com.iamasoldier6.soldierweather.model.Province;
import com.iamasoldier6.soldierweather.utility.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iamasoldier6 on 1/15/16.
 */
public class SoldierWeatherDB {
    private static final String DB_NAME = "cool_weather";
    private static final int VERSION = 1;
    private static SoldierWeatherDB soldierWeatherDB;
    private SQLiteDatabase db;

    private SoldierWeatherDB(Context context) {
        SoldierWeatherOpenHelper dbHelper = new SoldierWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static SoldierWeatherDB getInstance(Context context) {
        if (soldierWeatherDB == null) {
            soldierWeatherDB = new SoldierWeatherDB(context);
        }
        return soldierWeatherDB;
    }

    public synchronized void saveProvince(Province province) {
        ContentValues values = new ContentValues();
        values.put("province_name", province.getProvinceName());
        db.insert("Province", null, values);
    }

    public List<Province> loadProvinces() {
        List<Province> lists = new ArrayList<>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                lists.add(province);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return lists;
    }

    public synchronized void saveCity(City city) {
        ContentValues values = new ContentValues();
        values.put("city_name", city.getCityName());
        values.put("province_id", city.getProvinceId());
        db.insert("City", null, values);
        LogUtil.log("CoolWeatherDB", "city_id = " + city.getId() +
                "\tcity_name = " + city.getCityName() +
                "\tprovince_id = " + city.getProvinceId(), LogUtil.NOTHING);
    }

    public List<City> loadCities(int provinceId) {
        List<City> lists = new ArrayList<>();
        Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                lists.add(city);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return lists;
    }

    public synchronized void saveDistrict(District district) {
        ContentValues values = new ContentValues();
        values.put("district_name", district.getDistrictName());
        values.put("city_id", district.getCityId());
        db.insert("District", null, values);
        LogUtil.log("CoolWeatherDB", "#################\ndistrict_id = " + district.getId() +
                "\ndistrict_name = " + district.getDistrictName() +
                "\ncity_id = " + district.getCityId(), LogUtil.NOTHING);
    }

    public List<District> loadDistricts(int cityId) {
        List<District> lists = new ArrayList<>();
        Cursor cursor = db.query("District", null, "city_id = ?", new String[]{String.valueOf(cityId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                District district = new District();
                district.setId(cursor.getInt(cursor.getColumnIndex("id")));
                district.setDistrictName(cursor.getString(cursor.getColumnIndex("district_name")));
                district.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                lists.add(district);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return lists;
    }
}

