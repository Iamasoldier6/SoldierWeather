package com.iamasoldier6.soldierweather.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.JsonReader;

import com.iamasoldier6.soldierweather.db.SoldierWeatherDB;
import com.iamasoldier6.soldierweather.model.City;
import com.iamasoldier6.soldierweather.model.District;
import com.iamasoldier6.soldierweather.model.Province;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iamasoldier6 on 1/15/16.
 */
public class Utility {
    private static SoldierWeatherDB soldierWeatherDB;

    public static boolean handleResponse(SoldierWeatherDB soldierWeatherDb, InputStream in) {
        LogUtil.log("Utility", "handleResponse", LogUtil.DEBUG);
        soldierWeatherDB = soldierWeatherDb;
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        boolean flag = false;
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String nodeName = reader.nextName();
                if (nodeName.equals("resultcode")) {
                    LogUtil.log("Utility", "resultcode = " + reader.nextString(), LogUtil.NOTHING);
                    flag = true;
                } else if (nodeName.equals("result") && flag) {
                    saveAreaToDatabase(reader);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean saveAreaToDatabase(JsonReader reader) {
        LogUtil.log("Utility", "saveAreaToDatabase", LogUtil.NOTHING);
        String provinceName = null;
        String cityName = null;
        String districtName = null;
        List<String> provinceNames = new ArrayList<>();
        List<String> cityNames = new ArrayList<>();
        boolean changedProvince = false;
        boolean changedCity = false;
        int provinceId = 0;
        int cityId = 0;
        int districtId = 0;
        Province previousProvince = new Province();
        City previousCity = new City();

        try {
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String nodeName = reader.nextName();
                    if (nodeName.equals("province")) {
                        provinceName = reader.nextString().trim();
                        if (!provinceNames.contains(provinceName)) {
                            provinceNames.add(provinceName);
                            changedProvince = true;
                            provinceId++;
                        }
                    } else if (nodeName.equals("city")) {
                        cityName = reader.nextString().trim();
                        if (!cityNames.contains(cityName)) {
                            cityNames.add(cityName);
                            changedCity = true;
                            cityId++;
                            LogUtil.log("Utility", "********changedCity = " + changedCity, LogUtil.NOTHING);
                        }
                    } else if (nodeName.equals("district")) {
                        districtName = reader.nextString().trim();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
                LogUtil.log("Utility", /*"id = " + id + */"\nprovince_name = " + provinceName +
                        "\ncity_name = " + cityName + "\ndistrict_name = " + districtName, LogUtil.NOTHING);

                if (changedProvince) {
                    Province province = new Province();
                    province.setId(provinceId);
                    province.setProvinceName(provinceName);
                    previousProvince = province;
                    soldierWeatherDB.saveProvince(province);
                    changedProvince = false;
                    LogUtil.log("Utility", "province_id = " + province.getId() +
                            "\tprovince_name = " + province.getProvinceName(), LogUtil.NOTHING);
                }

                if (changedCity) {
                    LogUtil.log("Utility", "########changedCity = " + changedCity, LogUtil.NOTHING);
                    City city = new City();
                    city.setId(cityId);
                    city.setCityName(cityName);
                    city.setProvinceId(previousProvince.getId());
                    previousCity = city;
                    soldierWeatherDB.saveCity(city);
                    changedCity = false;
                    LogUtil.log("Utility", "city_id = " + city.getId() +
                            "\tcity_name = " + city.getCityName() +
                            "\tprovince_id = " + city.getProvinceId(), LogUtil.NOTHING);
                }

                District district = new District();
                districtId++;
                district.setId(districtId);
                district.setDistrictName(districtName);
                district.setCityId(previousCity.getId());
                soldierWeatherDB.saveDistrict(district);
                LogUtil.log("Utility", "district_id = " + district.getId() +
                        "\tdistrict_name = " + district.getDistrictName() +
                        "\tcity_id = " + district.getCityId(), LogUtil.NOTHING);
            }
            reader.endArray();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean handleWeatherResponse(Context context, InputStream in) {
        LogUtil.log("Utility", "handleWeatherResponse", LogUtil.NOTHING);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        StringBuilder response = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            LogUtil.log("Utility", "response = " + response.toString(), LogUtil.NOTHING);
            return parseWeatherInfo(context, response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean parseWeatherInfo(Context context, String data) {
        LogUtil.log("Utility", "parseWeatherInfo", LogUtil.NOTHING);
        try {
            JSONObject response = new JSONObject(data);
            String resultCode = response.getString("resultcode");
            LogUtil.log("Utility", "resultcode = " + resultCode, LogUtil.NOTHING);
            if (resultCode.equals("200")) {
                JSONObject result = response.getJSONObject("result");
                JSONObject today = result.getJSONObject("today");
                String temperature = today.getString("temperature");
                String cityName = today.getString("city");
                String weather = today.getString("weather");
                String date = today.getString("date_y");
                LogUtil.log("Utility", "\ntemperature = " + temperature +
                        "\ncityName = " + cityName + "\nweather = " + weather + "\ndate = " + date, LogUtil.DEBUG);
                return saveWeatherInfo(context, cityName, weather, temperature, date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean saveWeatherInfo(Context context, String cityName, String weather,
                                           String temperature, String date) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather", weather);
        editor.putString("temperature", temperature);
        editor.putString("date", date);
        editor.commit();
        return false;
    }
}

