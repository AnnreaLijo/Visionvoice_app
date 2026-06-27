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

public class Family_member_register extends AppCompatActivity {
    EditText Name,Phone_No,Email,Password;
    Button Register;
    TextView Login;
    String SName,SPhone_No,SEmail,SPassword,status,message,url=config.baseurl+"userregistration.php";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_family_member_register);
        Name = findViewById(R.id.RegName);
        Phone_No = findViewById(R.id.RegPhno);
        Email = findViewById(R.id.RegEmail);
        Password = findViewById(R.id.RegPass);
        Register = findViewById(R.id.RegBut);
        Login = findViewById(R.id.RegLog);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(Family_member_register.this, MainActivity.class);
                startActivity(a);
            }
        });
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Reg();
            }
        });
    }

    private void Reg() {
        SName=Name.getText().toString();
        SPhone_No=Phone_No.getText().toString();
        SEmail=Email.getText().toString();
        SPassword=Password.getText().toString();
        if(TextUtils.isEmpty(SName)){
            Name.requestFocus();
            Name.setError("Enter  Name");
            return;
        }
        if(TextUtils.isEmpty(SPhone_No)){
            Phone_No.requestFocus();
            Phone_No.setError("Enter Phone No");
            return;
        }
        else if(!SPhone_No.matches("\\d{10}")){   // 10 digits only
            Phone_No.requestFocus();
            Phone_No.setError("Phone number must be 10 digits");
            return;
        }

        if(TextUtils.isEmpty(SEmail)){
            Email.requestFocus();
            Email.setError("Enter Email");
            return;
        }
        else if(!SEmail.endsWith(".com")){   // must end with .com
            Email.requestFocus();
            Email.setError("Email must end with .com");
            return;
        }
        if(TextUtils.isEmpty(SPassword)){
            Password.requestFocus();
            Password.setError("Enter Password");
            return;
        }
        else if(!SPassword.matches(".*[!@#$%^&*+=?-].*")){  // at least one special character
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
                        Toast.makeText(Family_member_register.this, String.valueOf(error), Toast.LENGTH_SHORT).show();
                    }

                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", SName);
                params.put("email",SEmail);
                params.put("phoneno",SPhone_No);
                params.put("password",SPassword);
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
            Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
            Intent i =new Intent(Family_member_register.this, Family_member_login.class);
            startActivity(i);
            finish();
        }

    }
}