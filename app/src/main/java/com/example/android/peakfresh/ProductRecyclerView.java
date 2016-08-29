package com.example.android.peakfresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Warren on 8/28/2016.
 */
public class ProductRecyclerView extends RecyclerView.Adapter<ProductRecyclerView.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<String> mValues;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final View mView;
        public final ImageView mImageView;
        public final TextView mTextView;
        public String mBoundString;

        public ViewHolder(View view){
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.product_icon);
            mTextView = (TextView) view.findViewById(R.id.product_title);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public String getValueAt(int position) {
        return mValues.get(position);
    }

    public ProductRecyclerView(Context context, List<String> items){
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_listing, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mBoundString = mValues.get(position);
        holder.mTextView.setText(mValues.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Todo: Set onclick intent here
            }
        });

        Glide.with(holder.mImageView.getContext())
                .load(R.mipmap.ic_launcher)
                .fitCenter()
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


}
