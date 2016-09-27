package com.example.android.peakfresh.ui;

import android.os.Build;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportPostponeEnterTransition();
        }
        if (savedInstanceState == null) {


            Bundle arguments = new Bundle();
            arguments.putInt(Detail_Fragment.PRODUCT_ID_KEY, getIntent().getIntExtra(Detail_Fragment.PRODUCT_ID_KEY, 0));

            Detail_Fragment detail_fragment = new Detail_Fragment();
            detail_fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_detail_container, detail_fragment)
                    .commit();
        }
    }
}
