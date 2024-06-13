package com.example.rentcar;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

public class CustomerHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RentalAdap adapter;
    private List<Rental> rentalList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_history);

        recyclerView = findViewById(R.id.RentalRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rentalList = new ArrayList<>();
        adapter = new RentalAdap(rentalList, this);
        recyclerView.setAdapter(adapter);

        int customerID = getIntent().getIntExtra("customerID", -1); // Assuming customerID is passed in intent
        fetchRentalsForCustomer(customerID);
    }

    private void fetchRentalsForCustomer(int customerID) {
        String url = "http://172.19.0.120/CarRental/fetch_rentals.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Rental rental = new Rental(obj);
                                rentalList.add(rental);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("customerID", String.valueOf(customerID));
                return params;
            }
        };

        queue.add(request);
    }
}