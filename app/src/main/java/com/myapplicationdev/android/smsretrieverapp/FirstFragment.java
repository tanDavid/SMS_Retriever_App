package com.myapplicationdev.android.smsretrieverapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import android.content.ContentResolver;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class FirstFragment extends Fragment {
    EditText etNumber;
    Button btnRetrieve, btnEmail;
    TextView tvSMS;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        etNumber = view.findViewById(R.id.etNumber);
        btnRetrieve = view.findViewById(R.id.btnRetrieve);
        tvSMS = view.findViewById(R.id.tvSMS);
        btnEmail = view.findViewById(R.id.btnEmail);

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//Create all messages uri
                Uri uri = Uri.parse("content://sms");

                //the columns we want
                //date is when the message took place
                // address is the no. of the other party
                // body is the message content
                // type 1 is received, type 2 is sent
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                //get content resolver obj form which to query the content provider
                Context applicationContext = MainActivity.getContextOfApplication();
                ContentResolver cr = applicationContext.getContentResolver();

                String filter = null;
                String[] filterArgs = null;

                if (!tvSMS.getText().toString().isEmpty()) {
                    filter = "address LIKE ?";
                    filterArgs = new String[]{"%" + etNumber.getText().toString() + "%"};
                }

                //fetch sms messafe frm built in content provider
                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
                String smsBody = "";

                if(cursor.moveToFirst()){
                    do{
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if(type.equalsIgnoreCase("1")){
                            type = "Inbox";
                        }else{
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date + "\n\"" + body + "\"\n\n";
                    }while (cursor.moveToNext());
                }
                tvSMS.setText(smsBody);
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                // Put essentials like email address, subject & body text
                email.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{"andy_tao@rp.edu.sg"});
                email.putExtra(Intent.EXTRA_SUBJECT,
                        "Enter Subject");
                email.putExtra(Intent.EXTRA_TEXT,
                        tvSMS.getText());
                // This MIME type indicates email
                email.setType("message/rfc822");
                // createChooser shows user a list of app that can handle
                // this MIME type, which is, email
                startActivity(Intent.createChooser(email,
                        "Choose an Email client :"));
            }
        });

        return view;
    }

}