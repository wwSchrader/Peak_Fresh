package com.example.android.peakfresh;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.RemoteException;

import com.example.android.peakfresh.data.ProductColumns;
import com.example.android.peakfresh.data.ProductContentProvider;

import java.util.ArrayList;

/**
 * Created by Warren on 8/31/2016.
 */
public class InsertProductTask extends AsyncTask<String, Void, Void> {
    Context mContext;
    Cursor initQueryCursor;
    private String[] mColumns = {"_id"};

    public InsertProductTask(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(String...param) {
//        if (param[0].equals("init")){
            initQueryCursor = mContext.getContentResolver().query(
                    ProductContentProvider.Products.PRODUCTS_URI,
                    mColumns,
                    null,
                    null,
                    null);

            assert initQueryCursor != null;
            if (!initQueryCursor.moveToFirst()){
                //if db is empty, add sample product
                ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
                ContentProviderOperation.Builder builder = ContentProviderOperation
                        .newInsert(ProductContentProvider.Products.PRODUCTS_URI);

                builder.withValue(ProductColumns.PRODUCT_NAME, "Tomatoes");
                builder.withValue(ProductColumns.PRODUCT_CATEGORY, "Produce");
                builder.withValue(ProductColumns.PRODUCT_EXPIRATION_DATE, "10/13/16");
                batchOperations.add(builder.build());

                builder.withValue(ProductColumns.PRODUCT_NAME, "Milk");
                builder.withValue(ProductColumns.PRODUCT_CATEGORY, "Dairy");
                builder.withValue(ProductColumns.PRODUCT_EXPIRATION_DATE, "9/16/16");
                batchOperations.add(builder.build());

                try {
                    mContext.getContentResolver().applyBatch(ProductContentProvider.AUTHORITY, batchOperations);
                } catch (RemoteException | OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
            initQueryCursor.close();
//        }
        return null;
    }
}
