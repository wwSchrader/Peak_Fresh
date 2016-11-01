package com.example.android.peakfresh;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;

import com.example.android.peakfresh.data.ProductColumns;
import com.example.android.peakfresh.data.ProductContentProvider;
import com.example.android.peakfresh.ui.Main_Activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Warren on 10/21/2016.
 */

public class NotificationService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private android.support.v4.app.NotificationCompat.Builder mBuilder;

    private Cursor queryCursor;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String notificationDaysString = sharedPreferences.getString(getString(R.string.pref_key_days_selected), "0");

        int notificationDays = Integer.parseInt(notificationDaysString);

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.set(Calendar.HOUR_OF_DAY, 0);
        expirationDate.set(Calendar.MINUTE, 0);
        expirationDate.set(Calendar.SECOND, 0);
        expirationDate.set(Calendar.MILLISECOND, 0);

        //roll forward caledar date by user selected notification days
        expirationDate.add(Calendar.DAY_OF_YEAR, notificationDays);

        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");

        String notificationDate = dateFormat.format(expirationDate.getTime());

        //return results for selected notificaiton date
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
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText("You have " + numMessage + " products expiring on ")
                    .setSmallIcon(R.mipmap.ic_launcher);

            //intent and pending intent to launch main activity when notification is selected
            Intent resultIntent = new Intent(this, Main_Activity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1 , resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            mNotificationManager.notify(notifyId, mBuilder.build());
        }

        AlarmReceiver.completeWakefulIntent(intent);
    }
}
