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

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.Receiver;
import org.apache.commons.lang3.StringUtils;

import java.util.GregorianCalendar;

/**
 * Service that runs continue on background and collection bluetooth specific activities
 * <p/>
 * Created by zubaliy on 04/11/14.
 */

@EService
public class ActivityTrackingService extends Service {
    private static final String TAG = "zActivityTrackingService";

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // do not allow binding, return null
        return null;
    }




    @Receiver(actions = "android.bluetooth.device.action.ACL_CONNECTED")
    protected void bluetoothConnected(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        logInfoAboutBluetoothDevice(intent, device);

        if (StringUtils.equals(Config.bluetoothMAC, device.getAddress())) {
            connected();
        }

    }

    private void logInfoAboutBluetoothDevice(Intent intent, BluetoothDevice device) {
        if (Config.DEBUG) {
            Log.d(TAG, "intent.action = " + intent.getAction());

            Log.d(TAG, "device.name = " + device.getName());
            Log.d(TAG, "device.address = " + device.getAddress());
            Log.d(TAG, "device.bluetooth_class = " + device.getBluetoothClass());
            Log.d(TAG, "device.type = " + device.getType());
            if (device.getUuids() != null) {
                for (int i = 0; i < device.getUuids().length; i++) {
                    Log.d(TAG, "device.uuid " + i + " = " + device.getUuids()[i]);
                }
            }
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

    @Receiver(actions = "android.bluetooth.device.action.ACL_DISCONNECTED")
    protected void bluetoothDisconnected(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        logInfoAboutBluetoothDevice(intent, device);

        if (StringUtils.equals(Config.bluetoothMAC, device.getAddress())) {
            disconnected();
        }

    }
}
