package com.athome.zubaliy.util;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Utils for easy using of:
 * * SharedPreferences
 * <p/>
 * Created by zubaliy on 02/11/14.
 */
public class Utils {

    private static Context sContext;

    public static void initialize(Context context) {
        sContext = context.getApplicationContext();
    }

    public static void terminate() {
        sContext = null;
    }

    public static void savePreferences(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(sContext)
                         .edit()
                         .putString(key, value)
                         .apply();
    }

    public static String readPreferences(String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(sContext)
                                .getString(key, defaultValue);

    }

    public static String readPreferences(String key) {
        return readPreferences(key, "Error");
    }

}
