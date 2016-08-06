package com.navyas.android.tapexchange;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class NFCDisplayActivity extends ActionBarActivity {

    TextView fullName, phoneNum, emailAddr, nickname, organization;
    Button mSaveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcdisplay);

        android.support.v7.app.ActionBar menu = getSupportActionBar();

        TextView tv = new TextView(getApplicationContext());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT);

        tv.setLayoutParams(lp);
        tv.setText("Save Contact Info");
        tv.setTextColor(Color.WHITE);
        Typeface type = Typeface.createFromAsset(getAssets(),"Dashley.ttf");
        tv.setTypeface(type);
        tv.setTextSize(30);

        menu.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        menu.setCustomView(tv);

        menu.setDisplayHomeAsUpEnabled(true);

        fullName = (TextView) findViewById(R.id.full_name_text2);
        phoneNum = (TextView) findViewById(R.id.phone_num_text2);
        emailAddr = (TextView) findViewById(R.id.email_text2);
        nickname = (TextView) findViewById(R.id.home_addr_text2);
        organization = (TextView) findViewById(R.id.organization_text2);

        mSaveButton = (Button) findViewById(R.id.save_button);
            mSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ContextCompat.checkSelfPermission(NFCDisplayActivity.this,
                            Manifest.permission.WRITE_CONTACTS)
                            == PackageManager.PERMISSION_GRANTED ) {
                        WritePhoneContact(fullName.getText() + "", phoneNum.getText() + "", emailAddr.getText() + "", nickname.getText() + "", organization.getText() + "");
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();

                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(NFCDisplayActivity.this, Manifest.permission.WRITE_CONTACTS)) {
                            Toast.makeText(v.getContext(), "Permission Required To Write Contacts", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        }





    @Override
    protected void onResume()
    {
        super.onResume();

        Intent intent = getIntent();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
        {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage message = (NdefMessage) rawMessages[0];
            String encodedContact = new String(message.getRecords()[0].getPayload());

            String[] contactItems = encodedContact.split("\n");


            fullName.setText(contactItems[0]);
            phoneNum.setText(contactItems[1]);
            emailAddr.setText(contactItems[2]);
            nickname.setText(contactItems[3]);
            organization.setText(contactItems[4]);
        }
    }



    public void WritePhoneContact(String displayName, String number, String email, String nickname, String organization)
    {
        Context context = getApplicationContext(); //Application's context or Activity's context

        ArrayList<ContentProviderOperation> cntProOper = new ArrayList<ContentProviderOperation>();
        int contactIndex = cntProOper.size();//ContactSize

        //Newly Inserted contact
        // A raw contact will be inserted ContactsContract.RawContacts table in contacts database.
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)//Step1
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        //Display name will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step2
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, displayName) // Name of the contact
                .build());

        //Mobile number will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step 3
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number) // Number to be added
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()); //Type like HOME, MOBILE etc

        //Email will be inserted as well
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step2
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, null)
                .build());

        // Insert nickname into contact
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step2
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Nickname.NAME, nickname).build());

        // Organization
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step2
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, organization)
                .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                .build());
        try
        {
            // We will do batch operation to insert all above data
            //Contains the output of the app of a ContentProviderOperation.
            //It is sure to have exactly one of uri or count set
            ContentProviderResult[] contentProresult = null;
            contentProresult = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, cntProOper); //apply above data insertion into contacts list
            Toast.makeText(getApplicationContext(), "Contact successfully saved", Toast.LENGTH_SHORT).show();
        }
        catch (RemoteException exp)
        {
            Toast.makeText(getApplicationContext(), "Something went wrong, yo", Toast.LENGTH_SHORT).show();
        }
        catch (OperationApplicationException exp)
        {
            Toast.makeText(getApplicationContext(), "Something went wrong, yo", Toast.LENGTH_SHORT).show();
        }
    }

}
