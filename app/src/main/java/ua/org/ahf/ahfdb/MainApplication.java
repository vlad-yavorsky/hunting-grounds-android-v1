package ua.org.ahf.ahfdb;

import android.app.Application;
import android.content.Context;

import ua.org.ahf.ahfdb.helper.LocaleHelper;

public class MainApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, LocaleHelper.getLanguage(base)));
    }

}
