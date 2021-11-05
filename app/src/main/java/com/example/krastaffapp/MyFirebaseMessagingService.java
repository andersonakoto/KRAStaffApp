package com.example.krastaffapp;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.krastaffapp.ui.notifications.NotificationsDB;
import com.example.krastaffapp.ui.notifications.NotificationsList;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {


    public static NotificationsDB notificationsDB;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("KRA:", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("KRA:", "Message data payload: " + remoteMessage.getData());

            notificationsDB = Room.databaseBuilder(getApplicationContext(),NotificationsDB.class,"notificationsDB")            .fallbackToDestructiveMigration()
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();

            String nId = remoteMessage.getData().get("tag");
            String nTitle = remoteMessage.getData().get("title");
            String nBody = remoteMessage.getData().get("msg");
            String nTime = remoteMessage.getData().get("time");

            String unread = "unread";

            NotificationsList notificationsList =new NotificationsList();
            assert nId != null;
            notificationsList.setId(nId);
            notificationsList.setNtitle(nTitle);
            notificationsList.setNmessage(nBody);
            notificationsList.setNtime(nTime);
            notificationsList.setNread(unread);


            Intent intent2 = new Intent("custom-listener");
            broadcaster.sendBroadcast(intent2);

            MyFirebaseMessagingService.notificationsDB.notificationsDAO().addData(notificationsList);

            Log.d("KRA:", "Message saved to DB: " + "\n" + nId + "\n" + nTitle + "\n" + nBody + "\n" + nTime);

            scheduleJob();

        }


        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Context context = getApplicationContext();


        String channelId = "krastaffapp-01";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.kra_icon)
                        .setContentText(remoteMessage.getData().get("msg"))
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setAutoCancel(false)
                        .setSound(defaultSoundUri)
                        .setChannelId(channelId)
                        .setVibrate(new long[]{0, 400, 100, 400})
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setSortKey("e")
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel =
                    new NotificationChannel(channelId, "KRAStaffApp", NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            notificationChannel.setDescription("Staff Notifications");
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 400, 100, 400});
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);


        }

        assert notificationManager != null;
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


    }


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("KRA: ", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d("KRA: ", "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }


    private void sendNotification(String messageBody, String title) {
    }
}