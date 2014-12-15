package com.athome.zubaliy.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;


/**
 * Configuration
 * <p/>
 * <p/>
 * Created by zubaliy on 28/10/14.
 */
public class Config {
    private static final String TAG = Config.class.getSimpleName();

    // default MongoDB and JavaScript ISO date format
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static final String LINK_UPLOAD_ACTIVITYLOGS_SERVER = "https://lor-aroadz.rhcloud" + "" +
            ".com/lifeonroad/api/insert/ActivityLogs";
    public static final String LINK_UPLOAD_ACTIVITYLOGS_LOCAL = "http://192.168.1" + "" +
            ".4:8080/open/lifeonroad/api/insert/ActivityLogs";
    public static String LINK_UPLOAD_ACTIVITYLOGS = "default";

    private static String FILE_CONFIG_PROPERTIES = "config.properties";


    /**
     * Initialize
     *
     * @param context the
     */
    public static void init(Context context) {
        Utils.init(context);

        createPropertiesFile(context);

        readPropertiesFile(context);
    }

    /**
     * Read properties from properties file
     *
     * @param context the
     */
    public static void readPropertiesFile(Context context) {
        String propertiesPath = context.getFilesDir().getPath().toString() + File.separatorChar +
                FILE_CONFIG_PROPERTIES;

        if (new File(propertiesPath).exists()) {
            Properties properties = new Properties();

            try {
                FileInputStream input = new FileInputStream(propertiesPath); properties.load(input);
                LINK_UPLOAD_ACTIVITYLOGS = properties.getProperty(AppKey.CONFIG_LINK_SERVER_LOCAL.getKey());

            } catch (IOException e) {

            }
        }
    }


    /**
     * Get bluetooth MAC from settings
     *
     * @return MAC
     */
    public static String getBluetoothMac() {
        return Utils.readPreferences(AppKey.DEVICE_MAC_ADDRESS.getKey());
    }

    /**
     * Set bluetooth MAC
     *
     * @param mac MAC as string
     */
    public static void setBluetoothMac(String mac) {
        Utils.savePreferences(AppKey.DEVICE_MAC_ADDRESS.getKey(), mac);
    }

    /**
     * Create a property file if does not exist
     *
     * @param context
     */
    public static void createPropertiesFile(Context context) {
        String propertiesPath = context.getFilesDir().getPath().toString() + File.separatorChar +
                FILE_CONFIG_PROPERTIES;

        if (!new File(propertiesPath).exists()) {

            Properties prop = new Properties(); try {

                FileOutputStream out = new FileOutputStream(propertiesPath);
                prop.setProperty(AppKey.CONFIG_LINK_SERVER_LOCAL.getKey(), LINK_UPLOAD_ACTIVITYLOGS_LOCAL);
                prop.setProperty(AppKey.CONFIG_LINK_SERVER_WEB.getKey(), LINK_UPLOAD_ACTIVITYLOGS_SERVER);
                prop.store(out, null); out.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to open file " + FILE_CONFIG_PROPERTIES); Log.e(TAG, e.toString());
            }
        }
    }
}
