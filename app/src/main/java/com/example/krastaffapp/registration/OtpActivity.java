package com.example.krastaffapp.registration;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.krastaffapp.R;
import com.example.krastaffapp.helper.AppController;
import com.example.krastaffapp.helper.PrefManager;
import com.example.krastaffapp.interfaces.OTPInterface;
import com.example.krastaffapp.receiver.SmsBroadcastReceiver;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OtpActivity extends AppCompatActivity implements OTPInterface, View.OnClickListener {

    private TextView resend_after;
    private Button btn_resend;
    private ProgressDialog pDialog;
    SmsBroadcastReceiver mSmsBroadcastReceiver;
    private PrefManager pref;
    private EditText inputOtp;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_otp);

        startTimer();


        mSmsBroadcastReceiver = new SmsBroadcastReceiver();

        mSmsBroadcastReceiver.setOnOtpListeners(OtpActivity.this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        getApplicationContext().registerReceiver(mSmsBroadcastReceiver, intentFilter);


        startSMSListener();

        Button otp_reg = findViewById(R.id.next_otp);

        resend_after = findViewById(R.id.resend_after);
        btn_resend = findViewById(R.id.button_resend);
        inputOtp = findViewById(R.id.otp_view);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);



        pref = new PrefManager(this);

//        if (pref.isWaitingForSms()) {
//            Intent intent2 = new Intent(this, OtpActivity.class);
//            startActivity(intent2);
//            finish();
//        }


        otp_reg.setOnClickListener(view -> {
            validateOtp();
        });

        btn_resend.setOnClickListener(view -> {
            Intent intent = new Intent(this, StaffidActivity.class);
            startActivity(intent);
            finish();

        });


    }


    private void verifyOtp(final String otp) {
            pDialog.setMessage("Verifying ...");
            pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://10.151.1.114/query2.php", response -> {
            //Log.d("OTP-AUTH-RESPONSE: ", response);

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
                    pref.setIsWaitingForSms(false);
                    pref.setIsWaitingForPass(true);
//                    SharedPreferences pref = getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
//                    String mobile = pref.getString("KEY_MOBILE", null);      // getting String
//                    String staffname = pref.getString("KEY_STAFFNAME",null);
//                    String staffdept = pref.getString("KEY_STAFFDEPT", null);
//                    String stafftitle = pref.getString("KEY_STAFFTITLE", null);
//                    String staffnumber = pref.getString("KEY_STAFFNUMBER", null);
//                    String staffemail = pref.getString("KEY_STAFFEMAIL", null);
//
//                    pref2.createLogin(mobile, staffname, staffdept, stafftitle, staffnumber, staffemail);


                    Intent intent2 = new Intent(this, SetPassActivity.class);
                    startActivity(intent2);
                    finish();
                    pDialog.dismiss();
                    cancelTimer();

                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                }


            } catch (JSONException e) {
                Log.e("KRA:", "Error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "OTP authentication failure. Please try again.", Toast.LENGTH_LONG).show();
                pDialog.dismiss();
                cancelTimer();

            }

        }, error -> {
            Log.e("KRA:", "Error: " + error.getMessage());
            Toast.makeText(getApplicationContext(), "Connection timed out. Please try again.", Toast.LENGTH_LONG).show();
            pDialog.dismiss();
            cancelTimer();

        }) {

            /**
             * Passing user parameters to the SQl server
             */
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
                String mobile = pref.getString("KEY_MOBILE", null);
                Log.d("KRA:", "Phone " + mobile );

                Map<String, String> params = new HashMap<>();
                params.put("staffOTP", otp);
                params.put("staffPhone", mobile);
                Log.e("OTP-DATA: ", params.toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }




    public void startSMSListener() {
        SmsRetrieverClient mClient = SmsRetriever.getClient(this);
        Task<Void> mTask = mClient.startSmsRetriever();
        mTask.addOnSuccessListener(aVoid -> {
            //layoutInput.setVisibility(View.GONE);
            //layoutVerify.setVisibility(View.VISIBLE);
            // Toast.makeText(ActivityFirstpage.this, "SMS Retriever starts", Toast.LENGTH_LONG).show();
        });
        mTask.addOnFailureListener(e -> {
            //Toast.makeText(ActivityFirstpage.this, "Error", Toast.LENGTH_LONG).show();
        });
    }
    //Declare timer
    private CountDownTimer cTimer = null;


    //start timer function
    private void startTimer() {
        cTimer = new CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                resend_after.setVisibility(View.VISIBLE);
                btn_resend.setVisibility(View.INVISIBLE);
                resend_after.setText("Resend OTP after: " + millisUntilFinished / 1000);

            }

            public void onFinish() {
                btn_resend.setVisibility(View.VISIBLE);
                resend_after.setVisibility(View.INVISIBLE);
            }
        };
        cTimer.start();
    }


    //cancel timer
    private void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    private void validateOtp() {

        String otp = inputOtp.getText().toString().trim();

        // validating mobile number
        // it should be of 10 digits length
        if (!otp.isEmpty()) {

            verifyOtp(otp);

        } else {
            Toast.makeText(getApplicationContext(), "Please enter a valid OTP.", Toast.LENGTH_LONG).show();
            pDialog.dismiss();

        }


    }

    @Override
    public void onOtpReceived(String otp) {
        inputOtp.setText(otp);
        verifyOtp(otp);

    }

    @Override
    public void onOtpTimeout() {

    }

    @Override
    public void onClick(View view) {

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
