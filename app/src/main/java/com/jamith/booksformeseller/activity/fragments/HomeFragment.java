package com.jamith.booksformeseller.activity.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.adapters.RecentOrderAdapter;
import com.jamith.booksformeseller.model.OrderItem;
import com.jamith.booksformeseller.util.OrderStatus;
import com.jamith.booksformeseller.viewModel.SalesViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView ordersRecyclerView;
    private List<OrderItem> orders;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    TextView tv_book_stock, tv_orders_summary, tv_earnings, tv_low_stock;
    int bookStocks, lowStockCount, totalOrders = 0;
    int lowStockThreshold = 10;
    double totalEarnings = 0.0;
    private LineChart lineChart;
    private SalesViewModel salesViewModel;
    private RecyclerView recentOrdersRecycler;
    private RecentOrderAdapter orderAdapter;
    private List<OrderItem> ongoingOrderItems = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tv_book_stock = view.findViewById(R.id.fh_tv_book_stock);
        tv_orders_summary = view.findViewById(R.id.fh_tv_orders_summary);
        tv_earnings = view.findViewById(R.id.fh_tv_earnings);
        tv_low_stock = view.findViewById(R.id.fh_tv_low_stock);
        calculateSellerStats(auth.getUid());


        lineChart = view.findViewById(R.id.fh_line_chart);
        salesViewModel = new ViewModelProvider(this).get(SalesViewModel.class);
        salesViewModel.fetchSalesData(auth.getUid());

        salesViewModel.getSalesData().observe(getViewLifecycleOwner(), entries -> {
            if (entries != null && !entries.isEmpty()) {
                setupLineChart(entries);
            }
        });

        recentOrdersRecycler = view.findViewById(R.id.recent_orders_recycler);
        recentOrdersRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        orderAdapter = new RecentOrderAdapter(ongoingOrderItems);
        recentOrdersRecycler.setAdapter(orderAdapter);
        loadOngoingOrders(auth.getUid());
        return view;
    }

    private void loadOngoingOrders(String sellerId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("sellers")
                .document(sellerId)
                .collection("orderItems")
                .whereIn("status", Arrays.asList(
                        OrderStatus.ORDER_CONFIRMED.name(),
                        OrderStatus.ORDER_APPROVED.name(),
                        OrderStatus.PROCESSING.name()
                ))
                .orderBy("orderItemId", Query.Direction.DESCENDING) // Use orderItemId as timestamp proxy
                .limit(5)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("OngoingOrders", "Listen failed", error);
                        return;
                    }

                    List<OrderItem> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        OrderItem item = doc.toObject(OrderItem.class);
                        items.add(item);
                    }

                    orderAdapter.updateOrders(items);
                });
    }


    private void setupLineChart(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Total Sales");
        dataSet.setColor(getResources().getColor(android.R.color.holo_blue_bright));
        dataSet.setCircleColor(getResources().getColor(android.R.color.holo_blue_bright));
        dataSet.setValueTextColor(getResources().getColor(android.R.color.black));

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateX(2000);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // One step per day
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getDates(entries)));

        lineChart.invalidate();
    }

    private List<String> getDates(List<Entry> entries) {
        List<String> dates = new ArrayList<>();
        for (Entry entry : entries) {
            dates.add("Day " + ((int) entry.getX() + 1)); // Simplified date format
        }
        return dates;
    }


    private void calculateSellerStats(String sellerId) {
        db.collection("sellers")
                .document(sellerId)
                .collection("orderItems")
                .whereEqualTo("status", OrderStatus.ORDER_COMPLETED.name())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {


                            for (DocumentSnapshot document : task.getResult()) {
                                Long quantity = document.getLong("quantity");
                                Long price = document.getLong("price");
                                if (quantity != null && price != null) {
                                    totalEarnings += quantity * price;
                                }
                                totalOrders++;
                            }

                            tv_orders_summary.setText(String.valueOf(totalOrders));
                            tv_earnings.setText("$ " + String.valueOf(totalEarnings));
                        } else {
                            Log.d("SellerDashboard", "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection("bookStocks")
                .whereEqualTo("sellerId", sellerId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            bookStocks = task.getResult().size();
                            tv_book_stock.setText(String.valueOf(bookStocks) + " Stocks");
                            lowStockCount = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Long stock = document.getLong("stock");
                                if (stock != null && stock < lowStockThreshold) {
                                    lowStockCount++;
                                }
                            }
                            tv_low_stock.setText(String.valueOf(lowStockCount) + " Items");
                            Log.d("BookCount", "Total books for seller: " + bookStocks);
                            Log.d("LowStockCount", "Total low stock books for seller: " + lowStockCount);
                        } else {
                            Log.d("BookCount", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}