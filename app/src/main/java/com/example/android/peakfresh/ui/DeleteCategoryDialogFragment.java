package com.example.android.peakfresh.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.android.peakfresh.R;
import com.example.android.peakfresh.Utility;
import com.example.android.peakfresh.data.ProductColumns;
import com.example.android.peakfresh.data.ProductContentProvider;

import java.util.ArrayList;

/**
 * Created by Warren on 10/30/2016.
 */

public class DeleteCategoryDialogFragment extends DialogFragment {
    ArrayList<String> categoryArrayList, selectedItems;
    String[] unusedCategoryrArray;
    Cursor mCursor;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        categoryArrayList = Utility.loadCategoryArray(Utility.CATEGORY_ARRAY, getContext(), "preference_screen");

        mCursor = getContext().getContentResolver().query(
                ProductContentProvider.Products.PRODUCTS_URI,
                null,
                null,
                null,
                null
        );

        if (mCursor != null && mCursor.moveToFirst()){
            do {
                String categoryToMatch = mCursor.getString(mCursor.getColumnIndex(ProductColumns.PRODUCT_CATEGORY));
                for (int i = 0; i < categoryArrayList.size(); i++){
                    if (categoryArrayList.get(i).equals(categoryToMatch)){
                        categoryArrayList.remove(i);
                        break;
                    }
                }
            }while (mCursor.moveToNext());
        }

        unusedCategoryrArray = new String[categoryArrayList.size()];
        unusedCategoryrArray = categoryArrayList.toArray(unusedCategoryrArray);
        selectedItems = new ArrayList<String>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_categories_dialog_title)
            .setMultiChoiceItems(unusedCategoryrArray, null, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    if (b){
                        selectedItems.add(unusedCategoryrArray[i]);
                    } else if (selectedItems.contains(unusedCategoryrArray[i])){
                        selectedItems.remove(unusedCategoryrArray[i]);
                    }
                }
            })
        .setPositiveButton(R.string.delete_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Utility.removeItemFromCategoryArray(Utility.CATEGORY_ARRAY, getContext(), selectedItems);
                getActivity().finish();
            }
        })
        .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finish();
            }
        });

        return builder.create();
    }
}
