package com.example.echovision;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
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

public class Family_member_login extends AppCompatActivity {
    EditText Email,Password;
    Button Login;
    TextView Register;
    String Semail,Spassword,status,message,url=config.baseurl+"userlogin.php";

    String Lid,Lname,Lphone, Lemail,Lpassword;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_family_member_login);
        Email=findViewById(R.id.loginemail);
        Password=findViewById(R.id.loginpassword);
        Login=findViewById(R.id.loginbutton);
        Register=findViewById(R.id.loginregister);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a=new Intent(Family_member_login.this, Family_member_register.class);
                startActivity(a);
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log();
            }
        });
    }

    private void log() {
        Semail=Email.getText().toString();
        Spassword=Password.getText().toString();
        if(TextUtils.isEmpty(Semail)){
            Email.requestFocus();
            Email.setError("Enter your Email");
            return;
        }
        else if(!Semail.endsWith(".com")){   // must end with .com
            Email.requestFocus();
            Email.setError("Email must end with .com");
            return;
        }
        if(TextUtils.isEmpty(Spassword)){
            Password.requestFocus();
            Password.setError("Enter your Password");
            return;
        }
        else if(!Spassword.matches(".*[!@#$%^&*+=?-].*")){  // at least one special character
            Password.requestFocus();
            Password.setError("Password must include at least one special character");
            return;
        }
        StringRequest StringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //  Toast.makeText(Register.this, response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject c = new JSONObject(response);
                            status = c.getString("status");
                            message = c.getString("message");
                            Lid=c.getString("id");
                            Lname=c.getString("name");
                            Lphone=c.getString("phoneno");
                            Lemail=c.getString("email");
                            Lpassword=c.getString("password");
                            checklogin();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //run cheyikkumbo error indo ennu nokkan
                        Toast.makeText(Family_member_login.this, String.valueOf(error), Toast.LENGTH_SHORT).show();
                    }

                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", Semail);
                params.put("password", Spassword);
                return params;
            }


        };

        //string reqt ne execute cheyan aanu requestqueue
        Volley volley =  null;
        RequestQueue requestQueue = volley.newRequestQueue(this);
        requestQueue.add(StringRequest);
    }


    private void checklogin() {
        if (status.equals("0")){
            Toast.makeText(this, "Invalied", Toast.LENGTH_SHORT).show();
        }else {
            new com.example.echovision.SessionManager(Family_member_login.this).createLoginSession(Lid,Lname,Lphone,Lemail,Lpassword);
            Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
            Intent i =new Intent(Family_member_login.this,Userhome.class);
            startActivity(i);
            finish();
        }

    }
}
