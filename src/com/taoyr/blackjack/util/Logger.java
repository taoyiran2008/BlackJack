package com.taoyr.blackjack.util;

import android.util.Log;

public class Logger {
    private static final boolean LOG_DEBUG = true;
    private static final boolean LOG_ERROR = true;
    private static final String TAG = "BlackJack";
    
    public static void logDebug(String msg) {
        if (LOG_DEBUG) {
            Log.v(TAG, msg);
        }
    }
    
    public static void logError(String msg) {
        if (LOG_ERROR) {
            Log.e(TAG, msg);
        }
    }
}
