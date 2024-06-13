package com.example.rentcar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateCarActivity extends AppCompatActivity {

    private EditText txtBrand, txtPrice, txtColor ,txtModel ,txtstatus,txtid;
    private Button btnSearch, btnUpdate;
    private Spinner spinnerBrand;
    private SearchView searchViewModel;
    private RequestQueue queue;
    private ImageView car_im;

    private ImageButton btVhome, btView, bt_Vrep, btVupdate, btVpay, btReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_car);
        btVhome = findViewById(R.id.bt_Vhome);
        bt_Vrep = findViewById(R.id.bt_Vrep);
        btVupdate = findViewById(R.id.bt_Vupdate);
        btVpay = findViewById(R.id.bt_Vpay);
        btView=findViewById(R.id.bt_view);

        txtBrand = findViewById(R.id.txt_brand);
        searchViewModel = findViewById(R.id.search_model);
        txtPrice = findViewById(R.id.txt_price);
        txtColor = findViewById(R.id.txt_color);
        txtBrand = findViewById(R.id.txt_brand);
        txtModel = findViewById(R.id.txt_model);
        car_im = findViewById(R.id.car_im);
        txtstatus = findViewById(R.id.txt_status);
        txtid = findViewById(R.id.txt_car);
        btnSearch = findViewById(R.id.bt_search);
        btReturn = findViewById(R.id.bt_return);


        btnUpdate = findViewById(R.id.bt_update);
        spinnerBrand = findViewById(R.id.sp_brand);
        queue = Volley.newRequestQueue(this);

        // Fetch brands from the database and populate the spinner
        fetchBrands();


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String brandd = spinnerBrand.getSelectedItem().toString();

                System.out.println(brandd+" hhhhhhhhh");
                String model = searchViewModel.getQuery().toString().trim();
                searchCar(brandd, model);
            }
        });
        btVhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateCarActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String brand = txtBrand.getText().toString().trim();
                String model = searchViewModel.getQuery().toString().trim();
                String price = txtPrice.getText().toString().trim();
                String color = txtColor.getText().toString().trim();
                updateCar(brand, model, price, color);
            }
        });

        btVupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateCarActivity.this, UpdateCarActivity.class);
                startActivity(intent);
            }
        });

        btVpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateCarActivity.this, PayRequests.class);
                startActivity(intent);
            }
        });

        btReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateCarActivity.this, ReturnCar.class);
                startActivity(intent);
            }
        });

        btView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateCarActivity.this, CarViewScreen.class);
                startActivity(intent);
            }
        });
        bt_Vrep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateCarActivity.this, ReportMon.class);
                startActivity(intent);
            }
        });
    }

    private void fetchBrands() {
        String url = "http://172.19.0.120/CarRental/get_brands.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<String> brandList = new ArrayList<>();
                        System.out.println(response.length()+"    .......lengthhhhh");
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                String brandName = response.getString(i);
                                System.out.println(brandName+"    .......thisssssssss");
                                brandList.add(brandName);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                                UpdateCarActivity.this,
                                android.R.layout.simple_spinner_item,
                                brandList
                        );
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerBrand.setAdapter(spinnerAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdateCarActivity.this, "Error fetching brands: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }


    private void searchCar(final String brand, final String model) {
        String url = "http://172.19.0.120/CarRental/search_car.php?carBrand=" + brand + "&carModel=" + model;
        System.out.println(url +" uuuuuuuuuuuuuuuuuuu");
//        http://192.168.1.119/CarRental/search_car.php?carBrand=Audi&carModel=A4
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println(response.getBoolean("success")+" yyyyyyyyyyyy ");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            if (response.getBoolean("success")) {
                                JSONObject carData = response.getJSONObject("car");
                                String id = carData.getString("carID");
                                String brand = carData.getString("carBrand");
                                String model = carData.getString("carModel");
                                String color = carData.getString("color");
                                String price = carData.getString("price");
                                String status = carData.getString("status");
                                String imageUrl = carData.getString("image");
                                System.out.println(imageUrl+" %%%%%%%%%%%%%%%hhhhhhhhhhhhhhh");

                                Glide.with(UpdateCarActivity.this)
                                        .load("http://192.168.1.119/CarRental/images/"+imageUrl)
                                        .into(car_im);

                                // Fill text fields with retrieved car details
                                txtPrice.setText(price);
                                txtColor.setText(color);
                                txtBrand.setText(brand);
                                txtModel.setText(model);
                                txtstatus.setText(status);
                                txtid.setText(id);
                            } else {
                                Toast.makeText(UpdateCarActivity.this, "Car not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdateCarActivity.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }

    private void updateCar(final String brand, final String model, final String price, final String color) {
        String url = "http://172.19.0.120/CarRental/update_car.php";

        Log.d("UpdateCar", "Brand: " + brand + ", Model: " + model + ", Price: " + price + ", Color: " + color);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("UpdateCar", "Response: " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("success")) {
                                Toast.makeText(UpdateCarActivity.this, "Car updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                String error = jsonResponse.optString("error", "Failed to update car");
                                Toast.makeText(UpdateCarActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("UpdateCar", "JSON error: " + e.toString());
                            Toast.makeText(UpdateCarActivity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("UpdateCar", "Volley error: " + error.toString());
                        Toast.makeText(UpdateCarActivity.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("carBrand", brand);
                params.put("carModel", model);
                params.put("price", price);
                params.put("color", color);
                Log.d("UpdateCar", "Params: " + params.toString());
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("brand", txtBrand.getText().toString());
        outState.putString("model", txtModel.getText().toString());
        outState.putString("price", txtPrice.getText().toString());
        outState.putString("color", txtColor.getText().toString());
        outState.putString("status", txtstatus.getText().toString());
        outState.putString("id", txtid.getText().toString());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        txtBrand.setText(savedInstanceState.getString("brand"));
        txtModel.setText(savedInstanceState.getString("model"));
        txtPrice.setText(savedInstanceState.getString("price"));
        txtColor.setText(savedInstanceState.getString("color"));
        txtstatus.setText(savedInstanceState.getString("status"));
        txtid.setText(savedInstanceState.getString("id"));
    }




}
