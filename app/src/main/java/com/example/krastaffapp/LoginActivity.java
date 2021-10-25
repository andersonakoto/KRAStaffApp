package com.example.krastaffapp;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.krastaffapp.helper.AppController;
import com.example.krastaffapp.helper.PrefManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    private EditText inputstaffno;
    private TextInputLayout inputpass;
    private ProgressDialog pDialog;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;


    @RequiresApi(api = Build.VERSION_CODES.R)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);

        Button staff_login = findViewById(R.id.staff_login_btn);

        inputstaffno = findViewById(R.id.staffid_login);
        inputpass = findViewById(R.id.staffid_pass);


        PrefManager pref = new PrefManager(this);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Executor executor = ContextCompat.getMainExecutor(this);
                biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode,@NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);

                        Toast.makeText(getApplicationContext(), "Type in your credentials to login.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                        finish();
                        Toast.makeText(getApplicationContext(), "Logged in successfully.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });

                promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Biometric login for KRA Staff App")
                        .setSubtitle("Use your device biometrics to login")
                        .setNegativeButtonText("Use your password instead")
                        .build();


                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, 0);
                break;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                break;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                break;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                break;
        }

        if (pref.isLoggedIn()) {
            biometricPrompt.authenticate(promptInfo);
        }



        staff_login.setOnClickListener(view -> {

            String staffreg = inputstaffno.getText().toString().trim();
            String staffpass = Objects.requireNonNull(inputpass.getEditText()).getText().toString().trim();


            if (!staffreg.isEmpty() && !staffpass.isEmpty()) {

                pDialog.setMessage("Checking ...");
                pDialog.show();

            StringRequest strReq = new StringRequest(
                    Request.Method.POST,
                    "http://10.151.1.114/query4.php", response -> {
                Log.d("KRA:","STAFFID-RESPONSE:" + response);

                try {
                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    // checking for error, if not error SMS is initiated
                    // device should receive it shortly
                    if (!error) {

                        String staffname = responseObj.getJSONObject("data").getJSONObject("staffMember").getString("staffName");
                        String stafftitle = responseObj.getJSONObject("data").getJSONObject("staffMember").getString("staffTitle");
                        String staffdept = responseObj.getJSONObject("data").getJSONObject("staffMember").getString("staffDepartment");
                        String staffnumber = responseObj.getJSONObject("data").getJSONObject("staffMember").getString("staffNumber");
                        String staffemail = responseObj.getJSONObject("data").getJSONObject("staffMember").getString("staffEmail");
                        String staffphonenumber = responseObj.getJSONObject("data").getJSONObject("staffMember").getString("staffPhoneNumber");

                        String token = responseObj.getString("token");

                        SharedPreferences ui = getSharedPreferences("UserInfo", MODE_PRIVATE);
                        SharedPreferences.Editor edUi = ui.edit();
                        edUi.putString("KEY_STAFFNAME", staffname);
                        edUi.putString("KEY_STAFFDEPT", staffdept);
                        edUi.putString("KEY_STAFFTITLE", stafftitle);
                        edUi.putString("KEY_STAFFNUMBER", staffnumber);
                        edUi.putString("KEY_STAFFEMAIL", staffemail);
                        edUi.putString("KEY_MOBILE", staffphonenumber);
                        edUi.putString("KEY_TOKEN", token);
                        edUi.apply();

                        PrefManager pref2 = new PrefManager(getApplicationContext());

                        pref2.createLogin(staffphonenumber, staffname, staffdept, stafftitle, staffnumber, staffemail, token);


                        // Prompt appears when user clicks "Log in".
                        // Consider integrating with the keystore to unlock cryptographic operations,
                        // if needed by your app.

                        pDialog.dismiss();

                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                        finish();


                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        Log.d("KRA:", "TOKEN: " + token);


                    } else {

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        pDialog.dismiss();

                    }


                } catch (JSONException e) {
                    Log.e("KRA:", "Error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Subscription/Authentication failed. Please try again.", Toast.LENGTH_LONG).show();
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
                    Map<String, String> params = new HashMap<>();
                    params.put("staffNumber", staffreg);
                    params.put("staffPassword", staffpass);

                    Log.d( "KRA:", "Checking Staff Number: " + params.toString());

                    return params;
                }

            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq);


        } else {
            Toast.makeText(getApplicationContext(), "Please enter a valid KRA Staff Number and password.", Toast.LENGTH_LONG).show();
            pDialog.dismiss();

        }

        });


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
