package com.example.krastaffapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


public class CallerShortcut extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:+254711099999"));
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){

        Intent a = new Intent(this, LaunchActivity.class);

        startActivity(a);
        finish();

    }
}