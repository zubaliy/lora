package com.athome.zubaliy.util;

import android.content.Context;

import com.athome.zubaliy.mylifeontheroad.BuildConfig;

/**
 * Configuration
 * <p/>
 * <p/>
 * Created by zubaliy on 28/10/14.
 */
public class Config {

    /**
     * Initialize
     *
     * @param context the
     */
    public static void init(Context context) {
        Utils.init(context);
    }


    /**
     * Get bluetooth MAC from settings
     *
     * @return MAC
     */
    public static String getBluetoothMac() {
        return Utils.readPreferences(BuildConfig.KEY_DEVICE_MAC_ADDRESS);
    }

    /**
     * Set bluetooth MAC
     *
     * @param mac MAC as string
     */
    public static void setBluetoothMac(String mac) {
        Utils.savePreferences(BuildConfig.KEY_DEVICE_MAC_ADDRESS, mac);
    }
}
