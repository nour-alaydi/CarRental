package com.example.rentcar;

import android.content.Intent;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ReqDetailsActivity extends AppCompatActivity {
    private EditText txtCar, txtUser, txtSDate, txtEDate, txtPrice;
    private Button btConfirm;
    private RequestQueue queue;
    private int rentalID;
    private ImageButton btVhome, btView, bt_Vrep, btVupdate, btVpay, btReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.req_details);
        btVhome = findViewById(R.id.bt_Vhome);
        bt_Vrep = findViewById(R.id.bt_Vrep);
        btVupdate = findViewById(R.id.bt_Vupdate);
        btVpay = findViewById(R.id.bt_Vpay);
        btReturn = findViewById(R.id.bt_return);
        btView=findViewById(R.id.bt_view);
        bt_Vrep=findViewById(R.id.bt_Vrep);

        txtCar = findViewById(R.id.txt_car);
        txtUser = findViewById(R.id.txt_user);
        txtSDate = findViewById(R.id.txt_sdate);
        txtEDate = findViewById(R.id.txt_edate);
        txtPrice = findViewById(R.id.txt_price);
        btConfirm = findViewById(R.id.bt_confirm);

        // Disable editing
        txtCar.setEnabled(false);
        txtUser.setEnabled(false);
        txtSDate.setEnabled(false);
        txtEDate.setEnabled(false);
        txtPrice.setEnabled(false);

        queue = Volley.newRequestQueue(this);

        // Get request details from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            rentalID = extras.getInt("rentalID");
            txtCar.setText(String.valueOf(extras.getInt("carID")));
            txtUser.setText(String.valueOf(extras.getInt("idNumber")));
            txtSDate.setText(extras.getString("startDate"));
            txtEDate.setText(extras.getString("endDate"));
            txtPrice.setText(String.valueOf(extras.getDouble("totalPrice")));
        }

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmRequest();
            }
        });

        btVupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReqDetailsActivity.this, UpdateCarActivity.class);
                startActivity(intent);
            }
        });

        btVpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReqDetailsActivity.this, PayRequests.class);
                startActivity(intent);
            }
        });

        btReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReqDetailsActivity.this, ReturnCar.class);
                startActivity(intent);
            }
        });

        btVhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReqDetailsActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });

        btView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReqDetailsActivity.this, CarViewScreen.class);
                startActivity(intent);
            }
        });
        bt_Vrep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReqDetailsActivity.this, ReportMon.class);
                startActivity(intent);
            }
        });



    }


    private void confirmRequest() {
        String url = "http:/172.19.0.120/CarRental/update_request_status.php";

        JSONObject postData = new JSONObject();
        try {
            postData.put("rentalID", rentalID);
            postData.put("status", "Confirmed");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(ReqDetailsActivity.this, "Request confirmed", Toast.LENGTH_SHORT).show();
                                Intent resultIntent = new Intent(ReqDetailsActivity.this, HomeActivity.class);
                                resultIntent.putExtra("dataChanged", true);
                                startActivity(resultIntent);
                                finish();
                            } else {
                                String message = response.has("message") ? response.getString("message") : "Failed to confirm request";
                                Toast.makeText(ReqDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String responseData = new String(error.networkResponse.data);
                            Log.d("Error Response Data", responseData);
                        }
                        Toast.makeText(ReqDetailsActivity.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("Error_json", error.toString());
                    }
                });

        queue.add(request);
    }

}
