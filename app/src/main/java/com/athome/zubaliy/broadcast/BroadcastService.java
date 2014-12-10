package com.athome.zubaliy.broadcast;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.athome.zubaliy.mylifeontheroad.Engine;
import com.athome.zubaliy.sqlite.manager.ActivityLogManager;
import com.athome.zubaliy.util.Config;

/**
 * Created by zubaliy on 06/11/14.
 * <p/>
 * This broadcast service will write the date to db when bluetooth (dis)connect event occurs.
 */

public class BroadcastService extends BroadcastReceiver {
    private static final String TAG = BroadcastService.class.getSimpleName();

    private static Engine engine = new Engine();

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
        engine.doTheWork(intent.getAction(), device.getAddress());
    }

}
