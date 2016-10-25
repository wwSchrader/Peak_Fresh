package com.example.android.peakfresh.ui;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.peakfresh.AlarmReceiver;
import com.example.android.peakfresh.R;

/**
 * Created by Warren on 10/18/2016.
 */

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    public static class SettingsFragment extends PreferenceFragment {
        public static final String KEY_PREF_NOTIFICATION_SWITCH = "notification_switch_key";
        ListPreference  mListNotificationDays;
        SwitchPreference mSwitchPreferenceNotificationDays;

        AlarmReceiver mAlarmReceiver = new AlarmReceiver();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);



            //load preferences from xml resource
            addPreferencesFromResource(R.xml.preferences);

            mSwitchPreferenceNotificationDays = (SwitchPreference) findPreference("pref_key_switch");
            mListNotificationDays = (ListPreference) findPreference("pref_key_days_selected");

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
    }

}

