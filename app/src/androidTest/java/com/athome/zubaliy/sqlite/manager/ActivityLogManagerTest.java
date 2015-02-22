package com.athome.zubaliy.sqlite.manager;


import android.content.Context;
import android.util.Log;

import com.athome.zubaliy.mylifeontheroad.BuildConfig;
import com.athome.zubaliy.sqlite.model.ActivityLog;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivityLogManagerTest {
    private static final String TAG = ActivityLogManagerTest.class.getSimpleName();

    Context context;
    ActivityLogManager logManager;
    ActivityLog log1;
    ActivityLog log2;
    ActivityLog log3;

    int initialDbSize;

    @Before
    @SneakyThrows
    public void setup() {
        context = Robolectric.application.getApplicationContext();
        ActivityLogManager.init(context);
        logManager = ActivityLogManager.getInstance();


        log1 = new ActivityLog();
        log1.setConnected(DateUtils.parseDate("2015-01-01 01:00:00", BuildConfig.DATEFORMAT_FOR_TEST));
        log1.setDisconnected(DateUtils.parseDate("2015-01-01 23:59:59", BuildConfig.DATEFORMAT_FOR_TEST));
        logManager.addLog(log1);

        log2 = new ActivityLog();
        log2.setConnected(DateUtils.parseDate("2015-01-02 01:00:00", BuildConfig.DATEFORMAT_FOR_TEST));
        log2.setDisconnected(DateUtils.parseDate("2015-01-02 23:59:59", BuildConfig.DATEFORMAT_FOR_TEST));
        logManager.addLog(log2);

        log3 = new ActivityLog();
        log3.setConnected(DateUtils.parseDate("2015-01-03 01:00:00", BuildConfig.DATEFORMAT_FOR_TEST));
        log3.setDisconnected(DateUtils.parseDate("2015-01-03 23:59:59", BuildConfig.DATEFORMAT_FOR_TEST));
        logManager.addLog(log3);

        //TODO Id is not being set by ORM lite
        log1.setId(1);
        log2.setId(2);
        log3.setId(3);

        initialDbSize = 3;
    }

    @After
    public void tearDown() {
        if (logManager.getHelper() != null) {
            logManager.getHelper().getActivityLogDao().clearObjectCache();
            logManager.getHelper().close();
            ActivityLogManager.clearInstance();
            OpenHelperManager.releaseHelper();
        }
    }

    @Test
    public void testContext() {
        assertNotNull(context);
    }

    @Test
    public void testGetInstance() {
        assertNotNull(ActivityLogManager.getInstance());
    }

    @Test
    public void testGetAllLogs() {
        assertEquals(initialDbSize, logManager.getAllLogs().size());
    }

    @Test
    public void testGetLogWithId() {
        ActivityLog result = logManager.getLogWithId(log2.getId());

        assertNotNull(result);
        assertEquals(log2.getId(), result.getId());
        assertEquals(log2.getConnected().getTime(), result.getConnected().getTime());
        assertEquals(log2.getDisconnected().getTime(), result.getDisconnected().getTime());
        assertEquals(log2.getDifference(), result.getDifference());
    }

    @Test
    public void testAddLog() {
        Integer newId = logManager.getLastLog().getId() + 1;
        logManager.addLog(new ActivityLog());

        ActivityLog result = logManager.getLastLog();

        assertEquals(newId, result.getId());
    }

    @Test
    public void testUpdateLog() {
        ActivityLog lastLog = logManager.getLastLog();
        Integer newValue = 500;
        assertNotEquals(newValue, lastLog.getDifference());

        lastLog.setDifference(newValue);
        logManager.updateLog(lastLog);
        ActivityLog result = logManager.getLastLog();

        assertEquals(newValue, result.getDifference());
    }

    @Test
    public void testGetLastLog() {
        ActivityLog result = logManager.getLastLog();

        assertEquals(log3.getId(), result.getId());
    }

    @Test
    public void testDeleteLastLog() {
        logManager.deleteLastLog();

        assertEquals(initialDbSize - 1, logManager.getAllLogs().size());
    }

    @Test
    public void testGetHelper() {
        assertNotNull(logManager.getHelper());
    }

    @Test
    @SneakyThrows
    public void testFindLogsFromDate() {
        Date startDate = DateUtils.parseDate("2015-01-01 01:00:00", BuildConfig.DATEFORMAT_FOR_TEST);
        assertEquals(initialDbSize, logManager.findLogsFromDate(startDate).size());

        Date laterDate = DateUtils.parseDate("2015-01-01 02:00:00", BuildConfig.DATEFORMAT_FOR_TEST);
        assertEquals(initialDbSize - 1, logManager.findLogsFromDate(laterDate).size());
    }

    @Test
    public void testCreateZeroToday() {
        Date today = logManager.createZeroToday();
        Date expected = createTodayDate();
        assertEquals(expected, today);
    }

    @Test
    public void testCreateZeroThisWeek() {
        Calendar c = Calendar.getInstance();
        c.setTime(logManager.createZeroThisWeek());

        assertEquals(createMondayThisWeek(), c.getTime());
        assertEquals(Calendar.MONDAY, c.get(Calendar.DAY_OF_WEEK));
    }

    @Test
    public void testCreateZeroThisMonth() {
        assertEquals(createThisMonthFirstDay(), logManager.createZeroThisMonth());
    }

    @Test
    public void testCreateZeroThisYear() {
        assertEquals(createThisYearFirstDay(), logManager.createZeroThisYear());

    }

    @Test
    public void testGetActivityLogs() {
        // TODO create test
        assertTrue(false);
    }

    private Date createTodayDate() {
        return DateUtils.truncate(new Date(), Calendar.DATE);
    }

    private Date createMondayThisWeek() {
        Calendar c = Calendar.getInstance();
        c.setTime(logManager.createZeroThisWeek());
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return c.getTime();
    }

    private Date createThisMonthFirstDay() {
        Calendar c = Calendar.getInstance();
        c.setTime(logManager.createZeroThisWeek());
        c.set(Calendar.DAY_OF_MONTH, 1);

        return c.getTime();
    }

    @SneakyThrows
    private Date createThisYearFirstDay() {
        return DateUtils.parseDate(new Date().getYear() + 1900 + "-01-01 00:00:00", BuildConfig.DATEFORMAT_FOR_TEST);
    }

}