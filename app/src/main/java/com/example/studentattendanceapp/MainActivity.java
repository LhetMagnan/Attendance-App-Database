package com.example.studentattendanceapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.database.Cursor;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String  Channel_ID = "personal_notification";
    public static final int Notification_ID = 001;
    public static final String TXT_REPLY= "text reply";



    StudentDatabase myDatabase;
    EditText emailText,nameText,passwordText,idText;
    Button readData,saveData,updateData,deleteData, buttonScan;
    //qr code scanner object
    private IntentIntegrator qrScan;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDatabase= new StudentDatabase(this);
        nameText= (EditText) findViewById(R.id.studentName);
        emailText= (EditText) findViewById(R.id.email);
        passwordText= (EditText) findViewById(R.id.password);
        idText= (EditText) findViewById(R.id.studentID);
        saveData= (Button) findViewById(R.id.save);
        readData=(Button) findViewById(R.id.read);
        updateData=(Button) findViewById(R.id.update);
        deleteData=(Button) findViewById(R.id.delete);
        buttonScan = (Button) findViewById(R.id.buttonScan);

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        //attaching onclick listener
        buttonScan.setOnClickListener(this);
        save();
        read();
        update();
        delete();

    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    idText.setText(obj.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();

                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();

                    //Display the qrcode result in the Tap Student ID card
                    idText.setText(result.getContents());
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
//initiating the qr code scan
        qrScan.initiateScan();
    }


    public void notification(View view) {
        CreateNotificationChannel();

        Intent landingIntent = new Intent(this,LandingActivity.class);
        landingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent landingPendingIntent = PendingIntent.getActivity(this,0,landingIntent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,Channel_ID);
        builder.setSmallIcon(R.drawable.ic_message_notification);
        builder.setContentTitle("Daily reminder");
        builder.setContentText("It's time to study");

        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(landingPendingIntent);
        builder.setAutoCancel(true);


        RemoteInput remoteInput = new RemoteInput.Builder(TXT_REPLY).setLabel("Reply").build();
        Intent replyIntent = new Intent(this,RemoteReceiver.class);
        replyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent replyPendingIntent = PendingIntent.getActivity(this,0,replyIntent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_message_notification,"Reply",replyPendingIntent).addRemoteInput(remoteInput).build();
        builder.addAction(action);
        NotificationManagerCompat  notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(Notification_ID,builder.build());
    }


    private void CreateNotificationChannel () {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            CharSequence name = "Personal Notification";
            String description = "To include all Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel NC = new NotificationChannel(Channel_ID, name, importance);
            NC.setDescription(description);
            NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NM.createNotificationChannel(NC);

        }
    }

    public void delete(){
        deleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer deleted = myDatabase.deletedata(idText.getText().toString());
                if(deleted>0){
                    Toast.makeText(MainActivity.this,"Data Deleted successfully",Toast.LENGTH_LONG).show();


                }
                else {
                    Toast.makeText(MainActivity.this,"Data not deleted recorded",Toast.LENGTH_LONG).show();

                }

            }
        });
    }
    public void update(){
        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isupdated= myDatabase.updatedata(idText.getText().toString(),nameText.getText().toString(),emailText.getText().toString(),passwordText.getText().toString());
                if(isupdated==true){
                    Toast.makeText(MainActivity.this,"Data updated successfully",Toast.LENGTH_LONG).show();


                }
                else {
                    Toast.makeText(MainActivity.this,"Data not updated recorded",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void save(){
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result= myDatabase.adddata(nameText.getText().toString(),emailText.getText().toString(),passwordText.getText().toString());
                if(result==true){
                    Toast.makeText(MainActivity.this,"Data recorded successfully",Toast.LENGTH_LONG).show();

                }
                else {
                    Toast.makeText(MainActivity.this,"Data not successfully recorded",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void read(){
        readData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer buffer = new StringBuffer();
                Cursor res = myDatabase.showdata();
                if (res.getCount()==0){
                    showmessage("Error","no data");
                    return;
                }
                while (res.moveToNext()){
                    buffer.append("Id:"+res.getString(0)+"\n");
                    buffer.append("name:"+res.getString(1)+"\n");
                    buffer.append("email:"+res.getString(2)+"\n");
                    buffer.append("password:"+res.getString(3)+"\n\n");

                }
                showmessage("Data",buffer.toString());

            }
        });
    }
    public void showmessage(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.show();
    }






}
