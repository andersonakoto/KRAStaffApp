package com.example.krastaffapp.checkinout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.krastaffapp.CheckoutActivity;
import com.example.krastaffapp.CheckinActivity;
import com.example.krastaffapp.R;

public class CheckinoutActivity extends Activity {


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        Button laptopman = findViewById(R.id.laptop_man);

        laptopman.setOnClickListener(view -> {
            Intent intent = new Intent(CheckinoutActivity.this, LaptopManActivity.class);
            startActivity(intent);
//            this.finish();

        });

        Button laptopqr = findViewById(R.id.laptop_qr);

        laptopqr.setOnClickListener(view -> {
            Intent intent2 = new Intent(CheckinoutActivity.this, LaptopQRActivity.class);
            startActivity(intent2);
//            this.finish();

        });

        Button checkinindv = findViewById(R.id.checkin_indv);

        checkinindv.setOnClickListener(view -> {
            Intent intent2 = new Intent(CheckinoutActivity.this, CheckinActivity.class);
            startActivity(intent2);
//            this.finish();

        });

        Button checkoutindv = findViewById(R.id.checkout_indv);

        checkoutindv.setOnClickListener(view -> {
            Intent intent2 = new Intent(CheckinoutActivity.this, CheckoutActivity.class);
            startActivity(intent2);
//            this.finish();

        });




    }
}
