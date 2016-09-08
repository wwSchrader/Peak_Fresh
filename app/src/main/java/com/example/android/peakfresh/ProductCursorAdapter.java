package com.example.android.peakfresh;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.peakfresh.data.ProductColumns;
import com.example.android.peakfresh.data.ProductContentProvider;
import com.example.android.peakfresh.touch_helper.ProductTouchHelperAdapter;
import com.example.android.peakfresh.touch_helper.ProductTouchHelperViewHolder;

/**
 * Created by Warren on 8/29/2016.
 */
public class ProductCursorAdapter extends CursorRecyclerViewAdapter<ProductCursorAdapter.ViewHolder>
    implements ProductTouchHelperAdapter{

    private static Context mContext;
    private static Typeface robotoLight;
    public ProductCursorAdapter(Context context, Cursor cursor){
        super(context, cursor);
        mContext = context;
    }

    @Override
    public void onItemDismiss(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        String productTitle = c.getString(c.getColumnIndex(ProductColumns.PRODUCT_NAME));
        mContext.getContentResolver().delete(ProductContentProvider.Products.withName(productTitle), null, null);
        notifyItemRemoved(position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements
            ProductTouchHelperViewHolder, View.OnClickListener{
        public final TextView productName;
        public final TextView productExpirationDate;
        public final ImageView productIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.product_title);
            productName.setTypeface(robotoLight);
            productExpirationDate = (TextView) itemView.findViewById(R.id.product_expiration_date);
            productIcon = (ImageView) itemView.findViewById(R.id.product_icon);
        }

        @Override
        public void onClick(View view) {

        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            //set to no color if cleared
            itemView.setBackgroundColor(0);
        }
    }

    @Override
    public void onBindViewHolder(ProductCursorAdapter.ViewHolder viewHolder, Cursor cursor) {
        viewHolder.productName.setText(cursor.getString(cursor.getColumnIndex(ProductColumns.PRODUCT_NAME)));
        viewHolder.productExpirationDate.setText(cursor.getString(cursor.getColumnIndex(ProductColumns.PRODUCT_EXPIRATION_DATE)));

        //get string path and converts into int resource id
        int resourceId = cursor.getInt(cursor.getColumnIndex(ProductColumns.PRODUCT_ICON));
        Log.v("BindView", " " + resourceId);
        Glide.with(mContext)
                .load(resourceId)
                .placeholder(R.mipmap.ic_launcher)
                .fitCenter()
                .into(viewHolder.productIcon);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        robotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
        View productView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_listing, parent, false);
        return new ViewHolder(productView);
    }
}
