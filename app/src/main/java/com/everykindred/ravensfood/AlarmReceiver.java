package com.everykindred.ravensfood;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        //Intent dailyUpdater = new Intent(context, FetchIntentService.class);
        //context.startService(dailyUpdater);
    }
}
