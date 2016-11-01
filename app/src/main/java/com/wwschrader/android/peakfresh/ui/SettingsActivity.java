package com.wwschrader.android.peakfresh.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.Toast;

import com.wwschrader.android.peakfresh.AlarmReceiver;
import com.wwschrader.android.peakfresh.R;
import com.wwschrader.android.peakfresh.data.ProductColumns;
import com.wwschrader.android.peakfresh.data.ProductContentProvider;

import java.util.ArrayList;

/**
 * Created by Warren on 10/18/2016.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    public static class SettingsFragment extends PreferenceFragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private static final int LOADER_ID = 0;
        ListPreference  mListNotificationDays;
        SwitchPreference mSwitchPreferenceNotificationDays;
        Preference mDeleteCategories;
        Context mContext;
        AlarmReceiver mAlarmReceiver = new AlarmReceiver();
        ArrayList<String> databaseCategoriesArray;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mContext = getActivity();
            //load preferences from xml resource
            addPreferencesFromResource(R.xml.preferences);

            getLoaderManager().initLoader(LOADER_ID, null, this);

            mDeleteCategories = findPreference(this.getString(R.string.delete_categories_key));
            Preference.OnPreferenceClickListener onClickListener = new Preference.OnPreferenceClickListener(){
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (databaseCategoriesArray != null){
                        Intent intent = new Intent(mContext, DeleteCategoryDialogActivity.class);
                        intent.putStringArrayListExtra(mContext.getString(R.string.database_categories_key), databaseCategoriesArray);
                        startActivity(intent);
                        return true;
                    } else {
                        Toast.makeText(mContext, R.string.no_unused_categories_message, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                }
            };
            mDeleteCategories.setOnPreferenceClickListener(onClickListener);

            mSwitchPreferenceNotificationDays = (SwitchPreference) findPreference(this.getString(R.string.pref_switch_key));
            mListNotificationDays = (ListPreference) findPreference(this.getString(R.string.days_selected_key));

            //enable or disable notification days list according to state of notification switch
            mListNotificationDays.setEnabled(mSwitchPreferenceNotificationDays.isChecked());
            Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    //enable list according by checking old value of switch
                    mListNotificationDays.setEnabled(!mSwitchPreferenceNotificationDays.isChecked());
                    //turn on or off alarm receiver
                    if (!mSwitchPreferenceNotificationDays.isChecked()){
                        mAlarmReceiver.setAlarm(getActivity());
                    } else {
                        mAlarmReceiver.cancelAlarm(getActivity());
                    }

                    return true;
                }
            };
            mSwitchPreferenceNotificationDays.setOnPreferenceChangeListener(listener);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(mContext, ProductContentProvider.Products.PRODUCTS_URI,
                    new String[]{ProductColumns.PRODUCT_CATEGORY},
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            databaseCategoriesArray = new ArrayList<>();
            if (data.moveToFirst()){
                do {
                    String productCategory = data.getString(0);
                    //checks to see if category already exists in the arraylist
                    if (!databaseCategoriesArray.contains(productCategory)){
                        //add category if not already in arraylist
                        databaseCategoriesArray.add(productCategory);
                    }
                }while (data.moveToNext());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

}

