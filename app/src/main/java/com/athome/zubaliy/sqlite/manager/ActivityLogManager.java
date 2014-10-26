package com.athome.zubaliy.sqlite.manager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.athome.zubaliy.sqlite.model.ActivityLog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Created by zubaliy on 26/10/14.
 */
public class ActivityLogManager {

    private static final String TAG = "ActivityLogRepository";

    static private ActivityLogManager instance;

    static public void init(Context ctx) {
        if (null == instance) {
            instance = new ActivityLogManager(ctx);
        }
    }

    static public ActivityLogManager getInstance() {
        return instance;
    }

    @Getter
    private DatabaseHelper helper;

    private ActivityLogManager(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }


    public List<ActivityLog> getAllLogs() {
        List<ActivityLog> logs = new ArrayList<ActivityLog>();
        try {
            logs = helper.getActivityLogDao().queryForAll();
        } catch (SQLException e) {
            Log.d(TAG, e.toString());
        }
        return logs;
    }

    public ActivityLog getLogWithId(int id) {
        ActivityLog log = null;
        try {
            log = helper.getActivityLogDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(TAG, e.toString());
        }
        return log;
    }

    public void addLog(ActivityLog log){
        try {
            helper.getActivityLogDao().create(log);
        } catch (SQLException e) {
            Log.d(TAG, e.toString());
        }
    }

    public void updateLog(ActivityLog log) {
        try {
            helper.getActivityLogDao().update(log);
        } catch (SQLException e) {
            Log.d(TAG, e.toString());
        }
    }

}