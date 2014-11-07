package com.athome.zubaliy.service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.athome.zubaliy.util.Config;
import com.athome.zubaliy.sqlite.manager.ActivityLogManager;
import com.athome.zubaliy.sqlite.model.ActivityLog;
import com.athome.zubaliy.util.MyNotifications;
import com.athome.zubaliy.util.Utils;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.UiThread;
import org.apache.commons.lang3.StringUtils;

import java.util.GregorianCalendar;

/**
 * Service that runs continue on background and collection bluetooth activities
 * <p/>
 * Created by zubaliy on 04/11/14.
 */

@EService
public class ActivityTrackingService extends Service {
    private static final String TAG = "zActivityTrackingService";


    @Override
    public void onCreate() {
        Log.i(TAG, "Service Created");
        Config.init(this);


        startMeInForeground();
    }


    @UiThread
    void showToast() {
        Toast.makeText(getApplicationContext(), "Hello from Service!", Toast.LENGTH_SHORT).show();
    }

    /**
     * See description for startForeground()
     */
    private void startMeInForeground() {
        // Caution: The integer ID you give to startForeground() must not be 0.
        startForeground(1, MyNotifications.createNotificationInTheStatusBar(this));
    }


    @Override
    public IBinder onBind(Intent intent) {
        // do not allow binding, return null
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service Started");
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        // Let it continue running until it is stopped.
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service Destroyed");
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }


//    @Receiver(actions = "android.bluetooth.device.action.ACL_CONNECTED")
    protected void bluetoothConnected(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if (StringUtils.equals(Config.getBluetoothMac(), device.getAddress())) {
            connected();
        }
    }

//    @Receiver(actions = "android.bluetooth.device.action.ACL_DISCONNECTED")
    protected void bluetoothDisconnected(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if (StringUtils.equals(Config.getBluetoothMac(), device.getAddress())) {
            disconnected();
        }
    }

    public void connected() {
        Log.i(TAG, "connected");
        Toast.makeText(this, "connected", Toast.LENGTH_LONG).show();

        GregorianCalendar calendar = new GregorianCalendar();

        ActivityLog log = new ActivityLog(calendar.getTime());
        ActivityLogManager.getInstance().addLog(log);

        Log.i(TAG, ActivityLogManager.getInstance().getLastLog().toString());
    }

    public void disconnected() {
        Log.i(TAG, "disconnected");
        Toast.makeText(this, "disconnected", Toast.LENGTH_LONG).show();

        GregorianCalendar calendar = new GregorianCalendar();

        ActivityLog log = ActivityLogManager.getInstance().getLastLog();
        log.setDisconnected(calendar.getTime());
        log.setDifference((int) (log.getDisconnected().getTime() - log.getConnected().getTime()));
        ActivityLogManager.getInstance().updateLog(log);

        Log.i(TAG, ActivityLogManager.getInstance().getLastLog().toString());
    }


}
