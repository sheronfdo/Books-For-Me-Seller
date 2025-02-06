package com.jamith.booksformeseller.service;

import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class FirebaseStorageService {

    private static final String BASE_STORAGE_PATH = "uploads/";

    /**
     * Uploads a file to Firebase Storage.
     *
     * @param fileUri    The URI of the file to upload.
     * @param folderName The folder name in Firebase Storage where the file will be stored.
     * @param onSuccess  Callback for successful upload (returns download URL).
     * @param onFailure  Callback for failed upload (returns error message).
     */
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