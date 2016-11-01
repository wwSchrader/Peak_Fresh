package com.wwschrader.android.peakfresh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Warren on 10/21/2016.
 */

public class BootReceiver extends BroadcastReceiver {
    private AlarmReceiver mAlarmReceiver = new AlarmReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        //to restart alarm when device is rebooted
        if (intent.getAction().equals(context.getString(R.string.boot_completed_string)))
        {
            mAlarmReceiver.setAlarm(context);
        }
    }
}
