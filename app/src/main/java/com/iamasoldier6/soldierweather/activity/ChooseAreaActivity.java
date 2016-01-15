package com.iamasoldier6.soldierweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iamasoldier6.soldierweather.R;
import com.iamasoldier6.soldierweather.db.SoldierWeatherDB;
import com.iamasoldier6.soldierweather.model.City;
import com.iamasoldier6.soldierweather.model.District;
import com.iamasoldier6.soldierweather.model.Province;
import com.iamasoldier6.soldierweather.utility.HttpCallbackListener;
import com.iamasoldier6.soldierweather.utility.HttpUtil;
import com.iamasoldier6.soldierweather.utility.MyApplication;
import com.iamasoldier6.soldierweather.utility.Utility;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iamasoldier6 on 1/15/16.
 */
public class ChooseAreaActivity extends Activity {
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_DISTRICT = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private List<String> lists;
    private ArrayAdapter<String> adapter;
    private SoldierWeatherDB soldierWeatherDB;

    private List<Province> provinceList;
    private List<City> cityList;
    private List<District> districtList;

    private Province selectedProvince;
    private City selectedCity;

    private int currentLevel;

    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("city_selected", false) && !isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.choose_area);

        titleText = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);
        lists = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lists);
        soldierWeatherDB = SoldierWeatherDB.getInstance(this);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(i);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(i);
                    queryDistricts();
                } else if (currentLevel == LEVEL_DISTRICT) {
                    String districtName = districtList.get(i).getDistrictName();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("district_name", districtName);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces() {
        provinceList = soldierWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            lists.clear();
            for (Province province : provinceList) {
                lists.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer("Province");
        }
    }

    private void queryCities() {
        cityList = soldierWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            lists.clear();
            for (City city : cityList) {
                lists.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer("City");
        }
    }

    private void queryDistricts() {
        districtList = soldierWeatherDB.loadDistricts(selectedCity.getId());
        if (districtList.size() > 0) {
            lists.clear();
            for (District district : districtList) {
                lists.add(district.getDistrictName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_DISTRICT;
        } else {
            queryFromServer("District");
        }
    }

    private void queryFromServer(final String type) {
        showProgressDialog();
        HttpUtil.sendHttpRequest("http://v.juhe.cn/weather/citys?key=af2af1996d54696346d66504710ddcf5", new HttpCallbackListener() {
            @Override
            public void onFinish(InputStream in) {
                boolean result = Utility.handleResponse(SoldierWeatherDB.getInstance(MyApplication.getContext()), in);
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if (type.equals("Province")) {
                                queryProvinces();
                            } else if (type.equals("City")) {
                                queryCities();
                            } else if (type.equals("District")) {
                                queryDistricts();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_DISTRICT) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            if (isFromWeatherActivity) {
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}

