package com.example.krastaffapp.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.krastaffapp.LoginActivity;
import com.example.krastaffapp.R;
import com.example.krastaffapp.helper.PrefManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StaffidActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    public EditText inputreg;
    private PrefManager pref;
    private ProgressDialog pDialog;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_staffid);

        inputreg = findViewById(R.id.regStaffID);



        Button staff_reg_next = findViewById(R.id.next_register);

        pref = new PrefManager(this);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        staff_reg_next.setOnClickListener(view -> {

            String staffreg = inputreg.getText().toString().trim();

            // validating mobile number
        if (!staffreg.isEmpty()) {

            pDialog.setMessage("Checking ...");
            pDialog.show();

            StringRequest strReq = new StringRequest(
                    Request.Method.POST,
                    "http://10.151.1.114/query1.php", response -> {
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
                        // boolean flag saying device is waiting for sms
                        pref.setIsWaitingForSms(true);
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

                        pDialog.dismiss();

                        Intent intent = new Intent(this, OtpActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                        this.finish();


                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                    } else if(message.equals("You are already registered onto the App. Go back and tap 'Login' to continue.")) {

                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        this.finish();
                        pDialog.dismiss();

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                    }
                    else{
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
                    params.put("staffNo", staffreg);

                    Log.d( "KRA:", "Checking Staff Number: " + params.toString());

                    return params;
                }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(StaffidActivity.this);

            // Adding the StringRequest object into requestQueue.
            requestQueue.add(strReq);

        } else {
            Toast.makeText(getApplicationContext(), "Please enter a valid KRA Staff Number.", Toast.LENGTH_LONG).show();
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

