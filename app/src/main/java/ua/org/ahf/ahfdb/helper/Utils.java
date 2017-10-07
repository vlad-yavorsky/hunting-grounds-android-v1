package ua.org.ahf.ahfdb.helper;

import android.text.Editable;
import android.text.Html;

import org.xml.sax.XMLReader;

public class Utils {

    public static class UlTagHandler implements Html.TagHandler {
        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if(tag.equals("li") && opening) output.append("\t• ");
            if(tag.equals("li") && !opening) output.append("\n");
        }
    }

    public static CharSequence trimTrailingWhitespace(CharSequence source) {
        if(source == null) {
            return "";
        }

        int i = source.length();

        // loop back to the first non-whitespace character
        while(--i >= 0 && Character.isWhitespace(source.charAt(i)));

        return source.subSequence(0, i + 1);
    }
}
