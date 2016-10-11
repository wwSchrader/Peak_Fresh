package com.example.android.peakfresh.ui;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.android.peakfresh.R;
import com.example.android.peakfresh.Utility;

import java.util.ArrayList;

public class Main_Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Object onItemSelectedListener;
    private boolean onItemSelectedListenerFlag;

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
        MenuItem item = menu.findItem(R.id.menuSort);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        //setup the view for the category spinner
        ArrayList<String> categoryArrayList = Utility.loadCategoryArray(Utility.CATEGORY_ARRAY, this);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.category_spinner_item, categoryArrayList);
        //set flag to false since onItemSelected is triggered when first set
        onItemSelectedListenerFlag = false;
        adapter.setDropDownViewResource(R.layout.category_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
