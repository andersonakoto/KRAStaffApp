package com.example.krastaffapp.registration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.krastaffapp.R;

public class OtpActivity extends Activity{

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        Button otp_reg = findViewById(R.id.next_otp);

        otp_reg.setOnClickListener(view -> {
            Intent passcreen = new Intent(OtpActivity.this, SetPassActivity.class);
            startActivity(passcreen);
//            this.finish();

        });


    }
}
