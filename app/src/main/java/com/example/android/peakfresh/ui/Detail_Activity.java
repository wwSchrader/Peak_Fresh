package com.example.android.peakfresh.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.example.android.peakfresh.R;

/**
 * Created by Warren on 9/8/2016.
 */
public class Detail_Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        }
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
