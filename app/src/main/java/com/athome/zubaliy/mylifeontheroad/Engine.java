package com.athome.zubaliy.mylifeontheroad;

import android.util.Log;

import com.athome.zubaliy.sqlite.manager.ActivityLogManager;
import com.athome.zubaliy.sqlite.model.ActivityLog;
import com.athome.zubaliy.util.Config;
import com.athome.zubaliy.util.Utils;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.GregorianCalendar;

/**
 * Created by zubaliy on 02/12/14.
 */
@EBean
public class Engine {
    private static final String TAG = Engine.class.getSimpleName();

    /**
     * Handel intent action.
     * Check if the right device is (dis)connected.
     * Do the writes to db.
     *
     * @param intentAction the intention action
     * @param mac          the bluetooth mac address
     */
    @Background
    public void doTheWork(String intentAction, String mac) {
        if (StringUtils.equals(Config.getBluetoothMac(), mac)) {
            if (StringUtils.equals("android.bluetooth.device.action.ACL_CONNECTED", intentAction)) {
                if (!shortBreak()) {
                    Log.d(TAG, String.format("Short stop detected, less than %s ms",
                            Utils.readPreferences(BuildConfig.KEY_SHORT_BREAK)));
                    Log.d(TAG, "Short stop => skip creating new row in db.");
                    connected();
                }
            } else if (StringUtils.equals("android.bluetooth.device.action.ACL_DISCONNECTED", intentAction)) {
                if (shortJourney()) {
                    Log.d(TAG, String.format("Short drive detected, less than %s ms",
                            Utils.readPreferences(BuildConfig.KEY_SHORT_JOURNEY)));
                    Log.d(TAG, "Short drive => delete last inserted row.");
                    ActivityLogManager.getInstance().deleteLastRow();
                } else {
                    disconnected();
                }
            }
        }
    }

    /**
     * Writes a new record in db
     */
    public void connected() {
        Log.i(TAG, "connected");

        GregorianCalendar calendar = new GregorianCalendar();

        ActivityLog log = new ActivityLog(calendar.getTime());
        ActivityLogManager.getInstance().addLog(log);

        Log.i(TAG, ActivityLogManager.getInstance().getLastLog().toString());
    }

    /**
     * Adds to the last record in db the disconnection time and calculates the difference.
     */
    public void disconnected() {
        Log.i(TAG, "disconnected");

        GregorianCalendar now = new GregorianCalendar();

        ActivityLog log = ActivityLogManager.getInstance().getLastLog();
        log.setDisconnected(now.getTime());
        log.setDifference((int) (log.getDisconnected().getTime() - log.getConnected().getTime()));
        ActivityLogManager.getInstance().updateLog(log);

        Log.i(TAG, ActivityLogManager.getInstance().getLastLog().toString());
    }

    /**
     * Verifies if it was just a short stop.
     * Calculate time elapsed from last disconnection.
     */
    public boolean shortBreak() {
        boolean result = false;
        if (NumberUtils.isNumber(Utils.readPreferences(BuildConfig.KEY_SHORT_BREAK))) {
            GregorianCalendar now = new GregorianCalendar();
            ActivityLog log = ActivityLogManager.getInstance().getLastLog();
            long difference = now.getTime().getTime() - log.getDisconnected().getTime();

            Log.d(TAG, String.format("Short Stop for: %s", String.valueOf(difference)));

            result = (difference < NumberUtils.toInt(Utils.readPreferences(BuildConfig.KEY_SHORT_BREAK)));
        }

        return result;
    }

    /**
     * Verifies if it was just a short drive.
     * Calculate time elapsed from last connection
     */
    public boolean shortJourney() {
        boolean result = false;
        if (NumberUtils.isNumber(Utils.readPreferences(BuildConfig.KEY_SHORT_JOURNEY))) {
            GregorianCalendar now = new GregorianCalendar();
            ActivityLog log = ActivityLogManager.getInstance().getLastLog();
            long difference = now.getTime().getTime() - log.getConnected().getTime();

            Log.d(TAG, String.format("Short Drive for: %s", String.valueOf(difference)));

            result = (difference < NumberUtils.toInt(Utils.readPreferences(BuildConfig.KEY_SHORT_JOURNEY)));
        }

        return result;
    }
}
