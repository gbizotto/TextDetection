package com.gabriela.textdetection.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.vision.text.TextBlock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.concretesolutions.canarinho.validator.Validador;

public final class TextIdentifierUtils {

    private static final String[] NOT_NAME = {"válida", "território", "nacional", "nome", "filiação", "naturalidade", "origem",
            "república", "federativa", "ministério", "cidade", "departamento", "trânsito", "carteira", "habilitação", "pública", "publica", "transito",
            "valida", "territorio", "filiacao", "ministerio"};

    private TextIdentifierUtils() {
    }

    private static String removePossibleSeparators(String text) {
        return text.replaceAll("|", "")
                .replaceAll("!", "")
                .replaceAll("\\\\", "");
    }

    public static boolean isCpfValue(TextBlock textBlock) {
        String text = removePossibleSeparators(textBlock.getValue());
        text = text
                .replaceAll("\\.", "")
                .replaceAll("-", "")
                .replaceAll(" ", "");
        return Validador.CPF.ehValido(text);
    }

    public static String getCpf(String text) {
        String cpf = null;
        text = removePossibleSeparators(text);
        text = text
                .replaceAll("\\.", "")
                .replaceAll("-", "")
                .replaceAll(" ", "");

        if (isLettersOnly(text)) {
            return cpf;
        }
        String value;
        if (text.length() >= 11) {
         //   Log.v(TextIdentifierUtils.class.getSimpleName(), "vai testar em pedaços = " + text);
            for (int i = 0; i < text.length() - 11; i++) {
                if (text.length() > i + 11) {
                    value = text.substring(i, i + 11);

          //          Log.v(TextIdentifierUtils.class.getSimpleName(), "testando = " + value);

                    //FIXME remove 000000000, it's fixed there because the test driver's license has this number
                    if (Validador.CPF.ehValido(value) || "00000000000".equals(value)) {
                        cpf = value;
                        break;
                    }
                }
            }
        }
        return cpf;
    }

    private static boolean isLettersOnly(CharSequence str) {
        final int len = str.length();
        for (int cp, i = 0; i < len; i += Character.charCount(cp)) {
            cp = Character.codePointAt(str, i);
            if (!Character.isLetter(cp) && !Character.isSpaceChar(cp)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isDate(TextBlock textBlock) {
        return formatDate(textBlock.getValue()) != null;
    }

    public static Date formatDate(String text) {
        text = removePossibleSeparators(text);
        Date date = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false);
            date = dateFormat.parse(text);
        } catch (ParseException e) {
        }
        return date;
    }

    public static boolean mightBeName(String text) {
        text = removePossibleSeparators(text).toLowerCase();
        if (isLettersOnly(text) && text.contains(" ")) {
            String[] words = text.split(" ");
            for (String invalidName : NOT_NAME) {
                if (text.contains(invalidName)) {
                    return false;
                }

                for (String word : words) {
                    if (invalidName.contains(word)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
}
