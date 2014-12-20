package com.athome.zubaliy.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Bluetooth controller
 * <p/>
 *
 * @author Andriy Zubaliy
 */
public class Bluetooth {

    private static final String TAG = Bluetooth.class.getSimpleName();

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
        Map<String, String> bondedDevices = new HashMap<>();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        Log.d(TAG, "Paired devices:");
        if (pairedDevices != null && !pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                Log.d(TAG, device.getAddress() + " - " + device.getName());
                bondedDevices.put(device.getAddress(), device.getName());
            }
        } else {
            Log.d(TAG, "None.");
        }

        return bondedDevices;
    }
}
