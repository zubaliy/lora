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
import org.apache.commons.lang3.StringUtils;

import java.util.GregorianCalendar;
import java.util.Map;


@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {
    private static final String TAG = "zMainActivity";

    @ViewById(R.id.btn_connected)
    public Button zButton;

    @ViewById(R.id.txt_bluetooth_device)
    public TextView zDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "Activity started");


        Utils.init(this);


        ActivityLogManager.init(this);
    }

    public void initView() {
        String deviceName = Bluetooth.getInstance().getBondenDevices().get(Utils.readPreferences(AppKey
                .DEVICE_MAC_ADDRESS.getKey()));
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
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }


    @Receiver(actions = "android.bluetooth.device.action.ACL_CONNECTED")
    protected void bluetoothConnected(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


        if (StringUtils.equals(Config.bluetoothMAC, device.getAddress())) {
            connected();
        }
    }

    public void connected() {
        Toast.makeText(this, "connected", Toast.LENGTH_LONG).show();
        GregorianCalendar calendar = new GregorianCalendar();

        ActivityLog log = new ActivityLog(calendar.getTime());
        ActivityLogManager.getInstance().addLog(log);

        Log.i(TAG, ActivityLogManager.getInstance().getLastLog().toString());
    }

    public void disconnected() {
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
