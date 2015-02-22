package com.athome.zubaliy.sqlite.manager;

import android.content.Context;
import android.util.Log;

import com.athome.zubaliy.sqlite.model.ActivityLog;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import org.apache.commons.lang3.time.DateUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import lombok.Getter;

/**
 * Provides basic methods to manage the logs in db. Add, Update, Retrieve, ...
 *
 * @author zubaliy
 */
public class ActivityLogManager {

    private static final String TAG = ActivityLogManager.class.getSimpleName();

    private static ActivityLogManager instance;

    @Getter
    private DatabaseHelper helper;

    static public void init(Context ctx) {
        if (null == instance) {
            instance = new ActivityLogManager(ctx);
        }
    }

    static public ActivityLogManager getInstance() {
        return instance;
    }

    static public void clearInstance() {
        instance = null;
    }


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

    public void addLog(ActivityLog log) {
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

    public ActivityLog getLastLog() {

        QueryBuilder<ActivityLog, Integer> builder = helper.getActivityLogDao().queryBuilder();
        builder.orderBy("id", false);
        builder.limit(1L);

        List<ActivityLog> logs = null;
        try {
            logs = helper.getActivityLogDao().query(builder.prepare());
        } catch (SQLException e) {
            Log.d(TAG, e.toString());
        }

        return logs.get(0);
    }

    public void deleteLastLog() {
        try {
            helper.getActivityLogDao().delete(getLastLog());
        } catch (SQLException e) {
            Log.d(TAG, e.toString());
        }
    }

    public List<ActivityLog> findLogsFromDate(final Date fromDate) {
        List<ActivityLog> result = new ArrayList<>();


        QueryBuilder<ActivityLog, Integer> queryBuilder = helper.getActivityLogDao().queryBuilder();
        try {
            PreparedQuery<ActivityLog> preparedQuery = queryBuilder.where().ge(ActivityLog.FIELD_CONECTED,
                    fromDate).prepare();
            result = helper.getActivityLogDao().query(preparedQuery);
        } catch (SQLException e) {
            Log.e(TAG, e.toString());
        }

        return result;
    }

    public List<ActivityLog> getActivityLogs(final String period, final Integer value) {
        List<ActivityLog> result = new ArrayList<>();

        switch (period) {
            case "days":
                result = findLogsFromDate(DateUtils.addDays(createZeroToday(), -value));
                break;
            case "weeks":
                result = findLogsFromDate(DateUtils.addWeeks(createZeroThisWeek(), -value));
                break;
            case "months":
                result = findLogsFromDate(DateUtils.addMonths(createZeroThisMonth(), -value));
                break;
            case "years":
                result = findLogsFromDate(DateUtils.addYears(createZeroThisYear(), -value));
                break;
            default:
                break;
        }

        return result;
    }

    public Calendar createZeroTodayCalendar() {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c;
    }

    public Date createZeroToday() {
        return createZeroTodayCalendar().getTime();
    }

    public Date createZeroThisWeek() {
        Calendar c = createZeroTodayCalendar();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return c.getTime();
    }

    public Date createZeroThisMonth() {
        Calendar c = createZeroTodayCalendar();
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    public Date createZeroThisYear() {
        Calendar c = createZeroTodayCalendar();
        c.set(Calendar.DAY_OF_YEAR, 1);
        return c.getTime();
    }

}