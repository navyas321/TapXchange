package com.navyas.android.tapexchange;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;


public class SetContactActivity extends ActionBarActivity {

    Button mSetInfoButton;
    EditText fullName, phoneNum, emailAddr, nickname, organization;
    String[] contactInfo = new String[5];
    //String mFullName, mPhoneNum, mEmailAddr, mNickname, mOrganization;

    String encodedContactInfo = "";

    JSONSerializer serializer;
    JSONObject obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_contact);

        android.support.v7.app.ActionBar menu = getSupportActionBar();

        TextView tv = new TextView(getApplicationContext());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT);

        tv.setLayoutParams(lp);
        tv.setText("Set Contact Info");
        tv.setTextColor(Color.WHITE);
        Typeface type = Typeface.createFromAsset(getAssets(),"Dashley.ttf");
        tv.setTypeface(type);
        tv.setTextSize(30);

        menu.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        menu.setCustomView(tv);

        menu.setDisplayHomeAsUpEnabled(true);


        serializer = new JSONSerializer(getApplicationContext(), "encoded_contact_info.json", new String[] {"code"});
        boolean successfulLoad = ObtainJSONObject();

        fullName = (EditText) findViewById(R.id.full_name_text);
        phoneNum = (EditText) findViewById(R.id.phone_num_text);
        emailAddr = (EditText) findViewById(R.id.email_text);
        nickname  = (EditText) findViewById(R.id.home_addr_text);
        organization = (EditText) findViewById(R.id.organization_text);

        String encodedContact = ObtainString("code");
        if(encodedContact != null)
        {
            String[] contactItems = encodedContact.split("\n");
            fullName.setText(contactItems[0]);
            phoneNum.setText(contactItems[1]);
            emailAddr.setText(contactItems[2]);
            nickname.setText(contactItems[3]);
            organization.setText(contactItems[4]);
        }
//            for(int i = 0; i < contactItems.length; i++)
//            {
//                if(contactItems[i].equals("&"))
//                {
//                    contactItems[i] = "";
//                }
//            }


        mSetInfoButton = (Button) findViewById(R.id.set_button);
        mSetInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactInfo[0] =  fullName.getText().toString();
                contactInfo[1] = phoneNum.getText().toString();
                contactInfo[2] = emailAddr.getText().toString();
                contactInfo[3] = nickname.getText().toString();
                contactInfo[4] = organization.getText().toString();

                for(int i = 0; i < contactInfo.length; i++)
                {
                    if(!contactInfo[i].equals(""))
                    {
                        encodedContactInfo += contactInfo[i];
                    }
                    else
                    {
                        encodedContactInfo += " ";
                    }

                    encodedContactInfo += "\n";
                }


                boolean dataSaved = saveData(serializer);

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();

            }
        });
    }
    private boolean saveData(JSONSerializer serializer)
    {
        try
        {
            serializer.saveFile(new String[] {encodedContactInfo});
            Log.d("save_file", "Intent info saved to file");
            return true;
        }
        catch (Exception e)
        {
            Log.e("save_file", "File save unsuccessful");
            return false;
        }
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
