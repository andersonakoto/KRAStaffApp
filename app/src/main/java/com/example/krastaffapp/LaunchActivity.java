package com.example.krastaffapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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
}
