package com.example.android.peakfresh;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.peakfresh.data.ProductColumns;
import com.example.android.peakfresh.data.ProductContentProvider;
import com.example.android.peakfresh.widget.WidgetAppProvider;

/**
 * Created by Warren on 9/12/2016.
 */
public class UpdateProductTask extends AsyncTask<String, Void, Void> {

    private Context mContext;
    private int count;
    Uri productUri;
    private String newValue, selection, productColumn;
    private String[] productId;

    public UpdateProductTask(Context context, String[] productId, String newValue, String productColumn){
        mContext = context;
        this.productId = productId;
        this.newValue = newValue;
        this.productColumn = productColumn;
    }

    @Override
    protected Void doInBackground(String... strings) {
        ContentValues values = new ContentValues();
        values.put(productColumn, newValue);

        selection = ProductColumns._ID + " LIKE ?";


        count = mContext.getContentResolver().update(
                ProductContentProvider.Products.PRODUCTS_URI,
                values,
                selection,
                productId
        );

        updateWidgets();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);
    }

    private void updateWidgets() {
        Intent dataUpdatedIntent = new Intent(WidgetAppProvider.WIDGET_PRODUCT_UPDATE)
                .setPackage(mContext.getPackageName());
        mContext.sendBroadcast(dataUpdatedIntent);
    }
}
