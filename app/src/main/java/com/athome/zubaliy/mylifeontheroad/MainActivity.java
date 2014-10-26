package com.athome.zubaliy.mylifeontheroad;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.provider.CalendarContract.Events;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;


@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        initNfc();
        enableTagInterception();
    }

    private void enableTagInterception() {
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
            ndef.addCategory("DEFAULT");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);


        intentFiltersArray = new IntentFilter[]{ndef, tech};

        String[] techLists1 = new String[]{Ndef.class.getName(), NfcA.class.getName(),
                MifareUltralight.class.getName()};
        String[] techLists2 = new String[]{NdefFormatable.class.getName(), NfcA.class.getName(),
                MifareUltralight.class.getName()};
        techList = new String[][]{techLists1, techLists2};

    }

    private void initNfc() {
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is enabled.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enable NFC.", Toast.LENGTH_SHORT).show();
        }

    }

    private void handleIntent(Intent nfcIntent) {
        Toast.makeText(this, "New intent detected", Toast.LENGTH_SHORT).show();


        addCalendarEvent();


    }

    @Click(R.id.btn_add_reminder)
    public void addCalendarEvent() {
        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setType("vnd.android.cursor.item/event");
        calIntent.putExtra(Events.TITLE, "Finish your work");
        calIntent.putExtra(Events.DESCRIPTION, "I already worked 8 hours. It is enough for to day. I have to finish "
                + "my work.");


        GregorianCalendar calDate = new GregorianCalendar();
        calDate.add(GregorianCalendar.HOUR_OF_DAY, 8);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calDate.getTimeInMillis());
        calDate.add(Calendar.MINUTE, 30);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calDate.getTimeInMillis());

        calIntent.setData(Events.CONTENT_URI);

        startActivity(calIntent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
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
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    public void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techList);
        }
    }


}
