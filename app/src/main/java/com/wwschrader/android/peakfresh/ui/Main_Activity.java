package com.wwschrader.android.peakfresh.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.wwschrader.android.peakfresh.R;
import com.wwschrader.android.peakfresh.Utility;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

public class Main_Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String CATEGORY_SHARED_PREF_KEY = "category-key";
    private Object onItemSelectedListener;
    private boolean onItemSelectedListenerFlag;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set default values for settings menu
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        //setup animated transitions if version supports it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        }
        setContentView(R.layout.activity_main);

        //initialize Google Mobile ads
        MobileAds.initialize(this, getString(R.string.banner_ad_unit_id));

        //obtain firebaseAnayltics instance
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

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
        spinner = (Spinner) MenuItemCompat.getActionView(item);
        //setup the view for the category spinner
        refreshSpinner();

        return true;
    }

    private void refreshSpinner(){
        ArrayList<String> categoryArrayList = Utility.loadCategoryArray(Utility.CATEGORY_ARRAY, this, "main_screen");
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.category_spinner_item, categoryArrayList);
        //set flag to false since onItemSelected is triggered when first set
        onItemSelectedListenerFlag = false;
        adapter.setDropDownViewResource(R.layout.category_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (onItemSelectedListenerFlag){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor prefsEdit = prefs.edit();
            prefsEdit.putString(CATEGORY_SHARED_PREF_KEY, parent.getItemAtPosition(position).toString());
            prefsEdit.apply();
        } else {
            onItemSelectedListenerFlag = true;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        //setting up listener for a key change
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        if (spinner != null){
            refreshSpinner();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //unregister listener for key change
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
