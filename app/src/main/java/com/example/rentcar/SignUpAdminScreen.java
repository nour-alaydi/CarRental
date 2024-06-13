package com.example.rentcar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class SignUpAdminScreen extends AppCompatActivity {

    private EditText username;
    private EditText adminemail;
    private EditText edtTxtPassword;
    private Button btnAdminSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_admin_screen);

        setupView();
        btnAdminSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpAdmin();
            }
        });
    }

    private void setupView() {
        username = findViewById(R.id.username);
        adminemail = findViewById(R.id.adminemail);
        edtTxtPassword = findViewById(R.id.edtTxtPassword);
        btnAdminSignUp = findViewById(R.id.btnAdminSignUp);
    }

    private void signUpAdmin() {
        final String usernameStr = username.getText().toString().trim();
        final String emailStr = adminemail.getText().toString().trim();
        final String passwordStr = edtTxtPassword.getText().toString().trim();

        if (usernameStr.isEmpty() || emailStr.isEmpty() || passwordStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://172.19.0.120/CarRental/AdminSignUp.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    Toast.makeText(SignUpAdminScreen.this, message, Toast.LENGTH_SHORT).show();

                    if (status.equals("success")) {
                        Intent intent = new Intent(SignUpAdminScreen.this, AdminScreen.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignUpAdminScreen.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(SignUpAdminScreen.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", usernameStr);
                params.put("email", emailStr);
                params.put("password", passwordStr);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
