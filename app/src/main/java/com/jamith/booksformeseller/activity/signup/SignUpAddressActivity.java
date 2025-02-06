package com.jamith.booksformeseller.activity.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.dto.requestDTO.SellerSignUpAddressRequest;
import com.jamith.booksformeseller.dto.responseDTO.SellerSignUpResponseDTO;
import com.jamith.booksformeseller.service.SignUpService;

public class SignUpAddressActivity extends AppCompatActivity {
    private EditText etStreet, etCity, etState, etPostalCode, etCountry;
    private Button button;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_address);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        userId = getIntent().getStringExtra("userId");
        etStreet = findViewById(R.id.etStreet);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etPostalCode = findViewById(R.id.etPostalCode);
        etCountry = findViewById(R.id.etCountry);
        button = findViewById(R.id.btnSignUpAddress);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gatherSellerAddressDetails();
            }
        });

    }

    private void gatherSellerAddressDetails() {
        String street = etStreet.getText().toString();
        String city = etCity.getText().toString();
        String state = etState.getText().toString();
        String postalCode = etPostalCode.getText().toString();
        String country = etCountry.getText().toString();


        SellerSignUpAddressRequest sellerSignUpAddressRequest =
                new SellerSignUpAddressRequest(userId, street, city, state, postalCode, country);

        SignUpService signUpService = new SignUpService();
        signUpService.sendSellerAddressData(sellerSignUpAddressRequest, new SignUpService.SignUpCallback() {
            @Override
            public void onSuccess(SellerSignUpResponseDTO response) {
                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpAddressActivity.this, "Seller registered successfully!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignUpAddressActivity.this, SignUpBrActivity.class);
                    intent.putExtra("userId", response.getId());
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpAddressActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onFailure(String failureMessage) {
                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpAddressActivity.this, "Failure: " + failureMessage, Toast.LENGTH_LONG).show();
                });
            }
        });

    }

}