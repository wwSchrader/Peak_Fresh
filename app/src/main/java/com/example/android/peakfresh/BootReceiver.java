package com.example.android.peakfresh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Warren on 10/21/2016.
 */

public class BootReceiver extends BroadcastReceiver {
    AlarmReceiver mAlarmReceiver = new AlarmReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        //to restart alarm when device is rebooted
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            mAlarmReceiver.setAlarm(context);
        }
    }
}
