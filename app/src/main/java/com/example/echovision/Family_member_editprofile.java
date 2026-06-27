package com.example.echovision;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Family_member_editprofile extends AppCompatActivity {
    EditText Name,Phoneno,Email,Password;
    Button Update;


    String pname,pemail,pphn,ppass,url= config.baseurl+"profileupdate.php";
    String ppid,ppname,ppemail,ppphn,pppass,status,message;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.family_member_editprofile);

        Name=findViewById(R.id.editname);
        Phoneno=findViewById(R.id.editphone);
        Email=findViewById(R.id.editemail);
        Password=findViewById(R.id.editpassword);
        Update=findViewById(R.id.updatebutton);

        Intent intent = getIntent();



        ppid = intent.getStringExtra("id");
        pname = intent.getStringExtra("name");
        Name.setText(pname);

        pphn = intent.getStringExtra("phoneno");
        Phoneno.setText( pphn);

        pemail = intent.getStringExtra("email");
        Email.setText( pemail);



        ppass = intent.getStringExtra("password");
        Password.setText( ppass);

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submit();
            }
        });
        return ;
    }
    private void submit() {
        ppname=Name.getText().toString();
        ppphn=Phoneno.getText().toString();
        ppemail=Email.getText().toString();
        pppass=Password.getText().toString();

        if (TextUtils.isEmpty(pname)){
            Name.requestFocus();
            Name.setError("required field");
            return;
        }



        if (TextUtils.isEmpty(pphn)){
            Phoneno.requestFocus();
            Phoneno.setError("required field");
            return;
        }
        if (TextUtils.isEmpty(pemail)){
            Email.requestFocus();
            Email.setError("required field");
            return;
        }
        if (TextUtils.isEmpty(ppass)){
            Password.requestFocus();
            Password.setError("required field");
            return;
        }

        StringRequest str = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(Family_member_editprofile.this, response, Toast.LENGTH_SHORT).show();


                try {
                    JSONObject json = new JSONObject(response);
                    status = json.getString("status");
                    message = json.getString("message");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if ("0".equals(status)) {
                    Toast.makeText(Family_member_editprofile.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Family_member_editprofile.this, "updation successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Family_member_editprofile.this, Userhome.class));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Family_member_editprofile.this ,error.toString(), Toast.LENGTH_SHORT).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id",ppid);
                params.put("name",ppname);
                params.put("phoneno",ppphn);
                params.put("email",ppemail);
                params.put("password",pppass);
                return params;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(Family_member_editprofile.this);
        rq.add(str);
    }


}