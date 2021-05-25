package com.myapplicationdev.android.smsretrieverapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecondFragment extends Fragment {

    EditText etSMS;
    Button btnRetrieve;
    TextView tvSMS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        etSMS = view.findViewById(R.id.etWord);
        btnRetrieve = view.findViewById(R.id.btnRetrieve);
        tvSMS = view.findViewById(R.id.tvSMS);
        Context applicationContext = MainActivity.getContextOfApplication();
        btnRetrieve.setOnClickListener(v -> {
            Uri uri = Uri.parse("content://sms");
            String[] reqCols = new String[]{"date", "address", "body", "type"};
            ContentResolver cr = applicationContext.getContentResolver();
            String[] messages = new String[0];
            if (etSMS.getText().toString().contains(" ")) {
                messages = etSMS.getText().toString().split(" ");
            }
            String filter = "body LIKE ? ";
            String[] filterArgs = new String[messages.length];
            if (messages.length > 1) {
                for(int i = 0; i < messages.length; i++) {
                    filter += "OR body LIKE ? ";
                    filterArgs[i] = "%" + messages[i] + "%";
                }
            }
            Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
            String smsbody = "";
            if(cursor.moveToFirst()){
                do {
                    long dateInMillis = cursor.getLong(0);
                    String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                    String address = cursor.getString(1);
                    String body = cursor.getString(2);
                    String type = cursor.getString(3);
                    if(type.equalsIgnoreCase("1")) {
                        type = "Inbox:";
                    } else {
                        type = "Sent:";
                    }
                    smsbody += type + " " + address + "\nat " + date + "\n\"" + body + "\"\n\n";
                } while (cursor.moveToNext());
            }
            tvSMS.setText(smsbody);
        });
        return view;
    }
}