package com.example.android.peakfresh.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import com.example.android.peakfresh.UpdateProductTask;
import com.example.android.peakfresh.data.ProductColumns;

import java.util.Calendar;

/**
 * Created by Warren on 9/12/2016.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    public static final String PRODUCT_ID = "product_id";
    private String[] productId;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Bundle bundle = getArguments();

        productId = bundle.getStringArray(PRODUCT_ID);

        Log.v("DatePicker", month + "/" + day + "/" + year);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //adjust month by one since months start at 0
        int adjMonth = month + 1;

        String newDate = adjMonth + "/" + day + "/" + year;
        UpdateProductTask updateProductTask = new UpdateProductTask(getContext(), productId,
                newDate, ProductColumns.PRODUCT_EXPIRATION_DATE);
        updateProductTask.execute();
    }
}
