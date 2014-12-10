package com.athome.zubaliy.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Bluetooth controller
 * <p/>
 * Created by zubaliy on 28/10/14.
 */
public class Bluetooth {
    private static final String TAG = "zBluetooth";

    // singleton instance
    private static final Bluetooth instance = new Bluetooth();

    private BluetoothAdapter bluetoothAdapter;

    public static Bluetooth getInstance() {
        return instance;
    }

    private Bluetooth() {
        initBluetooth();
    }

    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        if (bluetoothAdapter == null) {
            Log.d(TAG, "Device does not support Bluetooth.");
        } else {
            Log.d(TAG, "Device supports Bluetooth.");
            enableBluetooth();
        }
    }

    private void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
    }


    /**
     * Get a list of bonded bluetooth devices
     *
     * @return map of MAC's and Names
     */
    public Map<String, String> getBondedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        Map<String, String> bondedDevices = new HashMap<>();
        // If there are paired devices
        if (CollectionUtils.isNotEmpty(pairedDevices)) {
            Log.d(TAG, "Paired devices:");
            for (BluetoothDevice device : pairedDevices) {
                Log.d(TAG, device.getName() + " - " + device.getAddress());
                bondedDevices.put(device.getAddress(), device.getName());
            }
        }

        return bondedDevices;
    }
}
