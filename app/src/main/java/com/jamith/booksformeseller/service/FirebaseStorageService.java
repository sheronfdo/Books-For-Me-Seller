package com.jamith.booksformeseller.service;

import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class FirebaseStorageService {

    private static final String BASE_STORAGE_PATH = "uploads/";

    public void uploadFile(Uri fileUri, String folderName,
                           OnSuccessListener onSuccess,
                           OnFailureListener onFailure) {
        if (fileUri == null) {
            onFailure.onFailure(new Exception("File URI is null"));
            return;
        }

        String fileName = UUID.randomUUID().toString(); // Generate a unique file name
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference(BASE_STORAGE_PATH + folderName + "/" + fileName);

        storageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL
                    storageRef.getDownloadUrl().addOnSuccessListener(onSuccess)
                            .addOnFailureListener(onFailure);
                })
                .addOnFailureListener(onFailure);
    }
}