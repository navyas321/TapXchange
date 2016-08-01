package com.navyas.android.tapexchange;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class SendApkActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_apk);

        android.support.v7.app.ActionBar menu = getSupportActionBar();

        TextView tv = new TextView(getApplicationContext());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT);

        tv.setLayoutParams(lp);
        tv.setText("TapXchange");
        tv.setTextColor(Color.WHITE);
        Typeface type = Typeface.createFromAsset(getAssets(),"Dashley.ttf");
        tv.setTypeface(type);
        tv.setTextSize(30);

        menu.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        menu.setCustomView(tv);

        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ictapxchangelauncher);
        menu.setDisplayUseLogoEnabled(true);

        PackageManager pm = this.getPackageManager();
        // Check whether NFC is available on device
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device.
            Toast.makeText(this, "The device does not has NFC hardware.",
                    Toast.LENGTH_SHORT).show();
        }
        // Check whether device is running Android 4.1 or higher
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            // Android Beam feature is not supported.
            Toast.makeText(this, "Android Beam is not supported.",
                    Toast.LENGTH_SHORT).show();
        }


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Check whether NFC is enabled on device
        if(!nfcAdapter.isEnabled()){
            // NFC is disabled, show the settings UI
            // to enable NFC
            Toast.makeText(this, "Please enable NFC.",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        // Check whether Android Beam feature is enabled on device
        else if(!nfcAdapter.isNdefPushEnabled()) {
            // Android Beam is disabled, show the settings UI
            // to enable Android Beam
            Toast.makeText(this, "Please enable Android Beam.",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }
        else {
            // NFC and Android Beam both are enabled

            File fileToTransfer = (File) getIntent().getExtras().get("file");
            fileToTransfer.setReadable(true, false);
            //Log.e("TAG",fileToTransfer.toString());
            nfcAdapter.setBeamPushUris(
                    new Uri[]{Uri.fromFile(fileToTransfer)}, this);
        }
    }


}
