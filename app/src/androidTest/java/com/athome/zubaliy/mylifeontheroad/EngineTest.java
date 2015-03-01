package com.athome.zubaliy.mylifeontheroad;

import android.content.Context;
import android.util.Log;

import com.athome.zubaliy.sqlite.manager.ActivityLogManager;
import com.athome.zubaliy.sqlite.manager.ActivityLogManagerTest;
import com.athome.zubaliy.sqlite.model.ActivityLog;
import com.athome.zubaliy.util.Utils;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import junit.framework.Assert;

import org.androidannotations.annotations.Bean;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@RunWith(RobolectricTestRunner.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EngineTest {
    private static final String TAG = EngineTest.class.getSimpleName();

    Context context;
    ActivityLogManager logManager;

    Engine engine;
    int shortBreakMinutes;
    int shortJourneyMinutes;

    @Before
    public void setUp() {
        context = Robolectric.application.getApplicationContext();
        ActivityLogManager.init(context);
        logManager = ActivityLogManager.getInstance();

        engine = new Engine();
        engine.setLogManager(logManager);
        // TODO read default value configuration
        // TODO how to read from prefereces during UT?
        shortBreakMinutes = 3;
        engine.setShortBreak(shortBreakMinutes * 60 * 1000);
        shortJourneyMinutes = 3;
        engine.setShortJourney(shortJourneyMinutes * 60 * 1000);
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
    public void testDoTheWork() throws Exception {

    }

    @Test
    public void testShortBreak_TRUE() throws Exception {
        ActivityLog log = new ActivityLog();
        log.setConnected(new Date());
        log.setDisconnected(new Date());
        logManager.addLog(log);

        Assert.assertTrue(engine.shortBreak());
    }

    @Test
    public void testShortBreak_FALSE() throws Exception {
        ActivityLog log = new ActivityLog();
        log.setConnected(DateUtils.addMinutes(new Date(), -15));
        log.setDisconnected(DateUtils.addMinutes(new Date(), -shortBreakMinutes - 1));
        logManager.addLog(log);

        Assert.assertFalse(engine.shortBreak());
    }

    @Test
    public void testShortJourney_TRUE() throws Exception {
        ActivityLog log = new ActivityLog();
        log.setConnected(new Date());
        log.setDisconnected(new Date());
        logManager.addLog(log);

        Assert.assertTrue(engine.shortJourney());
    }

    @Test
    public void testShortJourney_FALSE() throws Exception {
        ActivityLog log = new ActivityLog();
        log.setConnected(DateUtils.addMinutes(new Date(), -15));
        log.setDisconnected(new Date());
        logManager.addLog(log);

        Assert.assertFalse(engine.shortJourney());
    }
}