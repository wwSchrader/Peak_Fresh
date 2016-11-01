package com.wwschrader.android.peakfresh;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.widget.Toast;

import com.wwschrader.android.peakfresh.data.ProductColumns;
import com.wwschrader.android.peakfresh.data.ProductContentProvider;
import com.wwschrader.android.peakfresh.ui.Main_Fragment_List;
import com.wwschrader.android.peakfresh.ui.NewProduct_Activity;
import com.wwschrader.android.peakfresh.widget.WidgetAppProvider;

import java.util.ArrayList;

/**
 * Created by Warren on 8/31/2016.
 */
public class InsertProductTask extends AsyncTask<String, Void, Void> {
    private Context mContext;
    private Activity mActivity;
    private Cursor initQueryCursor;
    private String[] mColumns = {"_id"};
    private String addKey;

    public InsertProductTask(Activity activity, String key){
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
        addKey = key;
    }

    @Override
    protected Void doInBackground(String...param) {

        //if app is run for first time, add sample products
        initQueryCursor = mContext.getContentResolver().query(
                ProductContentProvider.Products.PRODUCTS_URI,
                mColumns,
                null,
                null,
                null);

        assert initQueryCursor != null;
        if (!initQueryCursor.moveToFirst() && addKey.equals(Main_Fragment_List.INIT_DATABASE_KEY)){
            //if db is empty, add sample product
            ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
            ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newInsert(ProductContentProvider.Products.PRODUCTS_URI);

            builder.withValue(ProductColumns.PRODUCT_NAME, mContext.getString(R.string.sample_1_name));
            builder.withValue(ProductColumns.PRODUCT_CATEGORY, mContext.getString(R.string.sampe_1_category));
            Utility.addItemToCategoryArray(Utility.CATEGORY_ARRAY, mContext, mContext.getString(R.string.sampe_1_category));
            builder.withValue(ProductColumns.PRODUCT_EXPIRATION_DATE, mContext.getString(R.string.sample_1_expiration_date));
            builder.withValue(ProductColumns.PRODUCT_ICON,
                    Utility.resourceToUri(mContext, R.drawable.broccoli).toString());
            batchOperations.add(builder.build());

            builder = ContentProviderOperation
                    .newInsert(ProductContentProvider.Products.PRODUCTS_URI);
            builder.withValue(ProductColumns.PRODUCT_NAME, mContext.getString(R.string.sample_2_name));
            builder.withValue(ProductColumns.PRODUCT_CATEGORY, mContext.getString(R.string.sample_2_category));
            Utility.addItemToCategoryArray(Utility.CATEGORY_ARRAY, mContext,mContext.getString(R.string.sample_2_category));
            builder.withValue(ProductColumns.PRODUCT_EXPIRATION_DATE, mContext.getString(R.string.sample_2_expiration_date));
            builder.withValue(ProductColumns.PRODUCT_ICON,
                    Utility.resourceToUri(mContext, R.drawable.milk).toString());
            batchOperations.add(builder.build());

            try {
                mContext.getContentResolver().applyBatch(ProductContentProvider.AUTHORITY, batchOperations);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
        } else if (addKey.equals(NewProduct_Activity.ADD_PRODUCT_KEY)){
            //add product to db
            ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
            ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newInsert(ProductContentProvider.Products.PRODUCTS_URI);
            builder.withValue(ProductColumns.PRODUCT_NAME, param[0]);
            builder.withValue(ProductColumns.PRODUCT_CATEGORY, param[1]);
            builder.withValue(ProductColumns.PRODUCT_EXPIRATION_DATE, param[2]);
            builder.withValue(ProductColumns.PRODUCT_ICON, param[3]);
            batchOperations.add(builder.build());

            try {
                mContext.getContentResolver().applyBatch(ProductContentProvider.AUTHORITY, batchOperations);

            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
        }
        initQueryCursor.close();

        updateWidgets();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (addKey.equals(NewProduct_Activity.ADD_PRODUCT_KEY)){
            //display toast confirming that product was added then finish the activity
            Toast.makeText(mContext, R.string.product_added_toast, Toast.LENGTH_SHORT).show();
            mActivity.finish();
        }
    }

    private void updateWidgets() {

        Intent dataUpdatedIntent = new Intent(WidgetAppProvider.WIDGET_PRODUCT_UPDATE)
                .setPackage(mContext.getPackageName());
        mContext.sendBroadcast(dataUpdatedIntent);
    }
}
