package com.example.android.peakfresh.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Warren on 10/31/2016.
 */

public class DeleteCategoryDialogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeleteCategoryDialogFragment categoryDialogFragment = new DeleteCategoryDialogFragment();
        categoryDialogFragment.show(getSupportFragmentManager(), "deleteCategory");
    }
}
