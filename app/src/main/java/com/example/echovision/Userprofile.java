package com.example.echovision;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class Userprofile extends AppCompatActivity {
    TextView name,email,phone_no,password;
    ImageView edit;
    String ppid,pphone,ppassword,pname,pemail;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_userprofile);

        name = findViewById(R.id.ProName);
        phone_no = findViewById(R.id.ProPhone);
        email = findViewById(R.id.ProEmail);
        password = findViewById(R.id.ProPass);
        edit = findViewById(R.id.ProEdit);

        HashMap<String, String> data = new SessionManager(Userprofile.this).getUserDetails();
        ppid = data.get("id");
        pname = data.get("name");
        pphone = data.get("phoneno");
        pemail = data.get("email");
        ppassword = data.get("password");

        name.setText(pname);
        phone_no.setText(pphone);
        email.setText(pemail);

        password.setText(ppassword);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Userprofile.this, Family_member_editprofile.class);
                intent.putExtra("id", ppid);
                intent.putExtra("name", pname);
                intent.putExtra("phoneno", pphone);
                intent.putExtra("email", pemail);
                intent.putExtra("password", ppassword);
                startActivity(intent);
            }
        });
    }
}