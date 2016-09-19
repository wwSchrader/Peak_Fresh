package com.example.android.peakfresh.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.android.peakfresh.R;

/**
 * Created by Warren on 9/15/2016.
 */
public class CreateNewProductDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private String expDate;
    private ImageView product_icon_detail;
    private Button mCameraButton, mExpirationDateButton;
    private EditText mEditTitleText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.new_product_dialog_fragment, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.new_product_dialog_fragment, null))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //// TODO: 9/18/2016 Set action to add product to database
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog dialog = builder.create();

        return dialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        int adjMonth = month + 1;
        expDate = adjMonth + "/" + day + "/" + year;
    }
}
