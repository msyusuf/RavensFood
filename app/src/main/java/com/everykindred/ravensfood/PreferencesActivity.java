package com.everykindred.ravensfood;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class PreferencesActivity extends AppCompatActivity {
    public static final String PREFERENCE_FILENAME = "AppPrefs";
    private static final String DEVICE_TOKEN = "firebase_registration_id";
    private static final String PLAY_NEXT = "play_next_pref";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(android.R.id.content, new PrefsFragment());
        transaction.commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class PrefsFragment extends PreferenceFragment {
        private int tapCount = 0;
        private String token;

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

            switch (preference.getKey()) {
                case PLAY_NEXT: {
                    tapCount ++;

                    if (tapCount >= 4) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Device Token")
                                .setMessage("Token is: " +token)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();

                        Log.i("TOKEN","Token: " + token);
                        tapCount = 0;
                    }
                }
                break;

            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate( savedInstanceState);
            PreferenceManager manager = getPreferenceManager();
            manager.setSharedPreferencesName(PREFERENCE_FILENAME);
            addPreferencesFromResource(R.xml.preferences);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            token = preferences.getString(DEVICE_TOKEN, null);
        }

    }
}

