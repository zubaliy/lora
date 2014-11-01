package com.athome.zubaliy.mylifeontheroad;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.athome.zubaliy.Util.Config;
import com.athome.zubaliy.bluetooth.Bluetooth;
import com.athome.zubaliy.sqlite.manager.ActivityLogManager;
import com.athome.zubaliy.sqlite.model.ActivityLog;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;

import java.util.GregorianCalendar;


@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {
    private static final String TAG = "zMainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "Activity started");

        Log.i(TAG, Bluetooth.getInstance().getBondenDevices());

        ActivityLogManager.init(this);


    }


//    @Click(R.id.btn_connected)
    @Receiver(actions = "android.bluetooth.device.action.ACL_CONNECTED")
    protected void connected() {
        Toast.makeText(this, "connected", Toast.LENGTH_LONG).show();
        Log.i(TAG, "connected");

        GregorianCalendar calendar = new GregorianCalendar();

        ActivityLog log = new ActivityLog(calendar.getTime());
        ActivityLogManager.getInstance().addLog(log);

        Log.i(TAG, ActivityLogManager.getInstance().getLastLog().toString());
    }

//    @Click(R.id.btn_disconnected)
    @Receiver(actions = "android.bluetooth.device.action.ACL_DISCONNECTED")
    protected void disconnected() {
        Toast.makeText(this, "disconnected", Toast.LENGTH_LONG).show();
        Log.i(TAG, "disconnected");

        GregorianCalendar calendar = new GregorianCalendar();

        ActivityLog log = ActivityLogManager.getInstance().getLastLog();
        log.setDisconnected(calendar.getTime());
        log.setDifference((int) (log.getDisconnected().getTime() - log.getConnected().getTime()));
        ActivityLogManager.getInstance().updateLog(log);

        Log.i(TAG, ActivityLogManager.getInstance().getLastLog().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

}
