package com.jamith.booksformeseller.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DataSyncManager {
    private static final String PREFS_NAME = "SyncPrefs";
    private static final String LAST_SYNC_TIME_KEY = "lastSyncTime";
    private static final long SYNC_INTERVAL = 6 * 60 * 60 * 1000;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final AppDatabaseHelper localDB;
    private final Context context;
    private final SharedPreferences sharedPreferences;

    public interface SyncCompleteListener {
        void onSyncComplete(boolean success);
    }

    public DataSyncManager(Context context) {
        this.context = context;
        this.localDB = new AppDatabaseHelper(context);
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    private long getLastSyncTime() {
        return sharedPreferences.getLong(LAST_SYNC_TIME_KEY, 0);
    }

    private void saveLastSyncTime() {
        sharedPreferences.edit()
                .putLong(LAST_SYNC_TIME_KEY, System.currentTimeMillis())
                .apply();
    }

    private boolean needsSync() {
        long lastSync = getLastSyncTime();
        return (System.currentTimeMillis() - lastSync) > SYNC_INTERVAL;
    }

    public void performInitialSync(SyncCompleteListener listener) {
        if (isNetworkAvailable() && needsSync()) {
            syncCategories(listener);
            syncLanguages(listener);
            listener.onSyncComplete(true);
        } else {
            // If no sync needed, just complete
            listener.onSyncComplete(true);
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    private void syncCategories(SyncCompleteListener listener) {
        db.collection("categories").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                localDB.clearTable(AppDatabaseHelper.TABLE_CATEGORIES);
                SQLiteDatabase db = localDB.getWritableDatabase();
                db.beginTransaction();

                try {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ContentValues values = new ContentValues();
                        values.put(AppDatabaseHelper.COL_CAT_ID, document.getId());
                        values.put(AppDatabaseHelper.COL_CAT_NAME, document.getString("name"));
                        db.insert(AppDatabaseHelper.TABLE_CATEGORIES, null, values);
                    }
                    db.setTransactionSuccessful();
                    saveLastSyncTime();
                    Log.d("categories", "updated");
                } finally {
                    db.endTransaction();
                    db.close();
                }
            } else {
                listener.onSyncComplete(false);
            }
        });
    }

    private void syncLanguages(SyncCompleteListener listener) {
        db.collection("languages").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                localDB.clearTable(AppDatabaseHelper.TABLE_LANGUAGES);
                SQLiteDatabase db = localDB.getWritableDatabase();
                db.beginTransaction();

                try {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ContentValues values = new ContentValues();
                        values.put(AppDatabaseHelper.COL_LANG_ID, document.getId());
                        values.put(AppDatabaseHelper.COL_LANG_NAME, document.getString("name"));
                        db.insert(AppDatabaseHelper.TABLE_LANGUAGES, null, values);
                    }
                    db.setTransactionSuccessful();
                    saveLastSyncTime();
                    Log.d("languages", "updated");
                } finally {
                    db.endTransaction();
                    db.close();
                }
            } else {
                listener.onSyncComplete(false);
            }
        });
    }
}