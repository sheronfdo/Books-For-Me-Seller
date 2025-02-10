package com.jamith.booksformeseller.activity.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.activity.inventory.SearchBookActivity;
import com.jamith.booksformeseller.adapters.SellerInventoryAdapter;
import com.jamith.booksformeseller.model.BookStockData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class InventoryFragment extends Fragment {
    Button addNewBookButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    List<BookStockData> bookStockDataList = new ArrayList<>();
    SellerInventoryAdapter sellerInventoryAdapter;
    RecyclerView bookListRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        addNewBookButton = view.findViewById(R.id.addNewBookButton);
        addNewBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchBookActivity.class);
                startActivity(intent);
            }
        });
        sellerInventoryAdapter = new SellerInventoryAdapter(view.getContext(), bookStockDataList);
        bookListRecyclerView = view.findViewById(R.id.bookListRecyclerView);
        bookListRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        bookListRecyclerView.setAdapter(sellerInventoryAdapter);
        fetchBookStockData();
        return view;
    }

    private void fetchBookStockData() {
        // Fetch book stocks only for the current seller
        db.collection("bookStocks")
                .whereEqualTo("sellerId", mAuth.getCurrentUser().getUid().toString())
                .get()
                .addOnSuccessListener(bookStockSnapshots -> {
                    if (bookStockSnapshots.isEmpty()) {
                        Log.d("bookStocksData List", "No book stocks found for this seller.");
                    }
                    for (QueryDocumentSnapshot stockDoc : bookStockSnapshots) {
                        String bookId = stockDoc.getString("bookId");
                        Map<String, Object> bookStockDetails = stockDoc.getData();
                        db.collection("books").document(bookId).get()
                                .addOnSuccessListener(bookDoc -> {
                                    if (bookDoc.exists()) {
                                        Map<String, Object> bookData = bookDoc.getData();
                                        BookStockData bookStockData = new BookStockData();
                                        bookStockData.setBookId(bookId);
                                        bookStockData.setTitle(bookData.get("title").toString());
                                        bookStockData.setAuthor(bookData.get("author").toString());
                                        bookStockData.setPrice(Double.parseDouble(bookStockDetails.get("price").toString()));
                                        bookStockData.setStock(Integer.parseInt(bookStockDetails.get("stock").toString()));
                                        bookStockData.setCondition(bookStockDetails.get("condition").toString());
                                        bookStockData.setCoverImage(bookData.get("coverImage").toString());
                                        bookStockDataList.add(bookStockData);
                                        sellerInventoryAdapter.notifyDataSetChanged();
                                    }
                                    Log.d("bookStocksData List", bookStockDataList.toString());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching data", e);
                });

    }
}