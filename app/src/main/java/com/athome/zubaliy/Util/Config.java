package com.athome.zubaliy.util;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.athome.zubaliy.service.ActivityTrackingService;
import com.athome.zubaliy.service.ActivityTrackingService_;

/**
 * Created by zubaliy on 28/10/14.
 */
public class Config {
    private static final String TAG = "zConfig";


    public static final boolean DEBUG = true;


    public static void init(Context context) {
        Utils.init(context);
    }

    public static String getBluetoothMac(){
        return Utils.readPreferences(AppKey.DEVICE_MAC_ADDRESS.getKey());
    }

    public static void setBluetoothMac(String mac) {
        Utils.savePreferences(AppKey.DEVICE_MAC_ADDRESS.getKey(), mac);
    }

    public static boolean isMyServiceRunning(Context context) {
        boolean result = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ActivityTrackingService_.class.getCanonicalName().toString().equals(service.service.getClassName())) {

                result = true;
                break;
            }
        }

        if (result) {
            Log.d(TAG, "Service is running");
        } else {
            Log.d(TAG, "Service is not running");
        }
        return result;
    }
}
