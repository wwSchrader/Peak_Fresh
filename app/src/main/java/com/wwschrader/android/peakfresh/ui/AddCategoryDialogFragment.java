package com.wwschrader.android.peakfresh.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.wwschrader.android.peakfresh.R;

/**
 * Created by Warren on 10/12/2016.
 */

public class AddCategoryDialogFragment extends DialogFragment {
    private EditText newCategory;
    private CategoryDialogListener mCategoryDialogListener;

    public interface CategoryDialogListener {
        void onDialogPositiveClick(String category);
        void onDialogNegativeClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        Activity activity = (Activity) context;
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mCategoryDialogListener = (CategoryDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception and try to get the target fragment
            try {
                mCategoryDialogListener = (CategoryDialogListener) getTargetFragment();
            } catch (ClassCastException ex){
                //if activity isn't implemented the second time, throw exception
                throw new ClassCastException(activity.toString()
                        + " must implement CategoryDialogListener");
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_category, null);
        newCategory = (EditText) view.findViewById(R.id.edit_text_category);

        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //send input string to host fragment
                        mCategoryDialogListener.onDialogPositiveClick(newCategory.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCategoryDialogListener.onDialogNegativeClick();
                    }
                });
        return builder.create();
    }

}