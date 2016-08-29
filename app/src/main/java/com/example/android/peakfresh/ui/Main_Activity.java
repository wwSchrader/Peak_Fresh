package com.example.android.peakfresh.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.peakfresh.R;

public class Main_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Checking if activity is using fragment container
        if (findViewById(R.id.fragement_main_container) != null) {

            //if restoring from a previous state
            if (savedInstanceState != null) {
                return;
            }


            Main_Fragment_List mainFragmentList = new Main_Fragment_List();

            //add fragment to fragment_main_container layout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragement_main_container, mainFragmentList).commit();
        }


    }
}
