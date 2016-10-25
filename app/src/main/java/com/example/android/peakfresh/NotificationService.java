package com.example.android.peakfresh;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.android.peakfresh.data.ProductColumns;
import com.example.android.peakfresh.data.ProductContentProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Warren on 10/21/2016.
 */

public class NotificationService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    android.support.v4.app.NotificationCompat.Builder mBuilder;

    Cursor queryCursor;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String notificationDaysString = sharedPreferences.getString("pref_key_days_selected", "0");

        int notificationDays = Integer.parseInt(notificationDaysString);

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.set(Calendar.HOUR_OF_DAY, 0);
        expirationDate.set(Calendar.MINUTE, 0);
        expirationDate.set(Calendar.SECOND, 0);
        expirationDate.set(Calendar.MILLISECOND, 0);

        expirationDate.add(Calendar.DAY_OF_YEAR, notificationDays);

        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");

        String notificationDate = dateFormat.format(expirationDate.getTime());

        Log.v("Notification Day", notificationDate);

        queryCursor = getContentResolver().query(
                ProductContentProvider.Products.PRODUCTS_URI,
                null,
                ProductColumns.PRODUCT_EXPIRATION_DATE + "=?",
                new String[] {notificationDate},
                null
        );

        if (queryCursor != null && queryCursor.moveToFirst()) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //go through entire cursor
            int numMessage = 1;
            int notifyId = 1;

            while (queryCursor.moveToNext()){
                numMessage++;
            }

            mBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("Expiring Product")
                    .setContentText("You have " + numMessage + " products expiring on ")
                    .setSmallIcon(R.mipmap.ic_launcher);

            mNotificationManager.notify(notifyId, mBuilder.build());
        }





        Log.d("Notification service", "Notification triggered!");

        AlarmReceiver.completeWakefulIntent(intent);
    }
}
