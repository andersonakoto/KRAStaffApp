package com.example.krastaffapp.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;


public class PrefManager {
    // Shared Preferences
    private final SharedPreferences pref;

    // Editor for Shared preferences
    private final Editor editor;

    // Shared preferences file name
    private static final String PREF_NAME = "KRASTAFFAPP";

    // All Shared Preferences Keys
    private static final String KEY_IS_WAITING_FOR_SMS = "IsWaitingForSms";
    private static final String KEY_IS_WAITING_FOR_PASS = "IsWaitingForPass";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private static final String KEY_MOBILE = "staffPhoneNumber";
    private static final String KEY_STAFFNAME = "staffName";
    private static final String KEY_STAFFDEPT = "staffDept";
    private static final String KEY_STAFFTITLE = "staffTitle";
    private static final String KEY_STAFFNUMBER = "staffNumber";
    private static final String KEY_STAFFEMAIL = "staffEmail";
    private static final String KEY_TOKEN = "token";




    public PrefManager(Context context) {
        // Context
        // Shared pref mode
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    public void setIsWaitingForSms(boolean isWaiting) {
        editor.putBoolean(KEY_IS_WAITING_FOR_SMS, isWaiting);
        editor.commit();
    }

    public boolean isWaitingForSms() {
        return pref.getBoolean(KEY_IS_WAITING_FOR_SMS, false);
    }

    public void setIsWaitingForPass(boolean isWaiting) {
        editor.putBoolean(KEY_IS_WAITING_FOR_PASS, isWaiting);
        editor.commit();
    }

    public boolean isWaitingForPass() {
        return pref.getBoolean(KEY_IS_WAITING_FOR_PASS, false);
    }


    public String getMobileNumber() {
        return pref.getString(KEY_MOBILE, null);

    }

    public void createLogin(String mobile, String staffname, String staffdept, String stafftitle, String staffnumber, String staffemail, String token) {

        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_STAFFNAME, staffname);
        editor.putString(KEY_STAFFDEPT, staffdept);
        editor.putString(KEY_STAFFTITLE, stafftitle);
        editor.putString(KEY_STAFFNUMBER, staffnumber);
        editor.putString(KEY_STAFFEMAIL, staffemail);
        editor.putString(KEY_TOKEN, token);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("staffPhoneNumber", pref.getString(KEY_MOBILE, null));
        return profile;
    }
}