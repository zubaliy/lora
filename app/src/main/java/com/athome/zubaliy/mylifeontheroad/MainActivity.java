package com.athome.zubaliy.mylifeontheroad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.athome.zubaliy.bluetooth.Bluetooth;
import com.athome.zubaliy.sqlite.manager.ActivityLogManager;
import com.athome.zubaliy.util.AndroidDatabaseManager;
import com.athome.zubaliy.util.Utils;
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
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.util.Map;

/**
 * The main activity, where all starts.
 */

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @ViewById(R.id.txt_bluetooth_device)
    public TextView zDevice;

    @ViewById(R.id.txt_console)
    public TextView zConsole;

    @HttpsClient
    public HttpClient httpsClient;

    private String url;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Activity started");

        url = BuildConfig.LINK_UPLOAD_ACTIVITYLOGS_LOCAL;
        gson = new GsonBuilder().setDateFormat(BuildConfig.SDF.toPattern()).create();

        ActivityLogManager.init(this);
    }

    @LongClick(R.id.tekst)
    public void showDbManager() {
        startActivity(new Intent(this, AndroidDatabaseManager.class));
    }


    @Click(R.id.btnUpload)
    public void uploadActivity() {
        uploadActivityLogs();
    }

    @Background
    public void uploadActivityLogs() {
        try {
            String json = gson.toJson(ActivityLogManager.getInstance().getAllLogs());
            Log.i(TAG, json);

            HttpPost httpPost = new HttpPost(url);
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
        appendToConsole(resp.getStatusLine().toString());
        for (Header header : resp.getAllHeaders()) {
            appendToConsole(header.toString());
        }
    }

    /**
     * Reload view, text, colors and other defaults
     */
    public void initView() {
        String chosenDeviceMacAddress = Utils.readPreferences(BuildConfig.KEY_DEVICE_MAC_ADDRESS, "00:00:00:00:00:00");
        String deviceName = Bluetooth.getInstance().getBondedDevices().get(chosenDeviceMacAddress);
        zDevice.setText(deviceName);

        appendToConsole(deviceName + " - " + chosenDeviceMacAddress);
        // Because zConsole and other UI components are only initialized after onCreate(),
        // so could not be used directly in onCreate()
        zConsole.setMovementMethod(new ScrollingMovementMethod());
    }

    @CheckedChange(R.id.sw_local_server)
    void checkedSwitch(CompoundButton hello, boolean isChecked) {
        url = isChecked ? BuildConfig.LINK_UPLOAD_ACTIVITYLOGS_SERVER : BuildConfig.LINK_UPLOAD_ACTIVITYLOGS_LOCAL;
        Log.i(TAG, "Switching to URL (" + (isChecked ? "REMOTE" : "LOCAL") +"): " + url);
        appendToConsole(url);
    }

    @LongClick(R.id.txt_bluetooth_device)
    public void selectDevice() {
        Map<String, String> devices = Bluetooth.getInstance().getBondedDevices();
        final String[] macs = devices.keySet().toArray(new String[devices.size()]);
        final String[] names = devices.values().toArray(new String[devices.size()]);

        new AlertDialog.Builder(this).setTitle("Select device")
                                     .setItems(names, new DialogInterface.OnClickListener() {
                                         public void onClick(DialogInterface dialog, int item) {
                                             // Do something with the selection
                                             Utils.savePreferences(BuildConfig.KEY_DEVICE_MAC_ADDRESS, macs[item]);
                                             zDevice.setText(names[item]);
                                         }
                                     })
                                     .show();
    }

    @LongClick(R.id.txt_settings)
    public void showSettings(){
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        initView();
    }

    private void appendToConsole(String text) {
        zConsole.append(text);
        zConsole.append("\n");
    }

}
