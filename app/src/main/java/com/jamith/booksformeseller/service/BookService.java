package com.jamith.booksformeseller.service;

import android.util.Log;

import com.google.gson.Gson;
import com.jamith.booksformeseller.dto.requestDTO.AddNewBookDTO;
import com.jamith.booksformeseller.dto.requestDTO.SellerSignUpRequest;
import com.jamith.booksformeseller.dto.responseDTO.BookAddResponseDTO;
import com.jamith.booksformeseller.dto.responseDTO.ErrorResponse;
import com.jamith.booksformeseller.dto.responseDTO.SellerSignUpResponseDTO;
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

public class BookService {
    private static final MediaType JSONMediaType = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private final ModelMapper modelMapper = new ModelMapper();

    public void addNewBookData(AddNewBookDTO addNewBookDTO, BookServiceCallback callback) {
        String jsonData = gson.toJson(addNewBookDTO);
        RequestBody body = RequestBody.create(jsonData, JSONMediaType);
        Request request = new Request.Builder()
                .url(UrlConstants.ADD_NEW_BOOK_URL)
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
                        BookAddResponseDTO bookAddResponseDTO = modelMapper.map(successResponse.getData(), BookAddResponseDTO.class);
                        Log.d("Book Add Details", bookAddResponseDTO.toString());
                        callback.onSuccess(bookAddResponseDTO);
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

    public interface BookServiceCallback {
        void onSuccess(BookAddResponseDTO response);
        void onError(String errorMessage);
        void onFailure(String failureMessage);
    }
}
