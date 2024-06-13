package com.example.rentcar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class RepAdapter extends RecyclerView.Adapter<RepAdapter.ViewHolder> {
    private ArrayList<RentalRequest> requestList;

    public RepAdapter(ArrayList<RentalRequest> requestList) {
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rep_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RentalRequest request = requestList.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtRentalID, txtCarID, txtUserID, txtStartDate, txtEndDate, totalPriceTextView, txtStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRentalID = itemView.findViewById(R.id.txt_rental_id);
            txtCarID = itemView.findViewById(R.id.txt_car_id);
            txtUserID = itemView.findViewById(R.id.txt_user_id);
            txtStartDate = itemView.findViewById(R.id.txt_start_date);
            txtEndDate = itemView.findViewById(R.id.txt_end_date);
            totalPriceTextView = itemView.findViewById(R.id.total_text_view);
            txtStatus = itemView.findViewById(R.id.txt_status);
        }

        public void bind(RentalRequest request) {
            txtRentalID.setText("Rental ID: " + request.getRentalID());
            txtCarID.setText("Car ID: " + request.getCarID());
            txtUserID.setText("User ID: " + request.getIdNumber());
            txtStartDate.setText("Start Date: " + request.getStartDate());
            txtEndDate.setText("End Date: " + request.getEndDate());
            totalPriceTextView.setText("Total Price: " + request.getTotalPrice());
            txtStatus.setText("Status: " + request.getStatus());
        }
    }

}


