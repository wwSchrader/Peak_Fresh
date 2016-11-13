package com.wwschrader.android.peakfresh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Warren on 10/21/2016.
 */

public class BootReceiver extends BroadcastReceiver {
    private AlarmReceiver mAlarmReceiver = new AlarmReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        //to restart alarm when device is rebooted
        if (intent.getAction().equals(context.getString(R.string.boot_completed_string))) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            //check to see if notification preference was switched on
            if (prefs.getBoolean(context.getString(R.string.pref_switch_key), false)){
                mAlarmReceiver.setAlarm(context);
            }
        }
    }
}
