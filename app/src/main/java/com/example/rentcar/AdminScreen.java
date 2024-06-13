package com.example.rentcar;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
public class AdminScreen extends AppCompatActivity {

    private Button btnAdminLogin;
    private Button btnAdminSignUp;
    private EditText edtTxtUsername;
    private EditText edtTxtPassword;
    private ImageButton homeBtn;

    private static final String PREFS_NAME = "AdminPrefs";
    private static final String KEY_USERNAME = "adminUsername";
    private static final String KEY_PASSWORD = "adminPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);
        setupViews();
        goToSignUpScreen();

        // Restore saved username and password
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString(KEY_USERNAME, "");
        String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
        edtTxtUsername.setText(savedUsername);
        edtTxtPassword.setText(savedPassword);

        btnAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminLogin();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminScreen.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupViews() {
        btnAdminLogin = findViewById(R.id.btnAdminLogin);
        btnAdminSignUp = findViewById(R.id.btnAdminSignUp);
        edtTxtUsername = findViewById(R.id.edtTxtUsername);
        edtTxtPassword = findViewById(R.id.edtTxtPassword);
        homeBtn = findViewById(R.id.homeBtn);
    }

    public void goToSignUpScreen() {
        btnAdminSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminScreen.this, SignUpAdminScreen.class);
                startActivity(intent);
            }
        });
    }

    private void adminLogin() {
        final String username = edtTxtUsername.getText().toString().trim();
        final String password = edtTxtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(AdminScreen.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://172.19.0.120/CarRental/AdminLogin.php";

        Log.d("AdminLogin", "URL: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AdminLogin", "Response: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        // Save username in SharedPreferences
                        saveUsername(username);

                        // Navigate to HomeActivity or Admin dashboard
                        Intent intent = new Intent(AdminScreen.this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(AdminScreen.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AdminScreen.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(AdminScreen.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void saveUsername(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }
}
