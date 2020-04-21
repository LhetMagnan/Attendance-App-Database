package com.example.studentattendanceapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.database.Cursor;

public class MainActivity extends AppCompatActivity {

    StudentDatabase myDatabase;
    EditText emailText,nameText,passwordText,idText;
    Button readData,saveData,updateData,deleteData;

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
        save();
        read();
        update();
        delete();

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
