package com.example.android.peakfresh.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.android.peakfresh.R;

/**
 * Created by Warren on 9/8/2016.
 */
public class Detail_Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {


            Bundle arguments = new Bundle();
            //Todo: Set info to pass to fragment

            Detail_Fragment detail_fragment = new Detail_Fragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_detail_container, detail_fragment)
                    .commit();
        }
    }
}
