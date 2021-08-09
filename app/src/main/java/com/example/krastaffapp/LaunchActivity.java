package com.example.krastaffapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import com.example.krastaffapp.registration.StaffidActivity;

public class LaunchActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Button launch_reg = findViewById(R.id.launch_register);

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
