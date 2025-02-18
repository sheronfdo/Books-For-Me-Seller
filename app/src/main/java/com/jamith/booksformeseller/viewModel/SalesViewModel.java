package com.jamith.booksformeseller.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.mikephil.charting.data.Entry;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jamith.booksformeseller.util.OrderStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SalesViewModel extends AndroidViewModel {
    private MutableLiveData<List<Entry>> salesData = new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String loggedInSellerId; // Set this value

    public SalesViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Entry>> getSalesData() {
        return salesData;
    }

    public void fetchSalesData(String sellerId) {
        db.collection("sellers")
                .document(sellerId)
                .collection("orderItems")
                .whereEqualTo("status", OrderStatus.ORDER_COMPLETED.name())
                .get()
                .addOnSuccessListener(orderItemsSnapshot -> {
                    // 1. Track pending async operations
                    final int totalItems = orderItemsSnapshot.size();
                    final AtomicInteger completedTasks = new AtomicInteger(0);
                    Map<String, Double> salesByDate = new ConcurrentHashMap<>(); // Thread-safe map

                    // 2. Check for empty result early
                    if (totalItems == 0) {
                        salesData.setValue(new ArrayList<>());
                        return;
                    }

                    // 3. Process each order item
                    for (QueryDocumentSnapshot itemDoc : orderItemsSnapshot) {
                        String orderId = itemDoc.getString("orderId");
                        double totalPrice = itemDoc.getDouble("price") * itemDoc.getLong("quantity");

                        // 4. Fetch order date
                        db.collection("orders").document(orderId)
                                .get()
                                .addOnCompleteListener(orderTask -> {
                                    if (orderTask.isSuccessful() && orderTask.getResult().exists()) {
                                        Date orderDate = orderTask.getResult().getDate("createdAt");
                                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                                .format(orderDate);

                                        // 5. Atomic update of salesByDate
                                        salesByDate.merge(date, totalPrice, Double::sum);
                                    }

                                    // 6. Check completion
                                    if (completedTasks.incrementAndGet() == totalItems) {
                                        // 7. Sort dates and create entries
                                        List<Entry> entries = new ArrayList<>();
                                        List<String> sortedDates = new ArrayList<>(salesByDate.keySet());
                                        Collections.sort(sortedDates);

                                        for (int i = 0; i < sortedDates.size(); i++) {
                                            entries.add(new Entry(i, salesByDate.get(sortedDates.get(i)).floatValue()));
                                        }

                                        salesData.postValue(entries);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching order items", e);
                });
    }
}