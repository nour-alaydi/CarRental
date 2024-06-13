package com.example.rentcar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReqDetails extends RecyclerView.Adapter<ReqDetails.ViewHolder> {
    private ArrayList<RentalRequest> items;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(RentalRequest request);
    }

    public ReqDetails(ArrayList<RentalRequest> items, OnItemClickListener listener) {
        this.items = items;
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RentalRequest request = items.get(position);
        holder.bind(request, itemClickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView requestIdTextView;
        private TextView carIdTextView;
        private TextView userIdTextView;
        private TextView startDateTextView;
        private TextView endDateTextView;
        private TextView totalPriceTextView;
        private TextView statusTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            requestIdTextView = itemView.findViewById(R.id.request_id_text_view);
            carIdTextView = itemView.findViewById(R.id.car_id_text_view);
            userIdTextView = itemView.findViewById(R.id.user_id_text_view);
            startDateTextView = itemView.findViewById(R.id.sdate_text_view);
            endDateTextView = itemView.findViewById(R.id.edate_text_view);
            totalPriceTextView = itemView.findViewById(R.id.total_text_view);
            statusTextView = itemView.findViewById(R.id.status_text_view);
        }

        public void bind(final RentalRequest request, final OnItemClickListener listener) {
            requestIdTextView.setText("Rental ID: " + request.getRentalID());
            carIdTextView.setText("Car ID: " + request.getCarID());
            userIdTextView.setText("User ID: " + request.getIdNumber());
            startDateTextView.setText("Start Date: " + request.getStartDate());
            endDateTextView.setText("End Date: " + request.getEndDate());
            totalPriceTextView.setText("Total Price: " + request.getTotalPrice());
            statusTextView.setText("Status: " + request.getStatus());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(request);
                }
            });
        }
    }
}