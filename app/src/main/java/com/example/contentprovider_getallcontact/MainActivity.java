package com.example.contentprovider_getallcontact;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<User> arrayList;
    ArrayAdapter adt;
    Button btnClick;
    ListView lvName;
    int MY_PERMISSION_REQUEST_CODE_READ_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList = new ArrayList<>();

        btnClick = findViewById(R.id.btnClick);
        lvName = findViewById(R.id.lvName);

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            permissonReadContact();
            Uri uri = Uri.parse("content://com.android.contacts/data/phones");
               // Uri uri = Uri.parse("content://contacts/people/");


//                Cursor cursor = getContentResolver().query(
//                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                        null,null, null, null);

               addContact("123","456","0337194150","home456","123456@gmail.com");
                Cursor cursor = getContentResolver().query(
                        uri,
                        null,null, null, null);

                //int count = cursor.getCount();

                while (cursor.moveToNext()){

                    String idName = ContactsContract.Contacts.DISPLAY_NAME;
                    int colNameIndex = cursor.getColumnIndex(idName);
                    String name = cursor.getString(colNameIndex);


                    String idPhone = ContactsContract.CommonDataKinds.Phone.NUMBER;
                    int colPhoneIndex = cursor.getColumnIndex(idPhone);
                    String phone = cursor.getString(colPhoneIndex);

                    User user = new User(name, phone);
                    arrayList.add(user);
                }
                cursor.close();
                adt.notifyDataSetChanged();
            }
        });

        Toast.makeText(this, "Finish!", Toast.LENGTH_SHORT).show();
        
        adt = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, arrayList);

        lvName.setAdapter(adt);

    }
    private void permissonReadContact(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int readContactPermisson = ActivityCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.WRITE_CONTACTS);
            if(readContactPermisson != PackageManager.PERMISSION_GRANTED){
                MainActivity.this.requestPermissions(
                        new String[]{
                                Manifest.permission.WRITE_CONTACTS},
                        MY_PERMISSION_REQUEST_CODE_READ_CONTACT
                );
                return;
            }
        }
    }

    private void addContact(String given_name, String name, String mobile, String home, String email) {
        ArrayList<ContentProviderOperation> contact = new ArrayList<ContentProviderOperation>();
        contact.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // first and last names
        contact.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, given_name)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, name)
                .build());

        // Contact No Mobile
        contact.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobile)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        // Contact Home
        contact.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, home)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());

        // Email    `
        contact.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());

        try {
            ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, contact);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}