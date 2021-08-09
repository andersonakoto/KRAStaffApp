package com.example.krastaffapp.registration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.krastaffapp.R;

public class StaffidActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staffid);

        Button staff_reg = findViewById(R.id.next_register);

        staff_reg.setOnClickListener(view -> {
            Intent otpscreen = new Intent(StaffidActivity.this, OtpActivity.class);
            startActivity(otpscreen);
//            this.finish();

        });


    }
}
