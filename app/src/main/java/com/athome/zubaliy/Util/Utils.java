package com.athome.zubaliy.Util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Utils for easy using of:
 * * SharedPreferences
 * <p/>
 * Created by zubaliy on 02/11/14.
 */
public class Utils {

    private static Activity zActivity;
    private static SharedPreferences sp;

    /**
     * Init utils to make reading and writing of shared preferences possible.
     *
     * @param activity the
     */
    public static void init(Activity activity) {
        zActivity = activity;
        sp = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
    }

    public static void savePreferences(Activity activity, String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String readPreferences(Activity activity, String key, String defaultValue) {
        return sp.getString(key, defaultValue);

    }

    public static void savePreferences(String key, String value) {
        savePreferences(zActivity, key, value);
    }

    public static String readPreferences(String key, String defaultValue) {
        return readPreferences(zActivity, key, defaultValue);
    }

    public static String readPreferences(String key) {
        return readPreferences(key, "Error");
    }

}
