package com.gabriela.textdetection.utils;

import android.util.Log;

import com.google.android.gms.vision.text.TextBlock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.concretesolutions.canarinho.validator.Validador;

public final class TextIdentifierUtils {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private static final String[] NOT_NAME = {"válida", "território", "nacional", "nome", "filiação", "naturalidade", "origem",
            "república", "federativa", "ministério", "cidade", "departamento", "trânsito", "carteira", "habilitação", "pública", "publica", "transito",
            "valida", "territorio", "filiacao", "ministerio", "identidade", "emissor", "data", "habilitacao", "permissão", "permissao", "registro", "nome", "validade"};

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

        text = removePossibleSeparators(text);
        text = text
                .replaceAll("\\.", "")
                .replaceAll("-", "")
                .replaceAll(" ", "");

        if (isLettersOnly(text)) {
            return null;
        }

        String cpf = null;
        String value;
        if (text.length() >= 11) {
            for (int i = 0; i < text.length() - 11; i++) {
                if (text.length() > i + 11) {
                    value = text.substring(i, i + 11);
                    if (Validador.CPF.ehValido(value)) {
                        cpf = value;
                        break;
                    }
                }
            }
        }
        return cpf;
    }

    public static Date getBirthDate(String text) {
        text = text.replaceAll(" ", "");
        String[] pieces = text.split("/");

        if (pieces.length < 3 || pieces[0].length() < 2 || pieces[1].length() != 2 || pieces[2].length() < 4) {
            return null;
        }

        String day = replaceLettersLookLikeNumbers(
                pieces[0].substring(
                        pieces[0].length() - 2,
                        pieces[0].length())
                        .toLowerCase());
        String month = replaceLettersLookLikeNumbers(pieces[1].toLowerCase());
        String year = replaceLettersLookLikeNumbers(pieces[2].substring(0, 4).toLowerCase());
        return formatDate(day + "/" + month + "/" + year);
    }

    private static String replaceLettersLookLikeNumbers(String text) {
        return text.replaceAll("o", "0")
                .replaceAll("b", "8")
                .replaceAll("l", "1");
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
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
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
