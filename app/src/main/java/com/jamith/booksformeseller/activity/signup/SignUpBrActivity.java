package com.jamith.booksformeseller.activity.signup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.dto.requestDTO.SellerSignUpBrRequest;
import com.jamith.booksformeseller.dto.responseDTO.SellerSignUpResponseDTO;
import com.jamith.booksformeseller.service.FirebaseStorageService;
import com.jamith.booksformeseller.service.SignUpService;
import com.jamith.booksformeseller.util.StorageFolders;

import java.io.ByteArrayOutputStream;

public class SignUpBrActivity extends AppCompatActivity {
    String userId;
    private EditText etCompanyName, etBusinessRegistrationNumber;
    private Button button;
    private Button imageButton;
    private ImageView imageView;
    private TextView textView;
    private ProgressBar progressBar;
//    private Uri brDetailDocuments;
    String brDocDownUrl;
    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int FILE_PICKER_REQUEST_CODE = 103;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private Uri selectedFileUri;


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
        imageView = findViewById(R.id.brRegImageView);
        textView = findViewById(R.id.textView2);
        progressBar= findViewById(R.id.signUpBrProgressBar);
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
                showFilePickerDialog();
            }
        });
        checkPermissions();
    }

    private void showFilePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option")
                .setItems(new String[]{"Camera", "Gallery", "Select File (PDF, DOCX)"}, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else if (which == 1) {
                        openGallery();
                    } else if (which == 2) {
                        openFilePicker();
                    }
                })
                .show();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                // Gallery Image Selected
                selectedFileUri = data.getData();
                imageView.setImageURI(selectedFileUri);
                textView.setVisibility(View.GONE);
                Toast.makeText(this, "Image Selected from Gallery", Toast.LENGTH_SHORT).show();
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                // Camera Image Captured
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                selectedFileUri = getImageUri(imageBitmap);
                textView.setVisibility(View.GONE);
                imageView.setImageURI(selectedFileUri);
                Toast.makeText(this, "Image Captured from Camera", Toast.LENGTH_SHORT).show();
            } else if (requestCode == FILE_PICKER_REQUEST_CODE) {
                // File Selected (PDF, DOCX, etc.)
                selectedFileUri = data.getData();
                imageView.setImageURI(null);
                textView.setVisibility(View.VISIBLE);
                Toast.makeText(this, "File Selected Success", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Captured Image", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                Toast.makeText(this, "Permissions Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions Denied! Camera will not work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No camera available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
    }

    private void gatherBusinessDetails() {
        progressBar.setVisibility(View.VISIBLE);
        String companyName = etCompanyName.getText().toString();
        String brNumber = etBusinessRegistrationNumber.getText().toString();
        if (selectedFileUri == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        } else {
            new FirebaseStorageService().uploadFile(selectedFileUri, StorageFolders.DOCUMENTS, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    brDocDownUrl = o.toString();
                    Log.d("image upload success", brDocDownUrl);
                    saveData(companyName, brNumber, brDocDownUrl);
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpBrActivity.this, "Failed to upload file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void saveData(String companyName, String brNumber, String brDocDownUrl) {
        SellerSignUpBrRequest sellerSignUpBrRequest = new SellerSignUpBrRequest(userId, companyName, brNumber, brDocDownUrl);
        SignUpService signUpService = new SignUpService();
        signUpService.sendSellerBrDetails(sellerSignUpBrRequest, new SignUpService.SignUpCallback() {
            @Override
            public void onSuccess(SellerSignUpResponseDTO response) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
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
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpBrActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onFailure(String failureMessage) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpBrActivity.this, "Failure: " + failureMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}