package com.athome.zubaliy.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.athome.zubaliy.Util.Config;
import com.athome.zubaliy.Util.LOG;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.client.utils.CloneUtils;

import java.util.HashSet;
import java.util.Set;

/**
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
            LOG.debug(TAG, "Device does not support Bluetooth.");
        } else {
            LOG.debug(TAG, "Device supports Bluetooth.");
            enableBluetooth();
        }
    }

    private void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
    }


    public String getBondenDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        Set<String> bondenDevices = new HashSet<String>();
        // If there are paired devices
        if (CollectionUtils.isNotEmpty(pairedDevices)) {
            LOG.debug(TAG, "Paired devices:");
            for (BluetoothDevice device : pairedDevices) {
                LOG.debug(TAG, device.getName() + " - " + device.getAddress());
                bondenDevices.add(device.getName() + " - " + device.getAddress());
            }
        }

        return bondenDevices.toString();
    }
}
