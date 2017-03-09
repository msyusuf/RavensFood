package com.everykindred.ravensfood;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Arrays;

public class RavenFirebaseMessagingService extends FirebaseMessagingService {
    public RavenFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String body = remoteMessage.getNotification().getBody();
        String title = remoteMessage.getNotification().getTitle();

        //saveToPrefs(title, body);
        createNotification(body);
    }

    private void saveToPrefs(String title, String body) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        SharedPreferences.Editor editor = preferences.edit();
//
//        String blogs = preferences.getString("blogs", "");
//
//        if (blogs == "") {
//
//        } else {
//            String blogString = blogs + ']' + "body";
//            String array[] = blogString.split("]");
//            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(array));
//            Log.i("tag", "notempty: " + blogs);
//        }
//
//        // Save to SharedPreferences
//        editor.putString("blogs", );
//        editor.apply();
    }

    private void createNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_rf)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentIntent(resultIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotificationBuilder.build());
    }
}
