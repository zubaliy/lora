package com.athome.zubaliy.mylifeontheroad;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.athome.zubaliy.Util.AppKey;
import com.athome.zubaliy.Util.Config;
import com.athome.zubaliy.Util.Utils;
import com.athome.zubaliy.bluetooth.Bluetooth;
import com.athome.zubaliy.sqlite.manager.ActivityLogManager;
import com.athome.zubaliy.sqlite.model.ActivityLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.GregorianCalendar;
import java.util.Map;

/**
 * The main activity, where all starts.
 */

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {
    private static final String TAG = "zMainActivity";

    @ViewById(R.id.btn_connected)
    public Button zButton;

    @ViewById(R.id.txt_bluetooth_device)
    public TextView zDevice;

    @ViewById(R.id.txt_console)
    public TextView zConsole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "Activity started");


        Utils.init(this);


        ActivityLogManager.init(this);
    }

    /**
     * Reload view, text, colors and other defaults
     */
    public void initView() {
        Config.bluetoothMAC = Utils.readPreferences(AppKey.DEVICE_MAC_ADDRESS.getKey());
        String deviceName = Bluetooth.getInstance().getBondenDevices().get(Config.bluetoothMAC);
        zDevice.setText(deviceName);
    }

    @LongClick(R.id.txt_bluetooth_device)
    public void selectDevice() {
        Map<String, String> devices = Bluetooth.getInstance().getBondenDevices();
        final String[] macs = devices.keySet().toArray(new String[devices.size()]);
        final String[] names = devices.values().toArray(new String[devices.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select device");
        builder.setItems(names, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                Utils.savePreferences(AppKey.DEVICE_MAC_ADDRESS.getKey(), macs[item]);
                zDevice.setText(names[item]);
                Config.bluetoothMAC = macs[item];
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }


    @Receiver(actions = "android.bluetooth.device.action.ACL_CONNECTED")
    protected void bluetoothConnected(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        logInfoAboutBluetoothDevice(intent, device);

        zConsole.append("connected to " + device.getName());
        zConsole.append("\n");

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

        zConsole.append("disconnected from " + device.getName());
        zConsole.append("\n");

        if (StringUtils.equals(Config.bluetoothMAC, device.getAddress())) {
            disconnected();
        }

    }




 

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        initView();
    }

}
