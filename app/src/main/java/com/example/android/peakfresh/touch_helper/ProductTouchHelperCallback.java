package com.example.android.peakfresh.touch_helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Warren on 8/29/2016.
 */
public class ProductTouchHelperCallback extends ItemTouchHelper.Callback {
    private final ProductTouchHelperAdapter mItemTouchHelper;

    public  ProductTouchHelperCallback(ProductTouchHelperAdapter adapter) {
        mItemTouchHelper = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = 0;
        final int swipeFlags= ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mItemTouchHelper.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE){
            ProductTouchHelperViewHolder productTouchHelperViewHolder = (ProductTouchHelperViewHolder) viewHolder;
            productTouchHelperViewHolder.onItemSelected();
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        ProductTouchHelperViewHolder productTouchHelperViewHolder = (ProductTouchHelperViewHolder) viewHolder;
        productTouchHelperViewHolder.onItemClear();
    }
}
