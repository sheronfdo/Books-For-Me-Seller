package com.jamith.booksformeseller.activity.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.adapters.BookSearchAdapter;
import com.jamith.booksformeseller.model.Book;

import java.util.ArrayList;
import java.util.List;

public class SearchBookActivity extends AppCompatActivity {
    Button addNewBookNavigateButton, searchBookbutton;
    BookSearchAdapter bookSearchAdapter;
    private List<Book> bookList = new ArrayList<>();
    private EditText searchInput;
    private RecyclerView searchResultsRecyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        searchInput = findViewById(R.id.addNewBookStockTitleEditText);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);
        bookSearchAdapter = new BookSearchAdapter(this, bookList, new BookSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                Log.d("Selected Book Is == ", book.toString());
            }
        });
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecyclerView.setAdapter(bookSearchAdapter);
        addNewBookNavigateButton = findViewById(R.id.addNewBookNavigateButton);
        addNewBookNavigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchBookActivity.this, AddNewBookActivity.class);
                startActivity(intent);
            }
        });
        searchBookbutton = findViewById(R.id.searchBookButton);
        searchBookbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBooks();
            }
        });
    }

    private void searchBooks() {
        String query = searchInput.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a title or ISBN", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("Searching", "Searching");
        // Query centralizedBooks collection by title or ISBN
        db.collection("books").whereEqualTo("title", query) // Search by title
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookList.clear();
                        for (DocumentSnapshot document : task.getResult()) {

                            Log.d("iten", document.getId());
                            Book book = document.toObject(Book.class);
                            book.setBookId(document.getId()); // Set Firestore document ID
                            bookList.add(book);
                        }
                        bookSearchAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to fetch books", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}