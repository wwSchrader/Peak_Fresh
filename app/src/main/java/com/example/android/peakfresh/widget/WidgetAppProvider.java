package com.example.android.peakfresh.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.peakfresh.R;
import com.example.android.peakfresh.ui.Detail_Activity;
import com.example.android.peakfresh.ui.Main_Activity;

/**
 * Created by Warren on 10/25/2016.
 */

public class WidgetAppProvider extends AppWidgetProvider {

    private static final String LOG_TAG = WidgetAppProvider.class.getSimpleName();

    public static final String WIDGET_PRODUCT_UPDATE = "com.example.android.peakfresh.app.ACTION_DATA_UPDATED";
    public static final String WIDGET_PRODUCT_ADDED = "com.example.android.peakfresh.app.ACTION_DATA_ADDED";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(LOG_TAG, "onRecive triggered " + intent.getAction());
        if (WIDGET_PRODUCT_UPDATE.equals(intent.getAction())){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass())
            );
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, getClass()));
        Log.v(LOG_TAG, "onUpdate triggered");
        for (int i = 0; i < appWidgetIds.length; ++i){
            Log.v(LOG_TAG, "onUpdate triggered");

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_collection);

            //set click action on title bar of widge goes to main activity
            Intent intent = new Intent(context, Main_Activity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(context, views);
            }

            //set product click goes to detail screen
            Intent productClickIntent = new Intent(context, Detail_Activity.class);
            PendingIntent pendingClickIntent = PendingIntent
                    .getActivity(context, 0, productClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list, pendingClickIntent);
            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            appWidgetManager.updateAppWidget(appWidgetIds, views);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, WidgetRemoteViewService.class));
    }

    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, WidgetRemoteViewService.class));
    }
}
