package com.athome.zubaliy.sqlite.manager;


import android.content.Context;

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
import java.util.List;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

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
    public void setUp() {
        context = Robolectric.application.getApplicationContext();
        ActivityLogManager.init(context);
        logManager = ActivityLogManager.getInstance();


        log1 = new ActivityLog();
        log1.setConnected(DateUtils.parseDate("2013-10-16 01:00:00", BuildConfig.DATEFORMAT_FOR_TEST));
        log1.setDisconnected(DateUtils.parseDate("2013-10-16 23:59:59", BuildConfig.DATEFORMAT_FOR_TEST));
        logManager.addLog(log1);

        log2 = new ActivityLog();
        log2.setConnected(DateUtils.parseDate("2013-10-17 01:00:00", BuildConfig.DATEFORMAT_FOR_TEST));
        log2.setDisconnected(DateUtils.parseDate("2013-10-17 23:59:59", BuildConfig.DATEFORMAT_FOR_TEST));
        logManager.addLog(log2);

        log3 = new ActivityLog();
        log3.setConnected(DateUtils.parseDate("2013-10-18 01:00:00", BuildConfig.DATEFORMAT_FOR_TEST));
        log3.setDisconnected(DateUtils.parseDate("2013-10-18 23:59:59", BuildConfig.DATEFORMAT_FOR_TEST));
        logManager.addLog(log3);

        //TODO Id is not being set by ORM lite
        log1.setId(1);
        log2.setId(2);
        log3.setId(3);

        initialDbSize = 3;
    }

    @After
    public void tearDown() {
        // clean up logManager
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
        Date startDate = DateUtils.parseDate("2013-10-16 01:00:00", BuildConfig.DATEFORMAT_FOR_TEST);
        assertEquals(initialDbSize, logManager.findLogsFromDate(startDate).size());

        Date laterDate = DateUtils.parseDate("2013-10-16 02:00:00", BuildConfig.DATEFORMAT_FOR_TEST);
        assertEquals(initialDbSize - 1, logManager.findLogsFromDate(laterDate).size());
    }

    @Test
    public void testCreateZeroToday() {
        Date today = logManager.createZeroToday();
        Date expected = createZeroToday();
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
        assertEquals(createZeroThisMonth(), logManager.createZeroThisMonth());
    }

    @Test
    public void testCreateZeroThisYear() {
        assertEquals(createZeroThisYear(), logManager.createZeroThisYear());

    }

    private Date createZeroToday() {
        return DateUtils.truncate(new Date(), Calendar.DATE);
    }

    private Date createMondayThisWeek() {
        Calendar c = Calendar.getInstance();
        c.setTime(createZeroToday());
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return c.getTime();
    }

    private Date createZeroThisMonth() {
        Calendar c = Calendar.getInstance();
        c.setTime(createZeroToday());
        c.set(Calendar.DAY_OF_MONTH, 1);

        return c.getTime();
    }

    @SneakyThrows
    private Date createZeroThisYear() {
        return DateUtils.parseDate(new Date().getYear() + 1900 + "-01-01 00:00:00", BuildConfig.DATEFORMAT_FOR_TEST);
    }

    @Test
    public void testGetActivityLogsForLastPeriod_DAYS() {
        fillDbWithLogsForTheLastThreeDays();

        List<ActivityLog> result = logManager.getActivityLogsForLast(1, Period.DAYS);
        assertNotNull(result);
        assertEquals(1, result.size());

        result = logManager.getActivityLogsForLast(2, Period.DAYS);
        assertNotNull(result);
        assertEquals(2, result.size());

        result = logManager.getActivityLogsForLast(3, Period.DAYS);
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    public void testGetActivityLogsForLastPeriod_WEEKS() {
        fillDbWithLogsForTheLastThreeDays();
        Date today = createZeroToday();
        Calendar c = new GregorianCalendar();
        c.setTime(today);

        List<ActivityLog> result = logManager.getActivityLogsForLast(1, Period.WEEKS);

        assertNotNull(result);

        int k;
        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                k = 1;
                break;
            case Calendar.TUESDAY:
                k = 2;
                break;
            default:
                k = 3;
                break;
        }

        assertEquals(k, result.size());

    }

    @Test
    public void testGetActivityLogsForLastPeriod_MONTHS() {
        fillDbWithLogsForTheLastThreeMonths();

        List<ActivityLog> result = logManager.getActivityLogsForLast(1, Period.MONTHS);
        assertNotNull(result);
        assertEquals(1, result.size());

        result = logManager.getActivityLogsForLast(2, Period.MONTHS);
        assertNotNull(result);
        assertEquals(2, result.size());

        result = logManager.getActivityLogsForLast(3, Period.MONTHS);
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    public void testGetActivityLogsForLastPeriod_YEARS() {
        fillDbWithLogsForTheLastTwoYears();

        List<ActivityLog> result = logManager.getActivityLogsForLast(1, Period.YEARS);
        assertNotNull(result);
        assertEquals(2, result.size());

    }

    @SneakyThrows
    private void fillDbWithLogsForTheLastThreeDays() {
        Date date = createZeroToday();
        Date dateOneHourLater = DateUtils.addHours(date, 1);

        ActivityLog log1 = new ActivityLog();
        log1.setConnected(date);
        log1.setDisconnected(dateOneHourLater);

        ActivityLog log2 = new ActivityLog();
        log2.setConnected(DateUtils.addDays(date, -1));
        log2.setDisconnected(DateUtils.addDays(dateOneHourLater, -1));

        ActivityLog log3 = new ActivityLog();
        log3.setConnected(DateUtils.addDays(date, -2));
        log3.setDisconnected(DateUtils.addDays(dateOneHourLater, -2));

        logManager.addLog(log1);
        logManager.addLog(log2);
        logManager.addLog(log3);
    }

    @SneakyThrows
    private void fillDbWithLogsForTheLastThreeMonths() {
        Date date = createZeroToday();
        Date dateOneHourLater = DateUtils.addHours(date, 1);

        ActivityLog log1 = new ActivityLog();
        log1.setConnected(date);
        log1.setDisconnected(dateOneHourLater);

        ActivityLog log2 = new ActivityLog();
        log2.setConnected(DateUtils.addMonths(date, -1));
        log2.setDisconnected(DateUtils.addMonths(dateOneHourLater, -1));

        ActivityLog log3 = new ActivityLog();
        log3.setConnected(DateUtils.addMonths(date, -2));
        log3.setDisconnected(DateUtils.addMonths(dateOneHourLater, -2));

        logManager.addLog(log1);
        logManager.addLog(log2);
        logManager.addLog(log3);
    }

    @SneakyThrows
    private void fillDbWithLogsForTheLastTwoYears() {
        Date date = createZeroToday();
        Date dateOneHourLater = DateUtils.addHours(date, 1);

        ActivityLog log1 = new ActivityLog();
        log1.setConnected(date);
        log1.setDisconnected(dateOneHourLater);

        ActivityLog log2 = new ActivityLog();
        log2.setConnected(DateUtils.addHours(date, 1));
        log2.setDisconnected(DateUtils.addHours(dateOneHourLater, 1));

        ActivityLog log3 = new ActivityLog();
        log3.setConnected(DateUtils.addYears(date, -1));
        log3.setDisconnected(DateUtils.addYears(dateOneHourLater, -1));

        logManager.addLog(log1);
        logManager.addLog(log2);
        logManager.addLog(log3);
    }
}