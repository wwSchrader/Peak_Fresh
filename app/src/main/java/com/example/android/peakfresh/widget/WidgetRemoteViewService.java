package com.example.android.peakfresh.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.example.android.peakfresh.R;
import com.example.android.peakfresh.data.ProductColumns;
import com.example.android.peakfresh.ui.Detail_Fragment;

import java.util.concurrent.ExecutionException;

/**
 * Created by Warren on 10/25/2016.
 */

public class WidgetRemoteViewService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private int mCount;
    private Context mContext;
    private int mAppWidgetId;
    private Cursor mCursor;

    public ListRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null){
            mCursor.close();
        }
        final long identityToken = Binder.clearCallingIdentity();
        mCursor = mContext.getContentResolver().query(
                com.example.android.peakfresh.data.ProductContentProvider.Products.PRODUCTS_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onDestroy() {
        if (mCursor != null){
            mCursor.close();
        }

    }

    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION || mCursor == null || !mCursor.moveToPosition(position)){
            return null;
        }

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

        String widgetProductName = mCursor.getString(mCursor.getColumnIndex(ProductColumns.PRODUCT_NAME));
        String widgetExpirationDate = mCursor.getString(mCursor.getColumnIndex(ProductColumns.PRODUCT_EXPIRATION_DATE));

        remoteViews.setTextViewText(R.id.widget_product_name, widgetProductName);
        remoteViews.setTextViewText(R.id.widget_expiration_date, widgetExpirationDate);

        try {
            Bitmap productBitMap = Glide
                    .with(mContext)
                    .load(mCursor.getString(mCursor.getColumnIndex(ProductColumns.PRODUCT_ICON)))
                    .asBitmap()
                    .into(100, 100)
                    .get();

            remoteViews.setImageViewBitmap(R.id.widget_product_image, productBitMap);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //set intent to go to detail screen of selected product
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(Detail_Fragment.PRODUCT_ID_KEY, mCursor.getInt(mCursor.getColumnIndex(ProductColumns._ID)));
        remoteViews.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (mCursor.moveToPosition(position))
            return mCursor.getLong(mCursor.getColumnIndex(ProductColumns._ID));
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
