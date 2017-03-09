package com.everykindred.ravensfood;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.view.SoundEffectConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class FetchIntentService extends IntentService {
    private static String BLOG_LENGTH = "blog_length";
    private static String BLOGS_NEW= "blogs_new";
    private static String BOOKS_LENGTH = "books_length";
    private static String BOOKS_NEW= "books_new";

    public FetchIntentService() {
        super("FetchIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        createNotification("test");
        //checkNewBlogs();
        checkNewBooks();
    }

    private void checkNewBooks() {


    }

    private void checkNewBlogs() {
        String jsonurl = getString(R.string.urlBlogsNewest);
        String message = getString(R.string.newBlogsMessage);
        checkNewItems(jsonurl, message, BLOG_LENGTH, BLOGS_NEW);
    }

    private void checkNewItems(String jsonurl, String message, String prefKey, String prefSetKey) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String jsonString = getjson(jsonurl);
        JSONArray jsonArray = parseJson(jsonString);

        int curJsonLength = jsonArray.length();
        int lastJsonLength = sharedPreferences.getInt(prefKey, curJsonLength);
        int newItemCount = curJsonLength - lastJsonLength;

        Set<String> prefSet = sharedPreferences.getStringSet(prefSetKey, null);
        ArrayList<String> newItems = new ArrayList<>();
        if (prefSet != null) {
            newItems = new ArrayList<>(prefSet);
        }

        if (newItemCount > 0) {
            createNotification(message);

            //for each new item add title to array to add to prefs
            for (int i = 0; i < newItemCount; i++) {
                JSONObject json = null;
                try {
                    json = jsonArray.getJSONObject(i);
                    String title = json.getString("title");
                    newItems.add(title);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //add new items to string set for prefs
        HashSet<String> stringSet = new HashSet<>();
        stringSet.addAll(newItems);

        //save current length and new blogs to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(prefKey, curJsonLength);
        editor.putStringSet(prefSetKey, stringSet);
        editor.apply();
    }

    private String getjson(String json_url) {

        try {
            URL url = new URL(json_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();
            String json_string;
            while ((json_string = bufferedReader.readLine()) != null) {
                stringBuilder.append(json_string);
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return stringBuilder.toString().trim();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray parseJson(String jsonString) {
        try {

            JSONArray jsonArray = new JSONArray(jsonString);

            return jsonArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
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
                //.setSound()
                .setContentIntent(resultIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotificationBuilder.build());
    }

//    private void saveToPrefs() {
//        //get the previous jsonarray count from prefs
//        int curJsonLength = jsonArray.length();
//        int lastJsonLength = preferences.getInt("blog_length", curJsonLength);
//        int newItemCount = curJsonLength - lastJsonLength;
//
//        Set<String> prefSet = preferences.getStringSet(BLOGS_NEW, null);
//        newItems = new ArrayList<>();
//        if (prefSet != null) {
//            newItems = new ArrayList<>(prefSet);
//        }
//
//        //for each new item add title to array to add to prefs
//        for (int i = 0; i < newItemCount; i++) {
//            String title = blogs.get(i).get("title");
//            newItems.add(title);
//        }
//
//        //add new items to string set for prefs
//        HashSet<String> stringSet = new HashSet<>();
//        stringSet.addAll(newItems);
//
//        //save current length and new blogs to SharedPreferences
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putInt("blog_length", curJsonLength);
//        editor.putStringSet(BLOGS_NEW, stringSet);
//        editor.apply();
//
//        //show the new item count as badge on blog drawer
//        setBadge(newItems.size());
//    }
}
