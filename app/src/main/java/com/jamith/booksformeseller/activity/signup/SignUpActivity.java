package com.jamith.booksformeseller.activity.signup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hbb20.CountryCodePicker;
import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.dto.requestDTO.SellerSignUpRequest;
import com.jamith.booksformeseller.dto.responseDTO.SellerSignUpResponseDTO;
import com.jamith.booksformeseller.service.SignUpService;

public class SignUpActivity extends AppCompatActivity {

    private EditText etFullNameOrRepresentative, etEmail, etPassword, etPhoneNumber, etConfirmPassword;
    private EditText etStreet, etCity, etState, etPostalCode, etCountry;
    private EditText etCompanyName, etBusinessRegistrationNumber;
    private Spinner spSellerType;
    private Button btnSignUp;
    private ProgressBar progressBar;
    private CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etFullNameOrRepresentative = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        spSellerType = findViewById(R.id.spinnerSellerType);
        btnSignUp = findViewById(R.id.btnSignUp);
        progressBar = findViewById(R.id.signUpProgressBar);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        countryCodePicker = findViewById(R.id.countryCodePicker);
        countryCodePicker.registerCarrierNumberEditText(etPhoneNumber);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gatherSellerDetails();
            }
        });
    }

    private void gatherSellerDetails() {
        progressBar.setVisibility(View.VISIBLE);
        String fullNameOrRepresentative = etFullNameOrRepresentative.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phoneNumber = "+" + countryCodePicker.getFullNumber().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String sellerType = spSellerType.getSelectedItem().toString();

        if (validateInputs(fullNameOrRepresentative, email, password, confirmPassword, phoneNumber)) {
            SellerSignUpRequest sellerSignUpRequest = new SellerSignUpRequest(sellerType, fullNameOrRepresentative,
                    email, password, phoneNumber);
            Log.d("sign up data", sellerSignUpRequest.toString());
            SignUpService signUpService = new SignUpService();
            signUpService.sendSellerSignUpData(sellerSignUpRequest, new SignUpService.SignUpCallback() {
                @Override
                public void onSuccess(SellerSignUpResponseDTO response) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignUpActivity.this, "Seller registered successfully!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignUpActivity.this, SignUpAddressActivity.class);
                        intent.putExtra("userId", response.getId());
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignUpActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onFailure(String failureMessage) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignUpActivity.this, "Failure: " + failureMessage, Toast.LENGTH_LONG).show();
                    });
                }
            });
        }
    }

    private boolean validateInputs(String fullName, String email, String password, String confirmPassword, String phoneNumber) {
        if (fullName.isEmpty()) {
            etFullNameOrRepresentative.setError("Full Name or Representative is required");
            return false;
        }
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password is not equal");
            return false;
        }
        if (phoneNumber.isEmpty()) {
            etPhoneNumber.setError("Phone Number is required");
            return false;
        }
        return true;
    }
}