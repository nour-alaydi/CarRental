package com.example.rentcar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReportMon extends AppCompatActivity {
    private Spinner spMon, spYear;
    private Button btReport;
    private RecyclerView recyRep;
    private RepAdapter adapter;
    private ArrayList<RentalRequest> requestList;
    private RequestQueue requestQueue;
    private ImageButton btVhome, btView, bt_Vrep, btVupdate, btVpay, btReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reports);

        spMon = findViewById(R.id.sp_mon);
        spYear = findViewById(R.id.sp_year);
        btReport = findViewById(R.id.bt_searpay);
        recyRep = findViewById(R.id.recy_pay);
        btVhome = findViewById(R.id.bt_Vhome);
        btView = findViewById(R.id.bt_view);
        bt_Vrep = findViewById(R.id.bt_Vrep);
        btVupdate = findViewById(R.id.bt_Vupdate);
        btVpay = findViewById(R.id.bt_Vpay);
        btReturn = findViewById(R.id.bt_return);

        requestList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        recyRep.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RepAdapter(requestList);
        recyRep.setAdapter(adapter);

        populateSpinners();

        btReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String month = spMon.getSelectedItem().toString();
                String year = spYear.getSelectedItem().toString();
                fetchReports(month, year);
            }
        });

        btView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportMon.this, CarViewScreen.class);
                startActivity(intent);
            }
        });

        bt_Vrep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportMon.this, ReportMon.class);
                startActivity(intent);
            }
        });
        btVhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportMon.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        btVupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportMon.this, UpdateCarActivity.class);
                startActivity(intent);
            }
        });

        btVpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportMon.this, PayRequests.class);
                startActivity(intent);
            }
        });

        btReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportMon.this, ReturnCar.class);
                startActivity(intent);
            }
        });
    }


    private void populateSpinners() {
        // Populate month spinner
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMon.setAdapter(monthAdapter);

        // Populate year spinner with years from the database start year to current year
        fetchYears();
    }

    private void fetchYears() {
        String url = "http://172.19.0.120/CarRental/get_years.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<String> years = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                years.add(response.getString(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(ReportMon.this,
                                android.R.layout.simple_spinner_item, years);
                        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spYear.setAdapter(yearAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private void fetchReports(String month, String year) {
        String url = "http://172.19.0.120/CarRental/get_paid_rentals.php?month=" + month + "&year=" + year;

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
                Toast.makeText(ReportMon.this, "Error fetching reports", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("selectedMonth", spMon.getSelectedItemPosition());
        outState.putInt("selectedYear", spYear.getSelectedItemPosition());

        ArrayList<Bundle> requestBundles = new ArrayList<>();
        for (RentalRequest request : requestList) {
            Bundle bundle = new Bundle();
            bundle.putInt("rentalID", request.getRentalID());
            bundle.putInt("carID", request.getCarID());
            bundle.putInt("idNumber", request.getIdNumber());
            bundle.putString("startDate", request.getStartDate());
            bundle.putString("endDate", request.getEndDate());
            bundle.putDouble("totalPrice", request.getTotalPrice());
            bundle.putString("status", request.getStatus());
            requestBundles.add(bundle);
        }
        outState.putParcelableArrayList("requestList", requestBundles);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            spMon.setSelection(savedInstanceState.getInt("selectedMonth"));
            spYear.setSelection(savedInstanceState.getInt("selectedYear"));

            ArrayList<Bundle> requestBundles = savedInstanceState.getParcelableArrayList("requestList");
            if (requestBundles != null) {
                for (Bundle bundle : requestBundles) {
                    RentalRequest request = new RentalRequest(bundle.getInt("rentalID"), bundle.getInt("carID"),
                            bundle.getInt("idNumber"), bundle.getString("startDate"), bundle.getString("endDate"),
                            bundle.getDouble("totalPrice"), bundle.getString("status"));
                    requestList.add(request);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}
