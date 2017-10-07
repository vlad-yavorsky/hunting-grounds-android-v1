package ua.org.ahf.ahfdb.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import ua.org.ahf.ahfdb.R;
import ua.org.ahf.ahfdb.activity.NavigationActivity;
import ua.org.ahf.ahfdb.helper.AsyncResponse;
import ua.org.ahf.ahfdb.helper.DbHelper;

public class PreferencesFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String KEY_UPDATE_DATABASE = "update_database";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        findPreference(KEY_UPDATE_DATABASE).setOnPreferenceClickListener(this);
    }

    // register the listener
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        ((NavigationActivity) getActivity()).getSupportActionBar().setTitle(R.string.preferences);
    }

    // unregister the listener
    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        switch (key) {
//            case "":
//                break;
//        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        String key = preference.getKey();
        switch (key) {
            case KEY_UPDATE_DATABASE:
                AsyncResponse listener = new AsyncResponse() {
                    @Override
                    public void processFinish(Boolean result) {
                        if (result) {
                            Toast.makeText(getActivity(), getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                DbHelper.instance(getActivity()).downloadData(listener);
                break;
        }
        return true;
    }

}
