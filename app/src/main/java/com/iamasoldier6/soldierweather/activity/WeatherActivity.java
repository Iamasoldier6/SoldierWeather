package com.iamasoldier6.soldierweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.iamasoldier6.soldierweather.R;
import com.iamasoldier6.soldierweather.service.AutoUpdateService;
import com.iamasoldier6.soldierweather.utility.HttpCallbackListener;
import com.iamasoldier6.soldierweather.utility.HttpUtil;
import com.iamasoldier6.soldierweather.utility.LogUtil;
import com.iamasoldier6.soldierweather.utility.Utility;

import java.io.InputStream;
import java.net.URLEncoder;

/**
 * Created by Iamasoldier6 on 1/15/16.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {

    private TextView cityNameText;
    private TextView weatherDespText;
    private TextView temperature;
    private TextView currentDate;
    private Button switchCity;
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        cityNameText = (TextView) findViewById(R.id.city_name);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temperature = (TextView) findViewById(R.id.temperature);
        currentDate = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);

        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
        String districtName = getIntent().getStringExtra("district_name");
        LogUtil.log("WeatherActivity", "districtName = " + districtName, LogUtil.NOTHING);
        if (!TextUtils.isEmpty(districtName)) {
            weatherDespText.setText("同步中...");
            queryWeather(districtName);
        } else {
            showWeather();
        }
    }

    private void queryWeather(String name) {
        try {
            String address = "http://v.juhe.cn/weather/index?format=2&cityname=" + URLEncoder.encode(name, "UTF-8") +
                    "&key=af2af1996d54696346d66504710ddcf5";
            HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                @Override
                public void onFinish(InputStream in) {
                    Utility.handleWeatherResponse(WeatherActivity.this, in);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weatherDespText.setText("同步失败");
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        LogUtil.log("WeatherActivity", "cityName = " + prefs.getString("city_name", ""));
        LogUtil.log("WeatherActivity", "temperature = " + prefs.getString("temperature", ""));
        LogUtil.log("WeatherActivity", "weather = " + prefs.getString("weather", ""));
        LogUtil.log("WeatherActivity", "date = " + prefs.getString("date", ""));
        cityNameText.setText(prefs.getString("city_name", ""));
        weatherDespText.setText(prefs.getString("weather", ""));
        temperature.setText(prefs.getString("temperature", ""));
        currentDate.setText(prefs.getString("date", ""));
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                weatherDespText.setText("同步中");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String districtName = preferences.getString("district_name", "");
                queryWeather(districtName);
                break;
            default:
                break;
        }
    }
}

