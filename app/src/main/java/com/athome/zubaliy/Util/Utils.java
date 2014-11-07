package com.athome.zubaliy.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Utils for easy using of:
 * * SharedPreferences
 * <p/>
 * Created by zubaliy on 02/11/14.
 */
public class Utils {

    private static SharedPreferences sp;

    /**
     * Init utils to make reading and writing of shared preferences possible.
     *
     * @param context the
     */
    public static void init(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void savePreferences(String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String readPreferences(String key, String defaultValue) {
        return sp.getString(key, defaultValue);

    }

    public static String readPreferences(String key) {
        return readPreferences(key, "Error");
    }

}
