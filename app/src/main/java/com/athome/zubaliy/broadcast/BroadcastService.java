package com.athome.zubaliy.broadcast;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.athome.zubaliy.sqlite.manager.ActivityLogManager;
import com.athome.zubaliy.sqlite.model.ActivityLog;
import com.athome.zubaliy.util.AppKey;
import com.athome.zubaliy.util.Config;
import com.athome.zubaliy.util.Utils;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.Receiver;
import org.apache.commons.lang3.StringUtils;

import java.util.GregorianCalendar;

/**
 * Created by zubaliy on 06/11/14.
 * <p/>
 * This broadcast service will write the date to db when bluetooth (dis)connect event occurs.
 */


public class BroadcastService extends BroadcastReceiver {
    private static final String TAG = "zBroadcastService";

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityLogManager.init(context);
        Config.init(context);
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        Log.d(TAG, "Bluetooth triggered");
        Log.d(TAG, "action: " + intent.getAction());
        Log.d(TAG, "config mac: " + Config.getBluetoothMac());
        Log.d(TAG, "device mac: " + device.getAddress());

        // Do the work
        if (StringUtils.equals(Config.getBluetoothMac(), device.getAddress())) {
            if (StringUtils.equals("android.bluetooth.device.action.ACL_CONNECTED", intent.getAction())) {
                connected();
            } else if (StringUtils.equals("android.bluetooth.device.action.ACL_DISCONNECTED", intent.getAction())) {
                disconnected();
            }
        }
    }

    public void connected() {
        Log.i(TAG, "connected");

        GregorianCalendar calendar = new GregorianCalendar();

        ActivityLog log = new ActivityLog(calendar.getTime());
        ActivityLogManager.getInstance().addLog(log);

        Log.i(TAG, ActivityLogManager.getInstance().getLastLog().toString());
    }

    public void disconnected() {
        Log.i(TAG, "disconnected");

        GregorianCalendar calendar = new GregorianCalendar();

        ActivityLog log = ActivityLogManager.getInstance().getLastLog();
        log.setDisconnected(calendar.getTime());
        log.setDifference((int) (log.getDisconnected().getTime() - log.getConnected().getTime()));
        ActivityLogManager.getInstance().updateLog(log);

        Log.i(TAG, ActivityLogManager.getInstance().getLastLog().toString());
    }
}
