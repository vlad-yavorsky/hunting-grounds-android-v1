package ua.org.ahf.ahfdb.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;

import org.xml.sax.XMLReader;

public class Utils {

    public static class UlTagHandler implements Html.TagHandler {
        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
//            if(tag.equals("ul") && !opening) output.append("\n");
            if(tag.equals("li") && opening) output.append("\t• ");
            if(tag.equals("li") && !opening) output.append("\n");
        }
    }


    private static final String LAST_FRAGMENT = "LAST_FRAGMENT";

    public static String getLastFragment(Context context, String lastFragment) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(LAST_FRAGMENT, lastFragment);
    }

    public static void setLastFragment(Context context, String lastFragment) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(LAST_FRAGMENT, lastFragment);
        editor.apply();
    }

}
