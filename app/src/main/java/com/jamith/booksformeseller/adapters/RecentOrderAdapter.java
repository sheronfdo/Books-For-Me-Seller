package com.jamith.booksformeseller.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.model.OrderItem;

import java.util.List;
import java.util.Locale;

public class RecentOrderAdapter extends RecyclerView.Adapter<RecentOrderAdapter.RecentOrderViewHolder> {
    private List<OrderItem> orderItems;
//    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());

    public RecentOrderAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public RecentOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_order_item, parent, false);
        return new RecentOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentOrderViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);

        holder.orderId.setText("#" + item.getOrderId().substring(0, 6));
        holder.status.setText(item.getStatus().toUpperCase());
        holder.productName.setText(item.getTitle());
        holder.amount.setText(String.format(Locale.getDefault(), "$%.2f", item.getPrice() * item.getQuantity()));

        // Set status color
        int color = getStatusColor(item.getStatus());
        holder.status.setTextColor(color);
        holder.statusIndicator.setBackgroundColor(color);
    }

    private int getStatusColor(String status) {
        switch (status.toUpperCase()) {
            case "PENDING":
                return Color.parseColor("#FFA000");
            case "PROCESSING":
                return Color.parseColor("#2196F3");
            case "SHIPPED":
                return Color.parseColor("#9C27B0");
            default:
                return Color.parseColor("#9E9E9E");
        }
    }

    public void updateOrders(List<OrderItem> newItems) {
        orderItems = newItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class RecentOrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, status, productName, amount;
        View statusIndicator;

        public RecentOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            status = itemView.findViewById(R.id.order_status);
            productName = itemView.findViewById(R.id.product_name);
            amount = itemView.findViewById(R.id.order_amount);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
        }
    }
}