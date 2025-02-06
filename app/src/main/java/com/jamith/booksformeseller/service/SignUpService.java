package com.jamith.booksformeseller.service;

import android.util.Log;

import com.google.gson.Gson;
import com.jamith.booksformeseller.dto.requestDTO.SellerSignUpAddressRequest;
import com.jamith.booksformeseller.dto.requestDTO.SellerSignUpBrRequest;
import com.jamith.booksformeseller.dto.requestDTO.SellerSignUpImageRequest;
import com.jamith.booksformeseller.dto.responseDTO.ErrorResponse;
import com.jamith.booksformeseller.dto.responseDTO.SellerSignUpResponseDTO;
import com.jamith.booksformeseller.dto.requestDTO.SellerSignUpRequest;
import com.jamith.booksformeseller.dto.responseDTO.SuccessResponse;
import com.jamith.booksformeseller.util.UrlConstants;

import org.modelmapper.ModelMapper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpService {
    private static final MediaType JSONMediaType = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private final ModelMapper modelMapper = new ModelMapper();

    public void sendSellerSignUpData(SellerSignUpRequest sellerSignUpRequest, SignUpCallback callback) {
        String jsonData = gson.toJson(sellerSignUpRequest);
        RequestBody body = RequestBody.create(jsonData, JSONMediaType);
        Request request = new Request.Builder()
                .url(UrlConstants.SELLER_REGISTER_URL)
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
                        SuccessResponse successResponse = gson.fromJson(responseBody, SuccessResponse.class);
                        SellerSignUpResponseDTO signUpResponse = modelMapper.map(successResponse.getData(), SellerSignUpResponseDTO.class);
                        Log.d("Sign Up Details", signUpResponse.toString());
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

    public void sendSellerAddressData(SellerSignUpAddressRequest sellerSignUpAddressRequest, SignUpCallback callback) {
        String jsonData = gson.toJson(sellerSignUpAddressRequest);
        RequestBody body = RequestBody.create(jsonData, JSONMediaType);
        Request request = new Request.Builder()
                .url(UrlConstants.SELLER_SET_ADDRESS_URL)
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
                        SuccessResponse successResponse = gson.fromJson(responseBody, SuccessResponse.class);
                        SellerSignUpResponseDTO signUpResponse = modelMapper.map(successResponse.getData(), SellerSignUpResponseDTO.class);
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

    public void sendSellerBrDetails(SellerSignUpBrRequest sellerSignUpBrRequest, SignUpCallback callback){
        String jsonData = gson.toJson(sellerSignUpBrRequest);
        RequestBody body = RequestBody.create(jsonData, JSONMediaType);
        Request request = new Request.Builder()
                .url(UrlConstants.SELLER_SET_BR_DETAILS_URL)
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
                        SuccessResponse successResponse = gson.fromJson(responseBody, SuccessResponse.class);
                        SellerSignUpResponseDTO signUpResponse = modelMapper.map(successResponse.getData(), SellerSignUpResponseDTO.class);
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

    public void sendSellerImage(SellerSignUpImageRequest sellerSignUpImageRequest, SignUpCallback callback){
        String jsonData = gson.toJson(sellerSignUpImageRequest);
        RequestBody body = RequestBody.create(jsonData, JSONMediaType);
        Request request = new Request.Builder()
                .url(UrlConstants.SELLER_SET_IMAGE_URL)
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
                        SuccessResponse successResponse = gson.fromJson(responseBody, SuccessResponse.class);
                        SellerSignUpResponseDTO signUpResponse = modelMapper.map(successResponse.getData(), SellerSignUpResponseDTO.class);
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
