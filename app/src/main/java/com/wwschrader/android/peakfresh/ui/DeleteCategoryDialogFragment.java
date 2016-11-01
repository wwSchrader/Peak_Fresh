package com.wwschrader.android.peakfresh.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.wwschrader.android.peakfresh.R;
import com.wwschrader.android.peakfresh.Utility;

import java.util.ArrayList;

/**
 * Created by Warren on 10/30/2016.
 */

public class DeleteCategoryDialogFragment extends DialogFragment {
    private ArrayList<String> categoryArrayList;
    private ArrayList<String> selectedItems;
    private ArrayList<String> databaseCategories;
    private String[] unusedCategoryArray;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        categoryArrayList = Utility.loadCategoryArray(Utility.CATEGORY_ARRAY, getContext(), "preference_screen");

        Bundle extras = getActivity().getIntent().getExtras();
        databaseCategories = extras.getStringArrayList(this.getString(R.string.database_categories_key));

        //remove matching categories from arraylist so that only unused categories remain
        for (int i = 0; i < databaseCategories.size(); i++){
            if (categoryArrayList.contains(databaseCategories.get(i))){
                categoryArrayList.remove(categoryArrayList.indexOf(databaseCategories.get(i)));
            }
        }

        unusedCategoryArray = new String[categoryArrayList.size()];
        unusedCategoryArray = categoryArrayList.toArray(unusedCategoryArray);
        selectedItems = new ArrayList<String>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_categories_dialog_title)
            .setMultiChoiceItems(unusedCategoryArray, null, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    if (b){
                        selectedItems.add(unusedCategoryArray[i]);
                    } else if (selectedItems.contains(unusedCategoryArray[i])){
                        selectedItems.remove(unusedCategoryArray[i]);
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
