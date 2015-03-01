package com.athome.zubaliy.mylifeontheroad;

import android.util.Log;

import com.athome.zubaliy.sqlite.manager.ActivityLogManager;
import com.athome.zubaliy.sqlite.model.ActivityLog;
import com.athome.zubaliy.util.Utils;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Date;
import java.util.GregorianCalendar;

import lombok.Setter;

/**
 * @author Andriy Zubaliy
 */
@EBean
public class Engine {
    private static final String TAG = Engine.class.getSimpleName();

    @Setter
    private ActivityLogManager logManager;

    @Setter
    private int shortBreak;

    @Setter
    private int shortJourney;

    public void init(final ActivityLogManager activityLogManager, final int shortBreak, final int shortJourney) {
        this.logManager = activityLogManager;
        this.shortBreak = shortBreak;
        this.shortJourney = shortJourney;
    }

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
        if (StringUtils.equals("android.bluetooth.device.action.ACL_CONNECTED", intentAction)) {
            if (!shortBreak()) {
                Log.d(TAG, String.format("Short stop detected, less than %s ms", Utils.readPreferences(BuildConfig
                        .KEY_SHORT_BREAK)));
                Log.d(TAG, "Short stop => skip creating new row in db.");
                connected();
            }
        } else if (StringUtils.equals("android.bluetooth.device.action.ACL_DISCONNECTED", intentAction)) {
            if (shortJourney()) {
                Log.d(TAG, String.format("Short drive detected, less than %s ms", Utils.readPreferences(BuildConfig
                        .KEY_SHORT_JOURNEY)));
                Log.d(TAG, "Short drive => delete last inserted row.");
                ActivityLogManager.getInstance().deleteLastLog();
            } else {
                disconnected();
            }
        }
    }

    /**
     * Writes a new record in db
     */
    private void connected() {
        Log.i(TAG, "connected");

        ActivityLog log = new ActivityLog(new Date());
        ActivityLogManager.getInstance().addLog(log);

        Log.d(TAG, ActivityLogManager.getInstance().getLastLog().toString());
    }

    /**
     * Adds to the last record in db the disconnection time and calculates the difference.
     */
    private void disconnected() {
        Log.i(TAG, "disconnected");

        ActivityLog lastLog = ActivityLogManager.getInstance().getLastLog();
        lastLog.setDisconnected(new Date());
        ActivityLogManager.getInstance().updateLog(lastLog);

        Log.d(TAG, ActivityLogManager.getInstance().getLastLog().toString());
    }

    /**
     * Verifies if it was just a short stop.
     * Calculate time elapsed from last disconnection.
     */
    protected boolean shortBreak() {
        boolean result = false;
        ActivityLog lastLog = logManager.getLastLog();

        long difference = System.currentTimeMillis() - lastLog.getDisconnected().getTime();
        Log.d(TAG, String.format("Short Stop for: %s", String.valueOf(difference)));

        result = (difference < this.shortBreak);
        return result;
    }

    /**
     * Verifies if it was just a short drive.
     * Calculate time elapsed from last connection
     */
    protected boolean shortJourney() {
        boolean result = false;
        ActivityLog lastLog = ActivityLogManager.getInstance().getLastLog();

        long difference = System.currentTimeMillis() - lastLog.getConnected().getTime();
        Log.d(TAG, String.format("Short Drive for: %s", String.valueOf(difference)));

        result = (difference < this.shortJourney);
        return result;
    }


}
