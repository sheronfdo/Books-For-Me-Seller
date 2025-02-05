package com.jamith.booksformeseller.service;

import android.util.Log;

import com.google.gson.Gson;
import com.jamith.booksformeseller.dto.ErrorResponse;
import com.jamith.booksformeseller.dto.SellerSignUpResponseDTO;
import com.jamith.booksformeseller.model.Seller;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpService {

    private static final String BASE_URL = "http://192.168.1.142:8080/api/seller/register";
    private static final MediaType JSONMediaType = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public void sendSellerSignUpData(Seller seller, SignUpCallback callback) {
        String jsonData = gson.toJson(seller);
        RequestBody body = RequestBody.create(jsonData, JSONMediaType);
        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Network Error", "Failed to connect to the server: " + e.getMessage());
                callback.onFailure("No internet connection or server unreachable.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        Log.d("Response Success", responseBody);
                        SellerSignUpResponseDTO signUpResponse = gson.fromJson(responseBody, SellerSignUpResponseDTO.class);
                        callback.onSuccess(signUpResponse);
                    } catch (Exception e) {
                        Log.e("Parsing Error", "Failed to parse the response: " + e.getMessage());
                        callback.onError("Failed to process the server response.");
                    }
                } else {
                    try {
                        String errorBody = response.body().string();
                        Log.e("Response Error", errorBody);
                        ErrorResponse errorResponse = gson.fromJson(errorBody, ErrorResponse.class);
                        callback.onError(errorResponse.getMessage());
                    } catch (Exception e) {
                        Log.e("Error Parsing", "Failed to parse the error response: " + e.getMessage());
                        callback.onError("An unexpected error occurred.");
                    }
                }
            }
        });
    }
    public interface SignUpCallback {
        void onSuccess(SellerSignUpResponseDTO response);
        void onError(String errorMessage);
        void onFailure(String failureMessage);
    }
}
