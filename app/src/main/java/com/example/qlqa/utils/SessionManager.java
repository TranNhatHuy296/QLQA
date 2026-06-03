package com.example.qlqa.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "QLQA_Session";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    private static final String KEY_STAFF_ID = "staff_id";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String username, String role, int staffId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE, role);
        editor.putInt(KEY_STAFF_ID, staffId);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, null);
    }

    public int getStaffId() {
        return pref.getInt(KEY_STAFF_ID, -1);
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }
}
