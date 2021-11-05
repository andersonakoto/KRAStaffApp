package com.example.krastaffapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.krastaffapp.helper.PrefManager;
import com.example.krastaffapp.registration.StaffidActivity;

import java.util.Objects;

public class LaunchActivity extends AppCompatActivity {



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();

        PrefManager pref = new PrefManager(this);

        if (pref.isLoggedIn()) {
            Intent intent2 = new Intent(LaunchActivity.this, LoginActivity.class);
            startActivity(intent2);

        } else {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                checkPermission();
            }

            setContentView(R.layout.activity_launcher);


            Button launch_reg = findViewById(R.id.launch_register);


            AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(this);

            // Inside Main Activity
            Log.d("KRA:", "APP-HASH-KEY: " + appSignatureHashHelper.getAppSignatures().get(0));
            // Inside  log cat Apps Hash Key: qzwS5M4KQ5H

            launch_reg.setOnClickListener(view -> {
                Intent intent1 = new Intent(LaunchActivity.this, StaffidActivity.class);
                startActivity(intent1);
//            this.finish();

            });

            Button launch_log = findViewById(R.id.launch_login);

            launch_log.setOnClickListener(view -> {
                Intent intent2 = new Intent(LaunchActivity.this, LoginActivity.class);
                startActivity(intent2);
//            this.finish();

            });


            Button call_Assist = findViewById(R.id.call_assist);

            call_Assist.setOnClickListener(view -> {
                //Opens phone dialler with number already set in the dialler, user just has to initiate the call
                Intent callKenya = new Intent(Intent.ACTION_DIAL);
                callKenya.setData(Uri.parse("tel:+254711099999"));
                startActivity(callKenya);
            });


        }
    }

    protected void checkPermission(){
        if(ContextCompat.checkSelfPermission
                (LaunchActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)

                + ContextCompat.checkSelfPermission
                (LaunchActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)


                != PackageManager.PERMISSION_GRANTED){

            // Do something, when permissions not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    LaunchActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    LaunchActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ){
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(LaunchActivity.this);
                builder.setMessage("Storage permissions are required for the app to work properly.");
                builder.setTitle("Please grant required permissions:");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                LaunchActivity.this,
                                new String[]{
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },
                                88
                        );
                    }
                });
                builder.setNeutralButton("Cancel",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        LaunchActivity.this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        88
                );
            }
        }else {
            // Do something, when permissions are already granted
            Toast.makeText(getApplicationContext(),"Permissions already granted",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 88) {// When request is cancelled, the results array are empty
            if (
                    (grantResults.length > 0) &&
                            (grantResults[0]
                                    + grantResults[1]
                                    == PackageManager.PERMISSION_GRANTED
                            )
            ) {
                // Permissions are granted
                Toast.makeText(getApplicationContext(), "Permissions granted.", Toast.LENGTH_SHORT).show();
            } else {
                // Permissions are denied
                Toast.makeText(getApplicationContext(), "Permissions denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
