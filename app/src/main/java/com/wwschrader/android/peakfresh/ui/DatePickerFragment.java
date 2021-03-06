package com.wwschrader.android.peakfresh.ui;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //if fragment didn't launch this class, cast using activity
        onDateSet = (DatePickerDialog.OnDateSetListener)getTargetFragment();
        if (onDateSet == null){
            onDateSet = (DatePickerDialog.OnDateSetListener)getActivity();
        }

        return new DatePickerDialog(getActivity(), onDateSet, year, month, day);
    }
}
