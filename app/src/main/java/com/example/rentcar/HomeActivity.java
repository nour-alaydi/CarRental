package com.example.rentcar;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class HomeActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<RentalRequest> rentalList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> rentalDisplayList;
    private RequestQueue queue;
    private TextView txtPending, txtConfirmed, txtPaid;

    private ImageButton btVhome, btView, bt_Vrep, btVupdate, btVpay, btReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtPending = findViewById(R.id.txt_pending);
        txtConfirmed = findViewById(R.id.txt_confirmed);
        txtPaid = findViewById(R.id.txt_paid);

        btVhome = findViewById(R.id.bt_Vhome);
        btView = findViewById(R.id.bt_view);
        bt_Vrep = findViewById(R.id.bt_Vrep);
        btVupdate = findViewById(R.id.bt_Vupdate);
        btVpay = findViewById(R.id.bt_Vpay);

        btReturn = findViewById(R.id.bt_return);
        listView = findViewById(R.id.lst_Request); // Updated ListView ID
        rentalList = new ArrayList<>();
        rentalDisplayList = new ArrayList<>();
        queue = Volley.newRequestQueue(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rentalDisplayList);
        listView.setAdapter(adapter);

        getStatusCounts();
        fetchRentalRequests();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RentalRequest selectedRental = rentalList.get(position);

                Intent intent = new Intent(HomeActivity.this, ReqDetailsActivity.class);
                intent.putExtra("rentalID", selectedRental.getRentalID());
                intent.putExtra("carID", selectedRental.getCarID());
                intent.putExtra("idNumber", selectedRental.getIdNumber());
                intent.putExtra("startDate", selectedRental.getStartDate());
                intent.putExtra("endDate", selectedRental.getEndDate());
                intent.putExtra("totalPrice", selectedRental.getTotalPrice());
                startActivity(intent);
            }
        });


        btView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CarViewScreen.class);
                startActivity(intent);
            }
        });
//
        bt_Vrep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ReportMon.class);
                startActivity(intent);
            }
        });
        btVhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });


        btVupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, UpdateCarActivity.class);
                startActivity(intent);
            }
        });

        btVpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PayRequests.class);
                startActivity(intent);
            }
        });

        btReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ReturnCar.class);
                startActivity(intent);
            }
        });
    }

    private void getStatusCounts() {
        String url = "http://172.19.0.120/CarRental/get_status_counts.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            txtPending.setText("Pending: " + response.getString("Pending"));
                            txtConfirmed.setText("Confirmed: " + response.getString("Confirmed"));
                            txtPaid.setText("Paid: " + response.getString("Paid"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("Error_json", error.toString());
                    }
                });

        queue.add(request);
    }

    private void fetchRentalRequests() {
        String url = "http://172.19.0.120/CarRental/get_requests.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            rentalList.clear();
                            rentalDisplayList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject rental = response.getJSONObject(i);
                                int rentalID = rental.getInt("rentalID");
                                int carID = rental.getInt("carID");
                                int idNumber = rental.getInt("idNumber");
                                String startDate = rental.getString("startDate");
                                String endDate = rental.getString("endDate");
                                double totalPrice = rental.getDouble("totalPrice");
                                String status = rental.getString("status");

                                RentalRequest newRental = new RentalRequest(rentalID, carID, idNumber, startDate, endDate, totalPrice, status);
                                rentalList.add(newRental);
                                rentalDisplayList.add("Rental ID: " + rentalID + "\nCar ID: " + carID + "\nUser ID: " + idNumber);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("Error_json", error.toString());
                    }
                });

        queue.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null && data.getBooleanExtra("dataChanged", false)) {
                getStatusCounts();
                fetchRentalRequests();
            }
        }
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("txtPending", txtPending.getText().toString());
//        outState.putString("txtConfirmed", txtConfirmed.getText().toString());
//        outState.putString("txtPaid", txtPaid.getText().toString());
////        outState.putParcelableArrayList("rentalList", rentalList);
//        outState.putStringArrayList("rentalDisplayList", rentalDisplayList);
//    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        if (savedInstanceState != null) {
//            txtPending.setText(savedInstanceState.getString("txtPending"));
//            txtConfirmed.setText(savedInstanceState.getString("txtConfirmed"));
//            txtPaid.setText(savedInstanceState.getString("txtPaid"));
////            rentalList = savedInstanceState.getParcelableArrayList("rentalList");
//            rentalDisplayList = savedInstanceState.getStringArrayList("rentalDisplayList");
//            adapter.notifyDataSetChanged();
//        }
//    }


}