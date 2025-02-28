package com.jamith.booksformeseller.activity.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseAuth;
import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.activity.MainActivity;
import com.jamith.booksformeseller.activity.signup.SignUpImageActivity;
import com.jamith.booksformeseller.dto.requestDTO.AddNewBookStockDTO;
import com.jamith.booksformeseller.dto.responseDTO.BookAddResponseDTO;
import com.jamith.booksformeseller.model.Book;
import com.jamith.booksformeseller.service.BookStockService;

public class AddBookStockActivity extends AppCompatActivity {

    private ImageView AddBookStockActivityBookCoverImageView;
    private TextView AddBookStockActivityBookTitleTextView, AddBookStockActivityBookAuthorTextView;
    private EditText AddBookStockActivityStockEditText, AddBookStockActivityPriceEditText;
    private Spinner AddBookStockActivityConditionSpinner;
    private Button AddBookStockActivitySubmitInventoryButton;
    private BookStockService bookStockService = new BookStockService();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ImageButton backButton;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_book_stock);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.activity_add_book_stock_progressBar);
        progressBar.setVisibility(View.GONE);
        Book book = (Book) getIntent().getSerializableExtra("book");
        AddBookStockActivityBookCoverImageView = findViewById(R.id.AddBookStockActivityBookCoverImageView);
        AddBookStockActivityBookTitleTextView = findViewById(R.id.AddBookStockActivityBookTitleTextView);
        AddBookStockActivityBookAuthorTextView = findViewById(R.id.AddBookStockActivityBookAuthorTextView);
        AddBookStockActivityStockEditText = findViewById(R.id.AddBookStockActivityStockEditText);
        AddBookStockActivityPriceEditText = findViewById(R.id.AddBookStockActivityPriceEditText);
        AddBookStockActivityConditionSpinner = findViewById(R.id.AddBookStockActivityConditionSpinner);
        AddBookStockActivitySubmitInventoryButton = findViewById(R.id.AddBookStockActivitySubmitInventoryButton);
        AddBookStockActivitySubmitInventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                addInventoryStock(book);
            }
        });
        backButton = findViewById(R.id.activity_add_book_stock_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.conditions_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AddBookStockActivityConditionSpinner.setAdapter(adapter);


        if (book != null) {
            // Display book details
            Glide.with(this)
                    .load(book.getCoverImage())
                    .placeholder(R.drawable.books_placeholder)
                    .error(R.drawable.books_placeholder)
                    .into(AddBookStockActivityBookCoverImageView);
            AddBookStockActivityBookTitleTextView.setText(book.getTitle());
            AddBookStockActivityBookAuthorTextView.setText(book.getAuthor());
        } else {
            Toast.makeText(this, "Failed to load book details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void addInventoryStock(Book book) {
        String stock = AddBookStockActivityStockEditText.getText().toString().trim();
        String price = AddBookStockActivityPriceEditText.getText().toString().trim();
        String condition = AddBookStockActivityConditionSpinner.getSelectedItem().toString();

        if (stock.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            AddNewBookStockDTO addNewBookStockDTO = new AddNewBookStockDTO();
            addNewBookStockDTO.setBookId(book.getBookId());
            addNewBookStockDTO.setSellerId(userId);
            addNewBookStockDTO.setStock(Integer.parseInt(stock));
            addNewBookStockDTO.setPrice(Double.parseDouble(price));
            addNewBookStockDTO.setCondition(condition);

            bookStockService.addNewBookStockData(addNewBookStockDTO, new BookStockService.BookStockServiceCallback() {
                @Override
                public void onSuccess(BookAddResponseDTO response) {
                    runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddBookStockActivity.this, "Book Stock Added successfully!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AddBookStockActivity.this, SearchBookActivity.class);
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddBookStockActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onFailure(String failureMessage) {
                    runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddBookStockActivity.this, "Failure: " + failureMessage, Toast.LENGTH_LONG).show();
                    });
                }
            });

        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}