package com.iamasoldier6.soldierweather.utility;

import java.io.InputStream;

/**
 * Created by Iamasoldier6 on 1/15/16.
 */
public interface HttpCallbackListener {
    void onFinish(InputStream in);

    void onError(Exception e);
}
