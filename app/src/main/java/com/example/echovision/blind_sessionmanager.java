package com.example.echovision;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class blind_sessionmanager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    private static final String PREF_NAME = "BlindLogin";
    private static final String IS_LOGIN = "IsLoggedIn";

    // Keys
    public static final String KEY_ID = "id";
    public static final String KEY_FINGERPRINT_ID = "fingerprint_id";
    public static final String KEY_CREATED_AT = "created_at";

    public blind_sessionmanager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // ✅ Create login session
    public void createLoginSession(String id, String fingerprintId, String createdAt) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_FINGERPRINT_ID, fingerprintId);
        editor.putString(KEY_CREATED_AT, createdAt);
        editor.apply(); // use apply() instead of commit() for async save
    }

    // ✅ Get stored user data
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_FINGERPRINT_ID, pref.getString(KEY_FINGERPRINT_ID, null));
        user.put(KEY_CREATED_AT, pref.getString(KEY_CREATED_AT, null));
        return user;
    }

    // ✅ Check login status
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    // ✅ Logout user
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}
