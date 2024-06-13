package com.example.rentcar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PayRequests extends AppCompatActivity {
    private SearchView searchView;
    private Button searchButton;
    private RecyclerView recyclerView;
    private ReqDetails adapter;
    private ArrayList<RentalRequest> requestList;
    private RequestQueue requestQueue;
    private ImageButton btVhome, btView, bt_Vrep, btVupdate, btVpay, btReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_req);

        btVhome = findViewById(R.id.bt_Vhome);
        btView = findViewById(R.id.bt_view);
        bt_Vrep = findViewById(R.id.bt_Vrep);
        btVupdate = findViewById(R.id.bt_Vupdate);
        btVpay = findViewById(R.id.bt_Vpay);
        btReturn = findViewById(R.id.bt_return);

        searchView = findViewById(R.id.searchView);
        searchButton = findViewById(R.id.bt_searpay);
        recyclerView = findViewById(R.id.recy_pay);
        requestList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReqDetails(requestList, new ReqDetails.OnItemClickListener() {
            @Override
            public void onItemClick(RentalRequest request) {
                changeRequestStatus(request.getRentalID());
            }
        });
        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        } else {
            loadRequests(null);
        }

//

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = searchView.getQuery().toString().trim();
                if (TextUtils.isEmpty(userID)) {
                    loadRequests(null);
                } else {
                    loadRequests(userID);
                }
            }
        });

        setupNavigationButtons();

    }
    private void setupNavigationButtons() {
        btVhome.setOnClickListener(v -> startActivity(new Intent(PayRequests.this, HomeActivity.class)));
        btVupdate.setOnClickListener(v -> startActivity(new Intent(PayRequests.this, UpdateCarActivity.class)));
        btVpay.setOnClickListener(v -> startActivity(new Intent(PayRequests.this, PayRequests.class)));
        btReturn.setOnClickListener(v -> startActivity(new Intent(PayRequests.this, ReturnCar.class)));
        btView.setOnClickListener(v -> startActivity(new Intent(PayRequests.this, CarViewScreen.class)));
        bt_Vrep.setOnClickListener(v -> startActivity(new Intent(PayRequests.this, ReportMon.class)));
    }

    private void loadRequests(String userID) {
        String url = "http://172.19.0.120/CarRental/get_confirmed_req.php";
        if (userID != null) {
            url += "?userID=" + userID;
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        requestList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                RentalRequest request = new RentalRequest(obj);
                                requestList.add(request);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    public void changeRequestStatus(int rentalID) {
        String url = "http://172.19.0.120/CarRental/update_request_status.php";
        JSONObject postData = new JSONObject();
        try {
            postData.put("rentalID", rentalID);
            postData.put("status", "Paid");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(PayRequests.this, "Status updated to Paid", Toast.LENGTH_SHORT).show();
                                loadRequests(null); // Reload all requests after status update
                            } else {
                                Toast.makeText(PayRequests.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("searchQuery", searchView.getQuery().toString());
        outState.putString("requestList", convertRequestListToJson().toString());
    }


    private void restoreInstanceState(Bundle savedInstanceState) {
        searchView.setQuery(savedInstanceState.getString("searchQuery", ""), false);
        try {
            JSONArray jsonArray = new JSONArray(savedInstanceState.getString("requestList", "[]"));
            requestList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                requestList.add(new RentalRequest(jsonArray.getJSONObject(i)));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONArray convertRequestListToJson() {
        JSONArray jsonArray = new JSONArray();
        for (RentalRequest request : requestList) {
            try {
                jsonArray.put(request.toJsonObject());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }
}