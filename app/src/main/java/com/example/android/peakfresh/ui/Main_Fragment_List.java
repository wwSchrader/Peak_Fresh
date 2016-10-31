package com.example.android.peakfresh.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.peakfresh.InsertProductTask;
import com.example.android.peakfresh.ProductCursorAdapter;
import com.example.android.peakfresh.R;
import com.example.android.peakfresh.RecyclerViewItemClickListener;
import com.example.android.peakfresh.data.ProductColumns;
import com.example.android.peakfresh.data.ProductContentProvider;
import com.example.android.peakfresh.touch_helper.ProductTouchHelperCallback;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;


public class Main_Fragment_List extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String TAG = Main_Fragment_List.class.getSimpleName();
    public static final String INIT_DATABASE_KEY = "init";


    protected RecyclerView mRecyclerView;
    ArrayList<String> list = new ArrayList<>();
    private String mCategory;
    private static final int CURSOR_LOADER_ID = 0;
    private ProductCursorAdapter mCursorAdapter;
    private TextView emptyTextViewMessage;
    private Context mContext;
    private ItemTouchHelper mItemTouchHelper;
    private Cursor mCursor;
    private boolean mIsLargeLayout;
    private int numberOfColumns = 2;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsLargeLayout = getResources().getBoolean(R.bool.large_layout);
        mCategory  = getString(R.string.category_all);
        if (savedInstanceState == null){
            //initialize database & add samples if there are none.
            InsertProductTask insertProductTask = new InsertProductTask(getActivity(), INIT_DATABASE_KEY);
            insertProductTask.execute();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mContext = getContext();

        getActivity().setContentView(R.layout.fragment_main__fragment__list);

        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.main_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);



        mCursorAdapter = new ProductCursorAdapter(mContext, null);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getContext(),
                new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Cursor c = mCursorAdapter.getCursor();
                        c.moveToPosition(position);
                        int productId = c.getInt(c.getColumnIndex(ProductColumns._ID));
                        Intent intent = new Intent(getContext(), Detail_Activity.class);
                        intent.putExtra(Detail_Fragment.PRODUCT_ID_KEY, productId);
                        intent.putExtra(Detail_Fragment.IMAGE_POSITION, position);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            //setup shared element transition
                            View imageTransitionView = v.findViewById(R.id.product_icon);
                            String transitionName = imageTransitionView.getTransitionName();
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), imageTransitionView, transitionName);
                            startActivity(intent, options.toBundle());
                        } else {
                            startActivity(intent);
                        }


                    }
                }));
        mRecyclerView.setAdapter(mCursorAdapter);

        ItemTouchHelper.Callback callback = new ProductTouchHelperCallback(mCursorAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        emptyTextViewMessage= (TextView) getActivity().findViewById(R.id.empty_view_text);

        //load banner ad
        AdView mAdView = (AdView) getActivity().findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, NewProduct_Activity.class);
                startActivity(intent);
            }
        });

        //inflate layout for fragment
        return inflater.inflate(R.layout.fragment_main__fragment__list, container, false);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mCategory.equals(getString(R.string.category_all))){
            //if category is "all" use null values for selection and selctionArgs
            return new CursorLoader(mContext, ProductContentProvider.Products.PRODUCTS_URI,
                    new String[]{ProductColumns._ID, ProductColumns.PRODUCT_NAME, ProductColumns.PRODUCT_EXPIRATION_DATE,
                            ProductColumns.PRODUCT_CATEGORY, ProductColumns.PRODUCT_ICON},
                    null,
                    null,
                    null);
        } else {
            return new CursorLoader(mContext, ProductContentProvider.Products.PRODUCTS_URI,
                    new String[]{ProductColumns._ID, ProductColumns.PRODUCT_NAME, ProductColumns.PRODUCT_EXPIRATION_DATE,
                            ProductColumns.PRODUCT_CATEGORY, ProductColumns.PRODUCT_ICON},
                    ProductColumns.PRODUCT_CATEGORY + "=?",
                    new String[] {mCategory},
                    null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        mCursor = data;
        updateEmptyRecyclerView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        //switches
        if (s.equals(Main_Activity.CATEGORY_SHARED_PREF_KEY)){
            mCategory = sharedPreferences.getString(Main_Activity.CATEGORY_SHARED_PREF_KEY, "All");
            getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
        }
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        sp.registerOnSharedPreferenceChangeListener(this);

        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        sp.unregisterOnSharedPreferenceChangeListener(this);

        super.onPause();
    }

    //message to display of recyclerview is empty
    public void updateEmptyRecyclerView(){
        //check to see if recyclerview is empty
        if (mCursorAdapter.getItemCount() == 0){
            String message;
            mRecyclerView.setVisibility(View.GONE);
            emptyTextViewMessage.setVisibility(View.VISIBLE);
            //sets empty view message depending on category selected
            if (mCategory.equals(getString(R.string.category_all))){
                message = getString(R.string.empty_database_message);
            } else {
                message = getString(R.string.empty_category_message);
            }

            emptyTextViewMessage.setText(message);
        } else {
            //sets recylerview back to visible
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyTextViewMessage.setVisibility(View.GONE);
        }
    }

}
