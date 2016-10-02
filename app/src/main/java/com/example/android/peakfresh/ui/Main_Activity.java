package com.example.android.peakfresh.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.peakfresh.R;

public class Main_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Checking if activity is using fragment container
        if (findViewById(R.id.fragment_main_container) != null) {

            //if restoring from a previous state
            if (savedInstanceState != null) {
                return;
            }


            Main_Fragment_List mainFragmentList = new Main_Fragment_List();

            //add fragment to fragment_main_container layout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_main_container, mainFragmentList).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
