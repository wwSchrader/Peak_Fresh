package com.example.android.peakfresh;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.peakfresh.data.ProductColumns;
import com.example.android.peakfresh.data.ProductContentProvider;

/**
 * Created by Warren on 9/12/2016.
 */
public class UpdateProductTask extends AsyncTask<String, Void, Void> {

    Context mContext;
    int count;
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
        return null;
    }
}
