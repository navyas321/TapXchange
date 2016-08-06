package com.navyas.android.tapexchange;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.util.List;


public class MainActivity extends ActionBarActivity implements NfcAdapter.CreateNdefMessageCallback{

    Button mSetInfoButton;
    Button mSendApkButton;
    TextView subtitle;
    JSONSerializer serializer;
    JSONObject obj;
    String apk = "tapexchange";
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subtitle = (TextView) findViewById(R.id.subtitle_text);

        android.support.v7.app.ActionBar menu = getSupportActionBar();

        TextView tv = new TextView(getApplicationContext());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT);

        tv.setLayoutParams(lp);
        tv.setText(menu.getTitle());
        tv.setTextColor(Color.WHITE);
        Typeface type = Typeface.createFromAsset(getAssets(),"Dashley.ttf");
        tv.setTypeface(type);
        tv.setTextSize(30);

        menu.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        menu.setCustomView(tv);

        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ictapxchangelauncher);
        menu.setDisplayUseLogoEnabled(true);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {

            NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
            final NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (mAdapter == null || mNfcAdapter == null) {
                return;
            }

            if (!mAdapter.isEnabled() || !mNfcAdapter.isEnabled()) {
                Toast.makeText(this, "Please Enable NFC", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            }

            mAdapter.setNdefPushMessageCallback(this, this);

            mSetInfoButton = (Button) findViewById(R.id.set_contact_info_btn);
            mSetInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), SetContactActivity.class));
                }
            });

            mSendApkButton = (Button) findViewById(R.id.send_apk);
            mSendApkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    final List pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
                    for (Object object : pkgAppsList) {
                        ResolveInfo info = (ResolveInfo) object;
                        File file = new File(info.activityInfo.applicationInfo.publicSourceDir);
                        //Log.e("TAGS",file.toString());
                        if (file.toString().contains(apk)) {
                            //Log.e("TAG0",file.toString());
                            Intent intent = new Intent(v.getContext(), SendApkActivity.class);
                            intent.putExtra("file", file);
                            startActivity(intent);

                        }
                    }
                }
            });
        }

        else {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_CONTACTS)) {
                Toast.makeText(this, "Permission Required To Write Contacts", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_CONTACTS}, REQUEST_CODE);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
            final NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (mAdapter == null || mNfcAdapter == null) {
                return;
            }

            if (!mAdapter.isEnabled() || !mNfcAdapter.isEnabled()) {
                Toast.makeText(this, "Please Enable NFC", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            }

            mAdapter.setNdefPushMessageCallback(this, this);

            mSetInfoButton = (Button) findViewById(R.id.set_contact_info_btn);
            mSetInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), SetContactActivity.class));
                }
            });

            mSendApkButton = (Button) findViewById(R.id.send_apk);
            mSendApkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    final List pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
                    for (Object object : pkgAppsList) {
                        ResolveInfo info = (ResolveInfo) object;
                        File file = new File(info.activityInfo.applicationInfo.publicSourceDir);
                        //Log.e("TAGS",file.toString());
                        if (file.toString().contains(apk)) {
                            //Log.e("TAG0",file.toString());
                            Intent intent = new Intent(v.getContext(), SendApkActivity.class);
                            intent.putExtra("file", file);
                            startActivity(intent);

                        }
                    }
                }
            });
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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



}


