package ua.org.ahf.ahfdb.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import ua.org.ahf.ahfdb.R;
import ua.org.ahf.ahfdb.helper.LocaleHelper;

public class PreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    // register the listener
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    // unregister the listener
    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(LocaleHelper.SELECTED_LANGUAGE)) {
            String locale = sharedPreferences.getString(key, "en_GB");
            LocaleHelper.setLocale(getActivity(), locale);
            getActivity().recreate();
        }
    }
}
