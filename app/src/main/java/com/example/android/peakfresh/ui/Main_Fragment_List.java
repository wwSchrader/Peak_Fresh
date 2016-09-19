package com.example.android.peakfresh.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.peakfresh.InsertProductTask;
import com.example.android.peakfresh.ProductCursorAdapter;
import com.example.android.peakfresh.R;
import com.example.android.peakfresh.RecyclerViewItemClickListener;
import com.example.android.peakfresh.data.ProductColumns;
import com.example.android.peakfresh.data.ProductContentProvider;
import com.example.android.peakfresh.touch_helper.ProductTouchHelperCallback;

import java.util.ArrayList;


public class Main_Fragment_List extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String TAG = Main_Fragment_List.class.getSimpleName();

    protected RecyclerView mRecyclerView;
    ArrayList<String> list = new ArrayList<>();
    private String mCategory = "all";
    private static final int CURSOR_LOADER_ID = 0;
    private ProductCursorAdapter mCursorAdapter;
    private Context mContext;
    private ItemTouchHelper mItemTouchHelper;
    private Cursor mCursor;
    private boolean mIsLargeLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsLargeLayout = getResources().getBoolean(R.bool.large_layout);
        //initialize database & add samples if there are none.
        InsertProductTask insertProductTask = new InsertProductTask(getContext());
        insertProductTask.execute();
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
                        startActivity(intent);
                    }
                }));
        mRecyclerView.setAdapter(mCursorAdapter);

        ItemTouchHelper.Callback callback = new ProductTouchHelperCallback(mCursorAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                CreateNewProductDialogFragment newFragment = new CreateNewProductDialogFragment();

                newFragment.show(fragmentManager, "dialog");
            }
        });

        //inflate layout for fragment
        return inflater.inflate(R.layout.fragment_main__fragment__list, container, false);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext, ProductContentProvider.Products.PRODUCTS_URI,
                new String[]{ProductColumns._ID, ProductColumns.PRODUCT_NAME, ProductColumns.PRODUCT_EXPIRATION_DATE,
                ProductColumns.PRODUCT_CATEGORY, ProductColumns.PRODUCT_ICON},
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        mCursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        //switches
        if (s.equals("category-key")){
            mCategory = sharedPreferences.getString("category-key", "all");
        }
    }
}
