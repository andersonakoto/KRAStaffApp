package com.example.krastaffapp.registration;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.krastaffapp.LoginActivity;
import com.example.krastaffapp.R;
import com.example.krastaffapp.helper.PrefManager;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class SetPassActivity extends AppCompatActivity {

    // defining our own password pattern
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*)" +               // at least 1 special character
                    "(?=\\S+$)" +            // no white spaces
                    ".{6,}" +                // at least 6 characters
                    "$");

    private TextInputLayout newpass, reenterpass;
    private ProgressDialog pDialog;
    private PrefManager pref;


    @SuppressLint("WrongViewCast")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_set_pass);

        Button setpass_reg = findViewById(R.id.next_set_pass);
        newpass = findViewById(R.id.newpass);
        reenterpass = findViewById(R.id.reenterpass);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        pref = new PrefManager(this);


        newpass.setError(null);
        reenterpass.setError(null);



        setpass_reg.setOnClickListener(view -> {

            String passwordInput = Objects.requireNonNull(newpass.getEditText()).getText().toString().trim();
            String passwordConf = Objects.requireNonNull(reenterpass.getEditText()).getText().toString().trim();

            if(validatePassword()){

            if (passwordInput.equals(passwordConf)) {

                Log.d("KRA:" ,"CHECK: " + passwordInput + passwordConf);


                pDialog.setMessage("Setting ...");
                pDialog.show();

                StringRequest strReq = new StringRequest(Request.Method.POST,
                        "http://10.151.1.114/query3.php", response -> {
                    Log.d("KRA" ,"OTP-PASS-RESPONSE:" + response);

                    try {
                        JSONObject responseObj = new JSONObject(response);

                        // Parsing json object response
                        // response will be a json object
                        boolean error = responseObj.getBoolean("error");
                        String message = responseObj.getString("message");

                        // checking for error, if not error SMS is initiated
                        // device should receive it shortly
                        if (!error) {

                            PrefManager pref2 = new PrefManager(getApplicationContext());

                            // boolean flag saying device is waiting for sms
                            pref.setIsWaitingForPass(false);


                            Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent2);
                            finish();
                            pDialog.dismiss();

                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        }


                    } catch (JSONException e) {
                        Log.e("KRA:", "Error: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "Password authentication failure. Please try again.", Toast.LENGTH_LONG).show();
                        pDialog.dismiss();

                    }

                }, error -> {
                    Log.e("KRA:", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Connection timed out. Please try again.", Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                }) {

                    /**
                     * Passing user parameters to the SQl server
                     */
                    @Override
                    protected Map<String, String> getParams() {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
                        String mobile = pref.getString("KEY_MOBILE", null);
                        Log.d("KRA:", "Phone " + mobile);

                        Map<String, String> params = new HashMap<>();
                        params.put("staffPassword", passwordInput);
                        params.put("staffPhone", mobile);
                        Log.e("OTP-DATA: ", params.toString());
                        return params;
                    }

                };
                RequestQueue requestQueue = Volley.newRequestQueue(SetPassActivity.this);

                // Adding the StringRequest object into requestQueue.
                requestQueue.add(strReq);


            } else {

                Toast.makeText(getApplicationContext(), "Passwords do not match. Please check and try again.", Toast.LENGTH_LONG).show();
                Log.d("KRA:" ,"CHECK: " + passwordInput + passwordConf);

            }
        }

        });

//        if (pref.isWaitingForPass()) {
//            Intent intent2 = new Intent(this, SetPassActivity.class);
//            startActivity(intent2);
//            finish();
//
//        }


    }

    private boolean validatePassword() {
        String passwordInput = Objects.requireNonNull(newpass.getEditText()).getText().toString().trim();
        String passwordConf = Objects.requireNonNull(reenterpass.getEditText()).getText().toString().trim();

        // if password field is empty
        // it will display error message "Field can not be empty"
        if (passwordInput.isEmpty() && passwordConf.isEmpty()) {
            newpass.setError("Field can not be empty");
            reenterpass.setError("Field Cannot be empty");

            CountDownTimer cTimer = null;

            cTimer = new CountDownTimer(800, 100) {
                @SuppressLint("SetTextI18n")
                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    newpass.setError(null);
                    reenterpass.setError(null);

                }
            };
            cTimer.start();
            return false;
        }

        // if password does not matches to the pattern
        // it will display an error message "Password is too weak"
        else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            newpass.setError("Password is too weak");
            CountDownTimer cTimer = null;

            cTimer = new CountDownTimer(800, 100) {
                @SuppressLint("SetTextI18n")
                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    newpass.setError(null);
                    reenterpass.setError(null);

                }
            };
            cTimer.start();
            return false;
        } else {
            newpass.setError(null);
            reenterpass.setError(null);
            return true;
        }
    }
    @Override
    public void onBackPressed() {

            /*pref.clearSession();
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);*/
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        finish();
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        finishAffinity();
        finishAndRemoveTask();
            /*System.exit(0);
            onDestroy();*/
//            progressDialog.dismiss();
        super.onBackPressed();

    }
}
