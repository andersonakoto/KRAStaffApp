package com.example.krastaffapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class LoginActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button staff_login = findViewById(R.id.staff_login_btn);

        staff_login.setOnClickListener(view -> {
            Intent homescreen = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(homescreen);
//            this.finish();

        });


    }
}
