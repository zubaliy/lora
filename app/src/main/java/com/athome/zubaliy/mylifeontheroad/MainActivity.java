package com.athome.zubaliy.mylifeontheroad;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.athome.zubaliy.service.ActivityTrackingService_;
import com.athome.zubaliy.util.AndroidDatabaseManager;
import com.athome.zubaliy.util.AppKey;
import com.athome.zubaliy.util.Config;
import com.athome.zubaliy.util.Utils;
import com.athome.zubaliy.bluetooth.Bluetooth;
import com.athome.zubaliy.sqlite.manager.ActivityLogManager;
import com.athome.zubaliy.sqlite.model.ActivityLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.HttpsClient;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

/**
 * The main activity, where all starts.
 */

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {
    private static final String TAG = "zMainActivity";


    @ViewById(R.id.txt_bluetooth_device)
    public TextView zDevice;

    @ViewById(R.id.txt_console)
    public TextView zConsole;

    @HttpsClient
    public HttpClient httpsClient;

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Activity started");

        Config.init(this);
        gson = new GsonBuilder().setDateFormat(Config.sdf.toPattern()).create();


        ActivityLogManager.init(this);
    }

    @LongClick(R.id.tekst)
    public void showDbManager() {
        Intent dbmanager = new Intent(this, AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }

    @Click(R.id.btnIsService)
    public void isServiceRunning() {

        zConsole.append("Service is running: " + Config.isMyServiceRunning(this));
        zConsole.append("\n");
    }

    @Click(R.id.btnUpload)
    public void uploadActivity() {
        uploadActivityLogs();
    }

    @Background
    public void uploadActivityLogs() {
//        List<ActivityLog> logs = ActivityLogManager.getInstance().getAllLogs();
//        StringBuilder builder = new StringBuilder();
//        builder.append("[");
//        for (ActivityLog log : logs) {
//            builder.append(log.toString());
//            builder.append(",");
//        }
//        builder.setLength(builder.length() - 1);
//        builder.append("]");


        try {

            String json = gson.toJson(ActivityLogManager.getInstance().getAllLogs()).toString();
            Log.i(TAG, json);

            HttpPost httpPost = new HttpPost(Config.LINK_UPLOAD_ACTIVITYLOGS);
            httpPost.setEntity(new StringEntity(json));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse response = httpsClient.execute(httpPost);
            handleResponse(response);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @UiThread
    public void handleResponse(HttpResponse resp) {
        zConsole.setText("");
        zConsole.append(resp.getStatusLine().toString());
        zConsole.append("\n");
        for (Header header : resp.getAllHeaders()) {
            zConsole.append(header.toString());
            zConsole.append("\n");
        }


    }

    /**
     * Reload view, text, colors and other defaults
     */
    public void initView() {
        String deviceName = Bluetooth.getInstance().getBondenDevices().get(Config.getBluetoothMac());
        zDevice.setText(deviceName);
        zConsole.setMovementMethod(new ScrollingMovementMethod());
    }

    @CheckedChange(R.id.sw_local_server)
    void checkedSwitch(CompoundButton hello, boolean isChecked) {
        Log.i(TAG, "server: " + isChecked);
        if (isChecked) {
            Config.LINK_UPLOAD_ACTIVITYLOGS = Config.LINK_UPLOAD_ACTIVITYLOGS_SERVER;
            appendToConsole(Config.LINK_UPLOAD_ACTIVITYLOGS);
        } else {
            Config.readPropertiesFile(this);
            appendToConsole(Config.LINK_UPLOAD_ACTIVITYLOGS);
        }
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
                Config.setBluetoothMac(macs[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }


    //@Receiver(actions = "android.bluetooth.device.action.ACL_CONNECTED")
    protected void bluetoothConnected(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        logInfoAboutBluetoothDevice(intent, device);

        zConsole.append("connected to " + device.getName());
        zConsole.append("\n");

        if (StringUtils.equals(Config.getBluetoothMac(), device.getAddress())) {
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

    //@Receiver(actions = "android.bluetooth.device.action.ACL_DISCONNECTED")
    protected void bluetoothDisconnected(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        logInfoAboutBluetoothDevice(intent, device);

        zConsole.append("disconnected from " + device.getName());
        zConsole.append("\n");

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


    /**
     * Method to start the service
     */
    @Click(R.id.btnStartService)
    public void startService() {
        startService(new Intent(this, ActivityTrackingService_.class));
//        ActivityTrackingService_.intent(this).start();
    }

    /**
     * Method to stop the service
     */
    @Click(R.id.btnStopService)
    public void stopService() {
        stopService(new Intent(this, ActivityTrackingService_.class));
//        ActivityTrackingService_.intent(this).stop();
    }


    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        initView();
    }

    private void appendToConsole(String text){
        zConsole.append(text);
        zConsole.append("\n");
    }

}
