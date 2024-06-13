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

public class CustomerScreen extends AppCompatActivity {

    private Button btnCustomerLogin;
    private Button btnCustomerSignUp;
    private EditText edtTxtID;
    private EditText edtTxtPassword;
    private ImageButton homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_screen);
        setupViews();
        GoToSignUpScreen();

        // Retrieve and set the ID number if it exists in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String savedID = sharedPreferences.getString("idNumber", "");
        edtTxtID.setText(savedID);

        btnCustomerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginCustomer();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerScreen.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupViews() {
        btnCustomerLogin = findViewById(R.id.btnCustomerLogin);
        btnCustomerSignUp = findViewById(R.id.btnCustomerSignUp);
        edtTxtID = findViewById(R.id.edtTxtID);
        edtTxtPassword = findViewById(R.id.edtTxtPassword);
        homeBtn = findViewById(R.id.homeBtn);
    }

    public void GoToSignUpScreen() {
        btnCustomerSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerScreen.this, CustomerSignUp.class);
                startActivity(intent);
            }
        });
    }

    public void loginCustomer() {
        final String idNumber = edtTxtID.getText().toString().trim();
        final String password = edtTxtPassword.getText().toString().trim();

        if (idNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(CustomerScreen.this, "Please enter both ID number and password", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://172.19.0.120/CarRental/CustomerLogin.php";
        Log.d("CustomerLogin", "URL: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("CustomerLogin", "Response: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {

                        saveIDNumber(idNumber);

                        Intent intent = new Intent(CustomerScreen.this, CustomersPage.class);
                        intent.putExtra("customerID", idNumber);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(CustomerScreen.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CustomerScreen.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(CustomerScreen.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idNumber", idNumber);
                params.put("password", password);
                System.out.println("HERE" + idNumber + "" + password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(CustomerScreen.this);
        requestQueue.add(stringRequest);
    }

    private void saveIDNumber(String idNumber) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("idNumber", idNumber);
        editor.apply();
    }
}
