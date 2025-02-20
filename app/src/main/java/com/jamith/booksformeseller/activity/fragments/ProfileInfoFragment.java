package com.jamith.booksformeseller.activity.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.activity.HomeActivity;
import com.jamith.booksformeseller.dto.requestDTO.SellerUpdateDTO;
import com.jamith.booksformeseller.dto.responseDTO.SellerSignUpResponseDTO;
import com.jamith.booksformeseller.model.Profile;
import com.jamith.booksformeseller.service.FirebaseStorageService;
import com.jamith.booksformeseller.service.SignUpService;
import com.jamith.booksformeseller.util.StorageFolders;

import java.io.ByteArrayOutputStream;

public class ProfileInfoFragment extends Fragment {

    private ImageView profileImage;
    private EditText etFullName, etPhoneNumber, etCompanyName, etRegistrationNumber,
            etStreet, etCity, etState, etCountry, etPostalCode, etEmail;
    private Button btnSaveChanges, btnChangeImage;
    private Uri selectedImageUri;
    private int PERMISSION_REQUEST_CODE = 100;
    private int PICK_IMAGE_REQUEST = 101;
    private int CAMERA_REQUEST_CODE = 102;
    private Uri selectedFileUri;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ProgressBar progressBar;
    private HomeActivity homeActivity;
    private String imageUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        profileImage = view.findViewById(R.id.profile_image);
        btnChangeImage = view.findViewById(R.id.btn_change_image);
        etFullName = view.findViewById(R.id.et_full_name);
        etPhoneNumber = view.findViewById(R.id.et_phone_number);
        etCompanyName = view.findViewById(R.id.et_company_name);
        etRegistrationNumber = view.findViewById(R.id.et_registration_number);
        etStreet = view.findViewById(R.id.et_street);
        etCity = view.findViewById(R.id.et_city);
        etState = view.findViewById(R.id.et_state);
        etCountry = view.findViewById(R.id.et_country);
        etPostalCode = view.findViewById(R.id.et_postal_code);
        etEmail = view.findViewById(R.id.et_email);
        btnSaveChanges = view.findViewById(R.id.btn_save_changes);
        checkPermissions();
        loadProfileData();
        progressBar = view.findViewById(R.id.prfoileInfoProgressBar);
        btnChangeImage.setOnClickListener(v -> showFilePickerDialog());
        btnSaveChanges.setOnClickListener(v -> saveProfileChanges());
        homeActivity = (HomeActivity) requireActivity();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        }
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
                Toast.makeText(getContext(), "Permissions Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permissions Denied! Camera will not work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showFilePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose an option")
                .setItems(new String[]{"Camera", "Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else if (which == 1) {
                        openGallery();
                    }
                })
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(getContext(), "No camera available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                selectedFileUri = data.getData();
                Toast.makeText(getContext(), "Image Selected from Gallery", Toast.LENGTH_SHORT).show();
                profileImage.setImageURI(selectedFileUri);

            } else if (requestCode == CAMERA_REQUEST_CODE) {
                // Camera Image Captured
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                selectedFileUri = getImageUri(imageBitmap);
                profileImage.setImageURI(selectedFileUri);
                Toast.makeText(getContext(), "Image Captured from Camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "Captured Image", null);
        return Uri.parse(path);
    }

    private void loadProfileData() {
        if (firebaseAuth.getCurrentUser() != null) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            DocumentReference userRef = firebaseFirestore.collection("sellers").document(userId);
            userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null && value.exists()) {

                        Profile profile = value.toObject(Profile.class);
                        Glide.with(getActivity())
                                .load(profile.getImageUrl())
                                .placeholder(R.drawable.profile)
                                .into(profileImage);
                        imageUrl = profile.getImageUrl();
                        Log.d("address", profile.getAddress().toString());
                        Log.d("businessDetails", profile.getBusinessDetails().toString());

                        etFullName.setText(profile.getFullNameOrRepresentative());
                        etPhoneNumber.setText(profile.getPhoneNumber());
                        etEmail.setText(profile.getEmail());
                        etCompanyName.setText(profile.getBusinessDetails().get("companyName"));
                        etRegistrationNumber.setText(profile.getBusinessDetails().get("businessRegistrationNumber"));
                        etStreet.setText(profile.getAddress().get("street"));
                        etCity.setText(profile.getAddress().get("city"));
                        etState.setText(profile.getAddress().get("state"));
                        etCountry.setText(profile.getAddress().get("country"));
                        etPostalCode.setText(profile.getAddress().get("postalCode"));
                    }
                }
            });
        }

    }


    private void saveProfileChanges() {
        progressBar.setVisibility(View.VISIBLE);
        // Collect updated data
        String fullName = etFullName.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String companyName = etCompanyName.getText().toString();
        String registrationNumber = etRegistrationNumber.getText().toString();
        String street = etStreet.getText().toString();
        String city = etCity.getText().toString();
        String state = etState.getText().toString();
        String country = etCountry.getText().toString();
        String postalCode = etPostalCode.getText().toString();

        SellerUpdateDTO sellerUpdateDTO = new SellerUpdateDTO();
        sellerUpdateDTO.setId(firebaseAuth.getCurrentUser().getUid());
        sellerUpdateDTO.setFullName(fullName);
        sellerUpdateDTO.setPhoneNumber(phoneNumber);
        sellerUpdateDTO.setCompanyName(companyName);
        sellerUpdateDTO.setRegistrationNumber(registrationNumber);
        sellerUpdateDTO.setStreet(street);
        sellerUpdateDTO.setCity(city);
        sellerUpdateDTO.setState(state);
        sellerUpdateDTO.setCountry(country);
        sellerUpdateDTO.setPostalCode(postalCode);

        if (selectedFileUri == null) {
            sellerUpdateDTO.setImageUrl(imageUrl);
            saveData(sellerUpdateDTO);
        } else {
            new FirebaseStorageService().uploadFile(selectedFileUri, StorageFolders.IMAGES, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    requireActivity().runOnUiThread(() -> {
                        String sellerImageDownloadUrl = o.toString();
                        Log.d("image upload success", o.toString());
                        sellerUpdateDTO.setImageUrl(sellerImageDownloadUrl);
                        saveData(sellerUpdateDTO);
                    });
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to upload file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }
            });
        }

    }

    private void saveData(SellerUpdateDTO sellerUpdateDTO) {
        SignUpService signUpService = new SignUpService();
        signUpService.updateSellerProfile(sellerUpdateDTO, new SignUpService.SignUpCallback() {
            @Override
            public void onSuccess(SellerSignUpResponseDTO response) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Seller Updated Successfully!", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(SignUpImageActivity.this, MainActivity.class);
//                    intent.putExtra("userId", response.getId());
//                    startActivity(intent);
//                    finish();
                    homeActivity.loadFragment(new HomeFragment());
                });
            }

            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onFailure(String failureMessage) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failure: " + failureMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

}