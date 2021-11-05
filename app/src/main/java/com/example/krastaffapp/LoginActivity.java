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
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.KeyTemplates;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.daead.DeterministicAeadConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

import kotlin.text.Charsets;

public class LoginActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    private ProgressDialog pDialog;
    private BiometricPrompt biometricPrompt;


    @RequiresApi(api = Build.VERSION_CODES.R)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);



        Button staff_login = findViewById(R.id.staff_login_btn);

        EditText inputstaffno = findViewById(R.id.staffid_login);
        TextInputLayout inputpass = findViewById(R.id.staffid_pass);


        PrefManager pref = new PrefManager(this);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        SharedPreferences pref2 = getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
        String sno = pref2.getString("KEY_STAFFNUMBER", null);
        inputstaffno.setText(sno);


        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Executor executor = ContextCompat.getMainExecutor(this);
                biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode,@NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
                        String sno = pref.getString("KEY_STAFFNUMBER", null);
                        EditText staffNo = findViewById(R.id.staffid_login);
                        staffNo.setText(sno);


                        Toast.makeText(getApplicationContext(), "Type in your credentials to login.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);


                        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
                        String sno = pref.getString("uname", null);
                        String sp = pref.getString("upass", null);

                        EditText staffNo = findViewById(R.id.staffid_login);
                        TextInputLayout staffP = findViewById(R.id.staffid_pass);

                        byte[] bpass2 = sp.getBytes(Charsets.ISO_8859_1);
                        byte[] bsno2 = sno.getBytes(Charsets.ISO_8859_1);

                        String filename = "my_keyset.json";
                        File dir = getApplicationContext().getFilesDir();

                        File file = new File(dir, filename);
                            if(file.exists()) {
                                try {

                                    DeterministicAeadConfig.register();

                                    KeysetHandle keysetHandle = CleartextKeysetHandle.read(
                                            JsonKeysetReader.withFile(new File(dir, filename)));

                                    Log.d("KRA:", "READING KEYFILE: " + file);

                                    Log.d("KRA:", "KEYFILE: " + keysetHandle);

                                    DeterministicAead daead = keysetHandle.getPrimitive(DeterministicAead.class);

                                    byte[] decrypted = daead.decryptDeterministically(bpass2, bsno2);

                                    String dpass = new String(decrypted, Charsets.ISO_8859_1);

                                    staffNo.setText(sno);
                                    Objects.requireNonNull(staffP.getEditText()).setText(dpass);

                                    String staffreg = inputstaffno.getText().toString().trim();
                                    String staffpass = Objects.requireNonNull(inputpass.getEditText()).getText().toString().trim();

                                    Log.d("KRA:", "DEBUG: " + daead + keysetHandle);

                                    checkstaffinfo(staffreg,staffpass);

                                } catch (GeneralSecurityException | IOException e) {


                                    e.printStackTrace();
                                }
                            }else if(!file.exists()) {

                                Log.d("KRA:", "KEYFILE DOESN'T EXIST.");

                            }
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });




                Log.d("KRA: ", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("KRA: ", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("KRA: ", "Biometric features are currently unavailable.");
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

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for KRA Staff App")
                .setSubtitle("Use your device biometrics to login")
                .setNegativeButtonText("Use your password instead")
                .build();


        if (pref.isLoggedIn()) {
            biometricPrompt.authenticate(promptInfo);

        } else if (!pref.isLoggedIn()){

            String filename = "my_keyset.json";
            File dir = getApplicationContext().getFilesDir();

            File file = new File(dir, filename);
            if(!file.exists()) {

                try {
                    DeterministicAeadConfig.register();

                    KeysetHandle keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("AES256_SIV"));

                    CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withFile(new File(dir, filename)));

                    Log.d("KRA:", "KEYFILE WRITTEN TO: " + file);

                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }else if(file.exists()){

                Log.d("KRA:", "KEYFILE ALREADY EXISTS: " + file);


            }
        }



        staff_login.setOnClickListener(view -> {

            String staffreg = inputstaffno.getText().toString().trim();
            String staffpass = Objects.requireNonNull(inputpass.getEditText()).getText().toString().trim();


            String filename = "my_keyset.json";
            File dir = getApplicationContext().getFilesDir();

            File file = new File(dir, filename);

            if(file.exists()) {
                try {

                    DeterministicAeadConfig.register();

                    KeysetHandle keysetHandle = CleartextKeysetHandle.read(
                            JsonKeysetReader.withFile(new File(dir, filename)));
                    Log.d("KRA:", "READING KEYFILE: " + file);

                    Log.d("KRA:", "KEYFILE: " + keysetHandle);

                    DeterministicAead daead = keysetHandle.getPrimitive(DeterministicAead.class);

                    byte[] ereg = staffreg.getBytes(Charsets.ISO_8859_1);
                    byte [] epass = staffpass.getBytes(Charsets.ISO_8859_1);

                    byte[] ciphertext = daead.encryptDeterministically(epass, ereg);

                    SharedPreferences ui = getSharedPreferences("UserInfo", MODE_PRIVATE);
                    SharedPreferences.Editor edUi = ui.edit();

                    edUi.putString("upass", new String(ciphertext, Charsets.ISO_8859_1));
                    edUi.putString("uname",new String(ereg, Charsets.ISO_8859_1));

                    Log.d("KRA:", "DEBUG: " + daead + keysetHandle);

                    edUi.apply();

                    checkstaffinfo(staffreg,staffpass);

                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }else if(!file.exists()){

                Log.d("KRA:", "KEYFILE DOESN'T EXIST: " + file);

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




    public void checkstaffinfo(String staffreg, String staffpass) {

        if (!staffreg.isEmpty() && !staffpass.isEmpty()) {

            pDialog.setMessage("Checking ...");
            pDialog.show();

            StringRequest strReq = new StringRequest(
                    Request.Method.POST,
                    "http://10.151.1.114/query4.php", response -> {
                Log.d("KRA:", "STAFFID-RESPONSE:" + response);

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

                    Log.d("KRA:", "Checking Staff Number: " + params.toString());

                    return params;
                }

            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq);


        } else {
            Toast.makeText(getApplicationContext(), "Please enter a valid KRA Staff Number and password.", Toast.LENGTH_LONG).show();
            pDialog.dismiss();

        }
    }

}
