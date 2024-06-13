package com.example.rentcar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class InsertIDDelete extends AppCompatActivity {

    private EditText editTextCarId;
    private Button buttonDeleteCar;
    private static final String DELETE_URL = "http://172.19.0.120/CarRental/DeleteCar.php";
    private static final String TAG = "InsertIDDelete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_iddelete);

        editTextCarId = findViewById(R.id.editTextCarId);
        buttonDeleteCar = findViewById(R.id.buttonDeleteCar);

        buttonDeleteCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCar();
            }
        });
    }

    private void deleteCar() {
        final String carId = editTextCarId.getText().toString().trim();
        if (carId.isEmpty()) {
            Toast.makeText(this, "Please enter Car ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = DELETE_URL + "?id=" + carId;
        Log.d(TAG, "Request URL: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response: " + response);
                        if (response.equals("success")) {
                            Toast.makeText(InsertIDDelete.this, "Car deleted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(InsertIDDelete.this, "Failed to delete car: " + response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(InsertIDDelete.this, "Error deleting car", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
