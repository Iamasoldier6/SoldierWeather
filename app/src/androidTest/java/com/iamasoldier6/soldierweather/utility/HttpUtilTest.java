package com.iamasoldier6.soldierweather.utility;

import com.iamasoldier6.soldierweather.db.SoldierWeatherDB;

import junit.framework.TestCase;

import java.io.InputStream;

/**
 * Created by Iamasoldier6 on 1/18/16.
 */
public class HttpUtilTest extends TestCase implements HttpCallbackListener {

    public void testSendHttpRequest() throws Exception {
        HttpUtil.sendHttpRequest("http://v.juhe.cn/weather/citys?key=af2af1996d54696346d66504710ddcf5", this);
    }

    @Override
    public void onFinish(InputStream in) {
        LogUtil.log("HttpUtilTest", "onFinish");
        Utility.handleResponse(SoldierWeatherDB.getInstance(MyApplication.getContext()), in);
    }

    @Override
    public void onError(Exception e) {
        LogUtil.log("HttpUtilTest", "Error");
    }
}
