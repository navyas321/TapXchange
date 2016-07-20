package com.gankmobile.android.tapexchange;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;


public class MainActivity extends ActionBarActivity implements NfcAdapter.CreateNdefMessageCallback{

    Button mSetInfoButton;
    TextView subtitle;
    JSONSerializer serializer;
    JSONObject obj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subtitle = (TextView) findViewById(R.id.subtitle_text);


        NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mAdapter == null)
        {
            return;
        }

        if(!mAdapter.isEnabled())
        {
            Toast.makeText(this, "Enable this shit yo", Toast.LENGTH_SHORT).show();
        }


        mAdapter.setNdefPushMessageCallback(this, this);

        mSetInfoButton = (Button) findViewById(R.id.set_contact_info_btn);
        mSetInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SetContactActivity.class));
            }
        });
    }


    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent)
    {
        serializer = new JSONSerializer(getApplicationContext(), "encoded_contact_info.json", new String[] {"code"});
        boolean successfulLoad = ObtainJSONObject();

        String encodedContactInfo = ObtainString("code");

        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", encodedContactInfo.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);

        return ndefMessage;
    }

    private String ObtainString(String key)
    {
        try
        {
            return obj.getString(key);
        }
        catch (Exception e)
        {
            Log.e("load_file", "JSON Object key: " + key + " not successfully loaded");
            return null;
        }
    }

    private boolean ObtainJSONObject()
    {
        try
        {
            obj = serializer.loadFile();
            Log.d("load_file", "File Successfully loaded");
            return true;
        }
        catch (Exception e)
        {
            Log.e("load_file", "File Not Loaded Successfully");
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
