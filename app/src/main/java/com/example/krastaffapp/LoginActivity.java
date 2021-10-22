package com.example.krastaffapp;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.krastaffapp.helper.AppController;
import com.example.krastaffapp.helper.PrefManager;
import com.example.krastaffapp.registration.OtpActivity;
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

public class LoginActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

    private EditText inputstaffno;
    private TextInputLayout inputpass;
    private PrefManager pref;
    private ProgressDialog pDialog;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button staff_login = findViewById(R.id.staff_login_btn);

        inputstaffno = findViewById(R.id.staffid_login);
        inputpass = findViewById(R.id.staffid_pass);


        pref = new PrefManager(this);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);




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

                        SharedPreferences ui = getSharedPreferences("UserInfo", MODE_PRIVATE);
                        SharedPreferences.Editor edUi = ui.edit();
                        edUi.putString("KEY_STAFFNAME", staffname);
                        edUi.putString("KEY_STAFFDEPT", staffdept);
                        edUi.putString("KEY_STAFFTITLE", stafftitle);
                        edUi.putString("KEY_STAFFNUMBER", staffnumber);
                        edUi.putString("KEY_STAFFEMAIL", staffemail);
                        edUi.putString("KEY_MOBILE", staffphonenumber);
                        edUi.apply();

                        PrefManager pref2 = new PrefManager(getApplicationContext());

                        pref2.createLogin(staffphonenumber, staffname, staffdept, stafftitle, staffnumber, staffemail);


                        pDialog.dismiss();

                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                        finish();


                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

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

}
