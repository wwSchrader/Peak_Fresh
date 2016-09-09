package com.example.android.peakfresh.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.peakfresh.R;
import com.example.android.peakfresh.data.ProductColumns;
import com.example.android.peakfresh.data.ProductContentProvider;

/**
 * Created by Warren on 9/8/2016.
 */
public class Detail_Fragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String[] DETAIL_COLUMNS = {ProductColumns.PRODUCT_NAME,
            ProductColumns.PRODUCT_ICON, ProductColumns.PRODUCT_EXPIRATION_DATE,
            ProductColumns.PRODUCT_EXPIRATION_DATE};
    private Detail_Fragment mContext;
    private int mProduct_Id;
    ImageView mImageView;
    TextView mProduct_title, mExpirationSummary, mExpirationDate;
    Button mCalendarButton;
    Spinner mCategorySpinner;
    private static String[] mProduct_ID_Array;
    private final static int LOADER_ID = 1;
    public final static String PRODUCT_ID_KEY = "Product_Id";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mProduct_Id = arguments.getInt(PRODUCT_ID_KEY);
            mProduct_ID_Array = new String[]{Integer.toString(mProduct_Id)};
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mImageView = (ImageView) rootView.findViewById(R.id.product_icon_detail);
        mProduct_title = (TextView) rootView.findViewById(R.id.product_title_detail);
        mExpirationDate = (TextView) rootView.findViewById(R.id.product_expiration_date_detail);
        mExpirationSummary = (TextView) rootView.findViewById(R.id.expiration_summary);
        mCalendarButton = (Button) rootView.findViewById(R.id.add_to_calendar_button);
//        mCategorySpinner = (Spinner) rootView.findViewById(R.id.category_spinner);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mProduct_Id != 0) {
            return new CursorLoader(
                    getActivity(),
                    ProductContentProvider.Products.PRODUCTS_URI,
                    DETAIL_COLUMNS,
                    ProductColumns._ID + " LIKE ?",
                    mProduct_ID_Array,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            mProduct_title.setText(data.getString(data.getColumnIndex(ProductColumns.PRODUCT_NAME)));
            mExpirationDate.setText(data.getString(data.getColumnIndex(ProductColumns.PRODUCT_EXPIRATION_DATE)));

            Log.v("onLoadFinished", data.getString(data.getColumnIndex(ProductColumns.PRODUCT_NAME)) + data.getString(data.getColumnIndex(ProductColumns.PRODUCT_EXPIRATION_DATE)));

            Glide.with(getContext())
                    .load(data.getInt(data.getColumnIndex(ProductColumns.PRODUCT_ICON)))
                    .placeholder(R.mipmap.ic_launcher)
                    .fitCenter()
                    .into(mImageView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
