package com.example.studentattendanceapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.app.RemoteInput;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

public class RemoteReceiver extends AppCompatActivity {

    private TextView textView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_receiver);

        textView = findViewById(R.id.receiver);
        Bundle remoteNotif = RemoteInput.getResultsFromIntent(getIntent());
        if(remoteNotif != null){
            String message = remoteNotif.getCharSequence(MainActivity.TXT_REPLY).toString();
            textView.setText(message);
        }

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(MainActivity.Notification_ID);
    }
}
