package com.athome.zubaliy.util;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.athome.zubaliy.service.ActivityTrackingService_;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;


/**
 * Created by zubaliy on 28/10/14.
 */
public class Config {
    private static final String TAG = "zConfig";
    public static final boolean DEBUG = true;

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");


    public static final String LINK_UPLOAD_ACTIVITYLOGS_SERVER = "https://open-aroadz.rhcloud" + "" +
            ".com/api/insert/ActivityLogs";
    public static final String LINK_UPLOAD_ACTIVITYLOGS_LOCAL = "http://192.168.1" + "" +
            ".4:8080/open/lifeonroad/api/insert/ActivityLogs";
    public static String LINK_UPLOAD_ACTIVITYLOGS = "default";
    private static String FILE_CONFIG_PROPERTIES = "config.properties";


    public static void init(Context context) {
        Utils.init(context);

        createPropertiesFile(context);

        readPropertiesFile(context);
    }

    public static void readPropertiesFile(Context context) {
        String propertiesPath = context.getFilesDir().getPath().toString() + File.separatorChar +
                FILE_CONFIG_PROPERTIES;


        if (new File(propertiesPath).exists()) {
            Properties properties = new Properties();

            try {
                FileInputStream input = new FileInputStream(propertiesPath);
                properties.load(input);
                LINK_UPLOAD_ACTIVITYLOGS = properties.getProperty(AppKey.CONFIG_FILENAME.getKey());

            } catch (IOException e) {

            }
        }
    }


    public static String getBluetoothMac() {
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

    /**
     * Will create a property file if not exists
     *
     * @param context
     */
    public static void createPropertiesFile(Context context) {
        String propertiesPath = context.getFilesDir().getPath().toString() + File.separatorChar +
                FILE_CONFIG_PROPERTIES;

        if (!new File(propertiesPath).exists()) {

            Properties prop = new Properties();
            try {

                FileOutputStream out = new FileOutputStream(propertiesPath);
                prop.setProperty(AppKey.CONFIG_FILENAME.getKey(), LINK_UPLOAD_ACTIVITYLOGS_LOCAL);
                prop.store(out, null);
                out.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to open file " + FILE_CONFIG_PROPERTIES);
                Log.e(TAG, e.toString());
            }
        }
    }
}
