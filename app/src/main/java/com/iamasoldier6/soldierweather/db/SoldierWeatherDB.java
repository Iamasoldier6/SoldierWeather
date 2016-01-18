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

    /**
     * 数据库名
     */
    private static final String DB_NAME = "soldier_weather";

    /**
     * 数据库版本
     */
    private static final int VERSION = 1;

    private static SoldierWeatherDB soldierWeatherDB;

    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     *
     * @param context
     */
    private SoldierWeatherDB(Context context) {
        SoldierWeatherOpenHelper dbHelper = new SoldierWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取SoldierWeatherDB的实例
     *
     * @param context
     * @return
     */
    public synchronized static SoldierWeatherDB getInstance(Context context) {
        if (soldierWeatherDB == null) {
            soldierWeatherDB = new SoldierWeatherDB(context);
        }
        return soldierWeatherDB;
    }

    /**
     * 将Province实例存储到数据库
     *
     * @param province
     */
    public synchronized void saveProvince(Province province) {
        ContentValues values = new ContentValues();
        values.put("province_name", province.getProvinceName());
        db.insert("Province", null, values);
    }

    /**
     * 从数据库读取全国所有的省份信息
     *
     * @return
     */
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     * 将City实例存储到数据库
     *
     * @param city
     */
    public synchronized void saveCity(City city) {
        ContentValues values = new ContentValues();
        values.put("city_name", city.getCityName());
        values.put("province_id", city.getProvinceId());
        db.insert("City", null, values);
        LogUtil.log("SoldierWeatherDB", "city_id = " + city.getId() +
                "\tcity_name = " + city.getCityName() +
                "\tprovince_id = " + city.getProvinceId(), LogUtil.NOTHING);
    }

    /**
     * 从数据库读取某省下所有的城市信息
     *
     * @param provinceId
     * @return
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(city);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     * 将County实例存储到数据库
     *
     * @param district
     */
    public synchronized void saveDistrict(District district) {
        ContentValues values = new ContentValues();
        values.put("district_name", district.getDistrictName());
        values.put("city_id", district.getCityId());
        db.insert("District", null, values);
        LogUtil.log("CoolWeatherDB", "#################\ndistrict_id = " + district.getId() +
                "\ndistrict_name = " + district.getDistrictName() +
                "\ncity_id = " + district.getCityId(), LogUtil.NOTHING);
    }

    /**
     * 从数据库读取某城市下所有的县信息
     *
     * @param cityId
     * @return
     */
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

