package com.athome.zubaliy.broadcast;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.athome.zubaliy.mylifeontheroad.BuildConfig;
import com.athome.zubaliy.mylifeontheroad.Engine;
import com.athome.zubaliy.sqlite.manager.ActivityLogManager;
import com.athome.zubaliy.util.Utils;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * This broadcast service will write the date to db when bluetooth (dis)connect event occurs.
 *
 * @author Andriy Zubaliy
 */

public class BroadcastService extends BroadcastReceiver {
    private static final String TAG = BroadcastService.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityLogManager.init(context);

        String chosenDeviceMacAddress = Utils.readPreferences(BuildConfig.KEY_DEVICE_MAC_ADDRESS, "00:00:00:00:00:00");
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        Log.d(TAG, "Bluetooth triggered");
        Log.d(TAG, "action: " + intent.getAction());
        Log.d(TAG, "config mac: " + chosenDeviceMacAddress);
        Log.d(TAG, "device mac: " + device.getAddress());

        // Do the work, only when the device matches our chosen device
        if(device.getAddress().equalsIgnoreCase(chosenDeviceMacAddress)) {
            Engine engine = new Engine();
            engine.setLogManager(ActivityLogManager.getInstance());
            engine.setShortBreak(NumberUtils.toInt(Utils.readPreferences(BuildConfig.KEY_SHORT_BREAK)));
            engine.setShortJourney(NumberUtils.toInt(Utils.readPreferences(BuildConfig.KEY_SHORT_JOURNEY)));

            engine.doTheWork(intent.getAction(), device.getAddress());
        }
    }

}
