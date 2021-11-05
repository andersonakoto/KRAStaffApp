package com.example.krastaffapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.krastaffapp.databinding.ActivityMainBinding;
import com.example.krastaffapp.helper.AppController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = "01";
            String channelName = "01";
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("Key:Value", "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]
        com.example.krastaffapp.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_apps, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("KRA:FCM::", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String fcmToken = task.getResult();

                        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
                        String staffPhone = pref.getString("KEY_MOBILE", null);


                        updateFCMtoken(fcmToken, staffPhone);

                        SharedPreferences ui = getSharedPreferences("UserInfo", MODE_PRIVATE);
                        SharedPreferences.Editor edUi = ui.edit();

                        edUi.putString("fcmToken", fcmToken);

                        edUi.apply();


                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, fcmToken);
                        Log.d("KRA:FCM:Message::", msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);


    }

    private static long sayBackPress;

    @Override
    public void onBackPressed() {

        if (sayBackPress + 2000 > System.currentTimeMillis()) {
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
        } else {
            Toast.makeText(MainActivity.this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
            sayBackPress = System.currentTimeMillis();

        }

    }

    public void updateFCMtoken(String fcmToken, String staffPhone) {

        if (!fcmToken.isEmpty()) {

            StringRequest strReq = new StringRequest(
                    Request.Method.POST,
                    "http://10.151.1.114/query5.php", response -> {
                Log.d("KRA:", "FCMToken-RESPONSE:" + response);

                try {
                    JSONObject responseObj = new JSONObject(response);

                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    if (!error) {

//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        Log.d("KRA:", "FCM TOKEN UPDATED: " + fcmToken +"\n "+ staffPhone);


                    } else {
                        Log.e("KRA:", "FCM TOKEN UPDATED ERROR: " + message);

                    }


                } catch (JSONException e) {
                    Log.e("KRA:", "FCM TOKEN UPDATE Error: " + e.getMessage());
                }

            }, error -> {
                Log.e("KRA:", "FCM TOKEN UPDATE Error: " + error.getMessage());

            }) {

                /**
                 * Passing user parameters to the SQl server
                 */
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("staffAppDevID", fcmToken);
                    params.put("staffPhone", staffPhone);

                    Log.d("KRA:", "FCM PARAM: " + params.toString());

                    return params;
                }

            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq);


        } else {
            Log.d("KRA:", "FCM TOKEN EMPTY: " + fcmToken);

        }
    }

}