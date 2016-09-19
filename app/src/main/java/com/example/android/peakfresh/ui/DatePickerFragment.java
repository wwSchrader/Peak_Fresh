package com.example.android.peakfresh.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by Warren on 9/12/2016.
 */
public class DatePickerFragment extends DialogFragment{
    private DatePickerDialog.OnDateSetListener onDateSet;
    public static final String PRODUCT_ID = "product_id";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        onDateSet = (DatePickerDialog.OnDateSetListener)getTargetFragment();
        return new DatePickerDialog(getActivity(), onDateSet, year, month, day);
    }
}
