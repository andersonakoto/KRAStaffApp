package com.example.krastaffapp.registration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.krastaffapp.LoginActivity;
import com.example.krastaffapp.R;

public class SetPassActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pass);

        Button setpass_reg = findViewById(R.id.next_set_pass);

        setpass_reg.setOnClickListener(view -> {
            Intent loginscreen = new Intent(SetPassActivity.this, LoginActivity.class);
            startActivity(loginscreen);
//            this.finish();

        });


    }
}
