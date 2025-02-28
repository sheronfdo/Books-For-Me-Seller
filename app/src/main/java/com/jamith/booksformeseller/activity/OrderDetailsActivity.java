package com.jamith.booksformeseller.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.dto.requestDTO.OrderStatusDTO;
import com.jamith.booksformeseller.dto.responseDTO.OrderResponseDTO;
import com.jamith.booksformeseller.model.OrderItem;
import com.jamith.booksformeseller.service.OrderService;
import com.jamith.booksformeseller.util.OrderStatus;

public class OrderDetailsActivity extends AppCompatActivity {
    private TextView tvOrderItemName, tvOrderItemQuantity, tvOrderItemPrice, tvOrderItemTotalPrice, tvOrderStatus;
    private TextView tvreceiver_name, tvreceiver_phone, tvreceiver_address, tvreceiver_email;
    private Spinner spinnerOrderStatus;
    private Button btnUpdateStatus, btnContactReceiver;
    private ImageView ivOrderItemImage;
    private OrderItem orderItem;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String recieverName;
    private String recieverPhoneNumber;
    private String recieverEmail;
    private String recieverAddress;
    private OrderService orderService = new OrderService();
    private ImageButton backButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        backButton = findViewById(R.id.activity_order_details_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progressBar  = findViewById(R.id.activityOrderDetailsprogressBar);



        tvOrderItemName = findViewById(R.id.tv_order_item_name);
        tvOrderItemQuantity = findViewById(R.id.tv_order_item_quantity);
        tvOrderItemPrice = findViewById(R.id.tv_order_item_price);
        tvOrderItemTotalPrice = findViewById(R.id.tv_order_item_total_price);
        tvOrderStatus = findViewById(R.id.tv_order_status);
        spinnerOrderStatus = findViewById(R.id.spinner_order_status);
        btnUpdateStatus = findViewById(R.id.btn_update_status);
        btnContactReceiver = findViewById(R.id.btn_contact_receiver);
        ivOrderItemImage = findViewById(R.id.iv_order_item_image);

        tvreceiver_name = findViewById(R.id.tv_receiver_name);
        tvreceiver_address = findViewById(R.id.tv_receiver_address);
        tvreceiver_email = findViewById(R.id.tv_receiver_email);
        tvreceiver_phone = findViewById(R.id.tv_receiver_phone);

        orderItem = (OrderItem) getIntent().getSerializableExtra("orderItem");
        loadOrderDetails();
        btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                updateOrderStatus();
            }
        });

        btnContactReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactReceiver();
            }
        });
    }

    private void loadOrderDetails() {
        if (orderItem != null) {
            DocumentReference orderRef = db.collection("orders").document(orderItem.getOrderId());
            orderRef.get().addOnSuccessListener(documentSnapshot -> {
                Log.d("doc snap", documentSnapshot.toString());
                if (documentSnapshot.exists()) {
                    recieverName = documentSnapshot.getString("recieverName");
                    recieverAddress = documentSnapshot.getString("recieverAddress");
                    recieverEmail = documentSnapshot.getString("recieverEmail");
                    recieverPhoneNumber = documentSnapshot.getString("recieverPhoneNumber");

                    tvreceiver_name.setText(recieverName);
                    tvreceiver_address.setText(recieverAddress);
                    tvreceiver_email.setText(recieverEmail);
                    tvreceiver_phone.setText(recieverPhoneNumber);
                } else {
                    Log.d("Data doesnt found", null);
                }
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Failed to load order details.", Toast.LENGTH_SHORT).show());


            tvOrderItemName.setText(orderItem.getTitle());
            tvOrderItemQuantity.setText(Integer.toString(orderItem.getQuantity()));
            tvOrderItemPrice.setText(Double.toString(orderItem.getPrice()));
            tvOrderItemTotalPrice.setText(Double.toString(orderItem.getPrice() * orderItem.getQuantity()));
            tvOrderStatus.setText(orderItem.getStatus());
            Glide.with(this).load(orderItem.getImageUrl()).into(ivOrderItemImage);
            for (int i = 0; i < spinnerOrderStatus.getCount(); i++) {
                if (spinnerOrderStatus.getItemAtPosition(i).toString().equals(orderItem.getStatus())) {
                    spinnerOrderStatus.setSelection(i);
                    break;
                }
            }

        }
    }

    private void updateOrderStatus() {
        String newStatus = spinnerOrderStatus.getSelectedItem().toString();

        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderStatus(OrderStatus.valueOf(newStatus));
        orderStatusDTO.setOrderItemId(orderItem.getOrderItemId());
        orderStatusDTO.setOrderId(orderItem.getOrderId());
        orderStatusDTO.setSellerId(orderItem.getSellerId());

        orderService.statusUpdate(orderStatusDTO, new OrderService.OrderServiceCallback() {
            @Override
            public void onSuccess(OrderResponseDTO response) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(OrderDetailsActivity.this, "Status Updated Successful.", Toast.LENGTH_LONG).show();
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(OrderDetailsActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onFailure(String failureMessage) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(OrderDetailsActivity.this, "Error: " + failureMessage, Toast.LENGTH_LONG).show();
                });
            }
        });

    }

    private void contactReceiver() {
        if (recieverPhoneNumber != null) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + recieverPhoneNumber));
            startActivity(intent);
        } else {
            Toast.makeText(this, "Phone number not available.", Toast.LENGTH_SHORT).show();
        }
    }
}