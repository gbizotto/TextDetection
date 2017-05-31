package com.gabriela.textdetection.utils;

import android.text.TextUtils;

import com.google.android.gms.vision.text.TextBlock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gabriela on 5/31/17.
 */

public final class TextIdentifierUtils {
    private TextIdentifierUtils() {
    }

    public static boolean isCpfValue(TextBlock textBlock) {
        String text = textBlock.getValue()
                .replaceAll("\\.", "")
                .replaceAll("-", "");
        if (TextUtils.isDigitsOnly(text) && text.length() == 11) {
            return true;
        }
        return false;
    }

    public static boolean isLettersOnly(CharSequence str) {
        final int len = str.length();
        for (int cp, i = 0; i < len; i += Character.charCount(cp)) {
            cp = Character.codePointAt(str, i);
            if (!Character.isLetter(cp)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isDate(TextBlock textBlock) {
        return formatDate(textBlock.getValue()) != null;
    }

    public static Date formatDate(String text) {
        Date date = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false);
            date = dateFormat.parse(text);
        } catch (ParseException e) {
        }
        return date;
    }
}
