package ua.org.ahf.ahfdb;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.Editable;
import android.text.Html;
import android.util.DisplayMetrics;

import org.xml.sax.XMLReader;

import java.util.Locale;

public class Utils {

    public static class UlTagHandler implements Html.TagHandler {
        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
//            if(tag.equals("ul") && !opening) output.append("\n");
            if(tag.equals("li") && opening) output.append("\t• ");
            if(tag.equals("li") && !opening) output.append("\n");
        }
    }

    public static void setLocale(String lang, Activity activity) {
        Locale myLocale = new Locale(lang);
        Resources res = activity.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(activity, activity.getClass());
        activity.startActivity(refresh);
        activity.finish();
    }
}
