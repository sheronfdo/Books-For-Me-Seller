package com.jamith.booksformeseller.service;

import android.util.Log;

import com.google.gson.Gson;
import com.jamith.booksformeseller.model.Seller;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class SignUpService {

    private static final String BASE_URL = "http://192.168.1.142:8080/api/seller/register";
    private static final MediaType JSONMediaType = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();

    public void sendSellerSignUpData(Seller seller) {
        Gson gson = new Gson();
        String jsonData = gson.toJson(seller);
        RequestBody body = RequestBody.create(jsonData, JSONMediaType);

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("Response", response.toString());
                if (response.isSuccessful()) {
                    Log.d("Response", response.body().string());
                } else {
                    Log.d("Response Error", response.toString());
                }
            }
        });
    }

}
