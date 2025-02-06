package com.jamith.booksformeseller.activity.signup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.activity.MainActivity;
import com.jamith.booksformeseller.dto.requestDTO.SellerSignUpBrRequest;
import com.jamith.booksformeseller.dto.responseDTO.SellerSignUpResponseDTO;
import com.jamith.booksformeseller.service.FirebaseStorageService;
import com.jamith.booksformeseller.service.SignUpService;
import com.jamith.booksformeseller.util.StorageFolders;

public class SignUpBrActivity extends AppCompatActivity {
    String userId;
    private EditText etCompanyName, etBusinessRegistrationNumber;
    private Button button;
    private ImageButton imageButton;
    private Uri brDetailDocuments;
    String brDocDownUrl;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_br);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        userId = getIntent().getStringExtra("userId");
        etCompanyName = findViewById(R.id.etCompanyName);
        etBusinessRegistrationNumber = findViewById(R.id.etBusinessRegistrationNumber);
        button = findViewById(R.id.btnSignUpBrDetails);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gatherBusinessDetails();
            }
        });
        imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            brDetailDocuments = data.getData();
            Toast.makeText(this, "Document selected", Toast.LENGTH_SHORT).show();
            Log.d("Image Path", brDetailDocuments.getPath());
        }
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

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void gatherBusinessDetails() {
        String companyName = etCompanyName.getText().toString();
        String brNumber = etBusinessRegistrationNumber.getText().toString();


        if (brDetailDocuments == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        } else {
            new FirebaseStorageService().uploadFile(
                    brDetailDocuments,
                    StorageFolders.DOCUMENTS,
                    new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            brDocDownUrl = o.toString();
                            Log.d("image upload success", o.toString());
                        }
                    }
                    ,
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpBrActivity.this, "Failed to upload file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }

        SellerSignUpBrRequest sellerSignUpBrRequest = new SellerSignUpBrRequest(userId, companyName, brNumber, brDocDownUrl);
        SignUpService signUpService = new SignUpService();
        signUpService.sendSellerBrDetails(sellerSignUpBrRequest, new SignUpService.SignUpCallback() {
            @Override
            public void onSuccess(SellerSignUpResponseDTO response) {
                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpBrActivity.this, "Seller registered successfully!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignUpBrActivity.this, SignUpImageActivity.class);
                    intent.putExtra("userId", response.getId());
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpBrActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onFailure(String failureMessage) {
                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpBrActivity.this, "Failure: " + failureMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

}