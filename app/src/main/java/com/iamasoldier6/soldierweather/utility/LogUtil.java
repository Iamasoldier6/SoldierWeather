package com.iamasoldier6.soldierweather.utility;

import android.util.Log;

/**
 * Created by Iamasoldier6 on 1/15/16.
 */
public class LogUtil {

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;

    public static int LEVEL = 0;

    public static void log(String tag, String msg, final int level) {
        if (LEVEL < level) {
            switch (level) {
                case VERBOSE:
                    Log.v(tag, msg);
                    break;
                case DEBUG:
                    Log.d(tag, msg);
                    break;
                case INFO:
                    Log.i(tag, msg);
                    break;
                case WARN:
                    Log.w(tag, msg);
                    break;
                case ERROR:
                    Log.e(tag, msg);
                    break;
                case NOTHING:
                default:
                    break;
            }
        }
    }

    public static void log(String tag, String msg) {
        log(tag, msg, DEBUG);
    }
}
