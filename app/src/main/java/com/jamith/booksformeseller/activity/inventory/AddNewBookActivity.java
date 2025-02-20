package com.jamith.booksformeseller.activity.inventory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.database.DataRepository;
import com.jamith.booksformeseller.dto.requestDTO.AddNewBookDTO;
import com.jamith.booksformeseller.dto.responseDTO.BookAddResponseDTO;
import com.jamith.booksformeseller.model.Category;
import com.jamith.booksformeseller.model.Language;
import com.jamith.booksformeseller.service.BookService;
import com.jamith.booksformeseller.service.FirebaseStorageService;
import com.jamith.booksformeseller.util.StorageFolders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddNewBookActivity extends AppCompatActivity {
    private EditText titleEditText, authorEditText, isbnEditText, descriptionEditText, publisherEditText, tagsEditText;
    private Spinner categorySpinner, languageSpinner;
    private Button saveButton, imageButton;
    NumberPicker numberPicker;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<Category> bookCategories = new ArrayList<>();
    private List<Language> bookLanguages = new ArrayList<>();
    private DataRepository dataRepo;
    private String selectedCategoryId;
    private String selectedLanguageId;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    Uri bookCoverImage;

    String bookCoverImageDownloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        dataRepo = new DataRepository(this);

        numberPicker = findViewById(R.id.addNewBookPublicationYearPicker);
        numberPicker.setMaxValue(2100);
        numberPicker.setMinValue(1900);
        numberPicker.setValue(2025);

        titleEditText = findViewById(R.id.addNewBookTitleValueTextView);
        authorEditText = findViewById(R.id.addNewBookAuthorValueTextView);
        isbnEditText = findViewById(R.id.addNewBookIsbnValueTextView);
        descriptionEditText = findViewById(R.id.addNewBookDescriptionValueTextView);
        imageButton = findViewById(R.id.addNewBookImageUploadButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        publisherEditText = findViewById(R.id.addNewBookPublisherValueTextView);
        tagsEditText = findViewById(R.id.addNewBookTagsValueTextView);
        categorySpinner = findViewById(R.id.addNewBookCategorySpinner);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = (Category) parent.getItemAtPosition(position);
                selectedCategoryId = selectedCategory.getId(); // Get the ID
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        languageSpinner = findViewById(R.id.addNewBookLanguageSpinner);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Language selectedLanguage = (Language) parent.getItemAtPosition(position);
                selectedLanguageId = selectedLanguage.getId(); // Get the ID
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        saveButton = findViewById(R.id.addNewBookSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gatherImageDetails();
            }
        });

        fetchCategories();
        fetchLanguages();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            bookCoverImage = data.getData();
            Toast.makeText(this, "Document selected", Toast.LENGTH_SHORT).show();
            Log.d("Book Cover Image Path", bookCoverImage.getPath());
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void fetchLanguages() {
      bookLanguages = dataRepo.getLanguages();
      populateLanguageSpinner();
//        CollectionReference categoriesRef = db.collection("languages");
//        categoriesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        String id = document.getId();
//                        String language = document.getString("name");  // Assuming "name" is the field for category
//                        if (language != null) {
//                            bookLanguages.add(new Language(id, language));
//                        }
//                    }
//                    populateLanguageSpinner();
//                } else {
//                    Toast.makeText(AddNewBookActivity.this, "Failed to fetch categories", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void populateLanguageSpinner() {
        ArrayAdapter<Language> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bookLanguages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
    }

    private void fetchCategories() {
        bookCategories = dataRepo.getCategories();
        populateCategorySpinner();

        //        CollectionReference categoriesRef = db.collection("categories");
//        categoriesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        String id = document.getId();
//                        String category = document.getString("name");  // Assuming "name" is the field for category
//                        if (category != null) {
//                            bookCategories.add(new Category(id, category));
//                        }
//                    }
//                    populateCategorySpinner();
//                } else {
//                    Toast.makeText(AddNewBookActivity.this, "Failed to fetch categories", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void populateCategorySpinner() {
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bookCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void gatherImageDetails() {
        if (bookCoverImage == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        } else {
            new FirebaseStorageService().uploadFile(bookCoverImage, StorageFolders.IMAGES, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    bookCoverImageDownloadUrl = o.toString();
                    Log.d("image upload success", o.toString());
                    saveBook();
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddNewBookActivity.this, "Failed to upload file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void saveBook() {
        String userId = mAuth.getCurrentUser().getUid();
        String title = titleEditText.getText().toString();
        String author = authorEditText.getText().toString();
        String isbn = isbnEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String publisher = publisherEditText.getText().toString();
        int publicationYear = numberPicker.getValue();
        String tags = tagsEditText.getText().toString();




        AddNewBookDTO addNewBookDTO = new AddNewBookDTO();
        addNewBookDTO.setTitle(title);
        addNewBookDTO.setAuthor(author);
        addNewBookDTO.setIsbn(isbn);
        addNewBookDTO.setCategory(selectedCategoryId);
        addNewBookDTO.setCoverImage(bookCoverImageDownloadUrl);
        addNewBookDTO.setDescription(description);
        addNewBookDTO.setPublisher(publisher);
        addNewBookDTO.setPublicationYear(publicationYear);
        addNewBookDTO.setLanguage(selectedLanguageId);
        addNewBookDTO.setTags(Arrays.stream(tags.split(",")).toList());
        addNewBookDTO.setCreatedUser(userId);
        Log.d("Book Data  =====   ", addNewBookDTO.toString());

        BookService bookService = new BookService();
        bookService.addNewBookData(addNewBookDTO, new BookService.BookServiceCallback() {
            @Override
            public void onSuccess(BookAddResponseDTO response) {
                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddNewBookActivity.this, "Book Added successfully!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddNewBookActivity.this, SearchBookActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddNewBookActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onFailure(String failureMessage) {
                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddNewBookActivity.this, "Failure: " + failureMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}