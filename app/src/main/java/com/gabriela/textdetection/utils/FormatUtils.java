package com.gabriela.textdetection.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.gabriela.textdetection.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.concretesolutions.canarinho.formatador.Formatador;

/**
 * Created by gabriela on 6/1/17.
 */

public final class FormatUtils {
    private FormatUtils() {
    }

    public static String formatDate(Date date, Context context) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(context.getString(R.string.birth_date_format)).format(date);
    }

    @NonNull
    public static String cleanFormattting(String text){
        return text
                .replaceAll("\\.", "")
                .replaceAll("-", "")
                .replaceAll(" ", "")
                .replaceAll(",", "");
    }

    public static String formatCpf(String cpf) {
        cpf = cleanFormattting(cpf);
        if (Formatador.CPF.podeSerFormatado(cpf)) {
            return Formatador.CPF.formata(cpf);
        }
        return cpf;
    }

    @NonNull
    public static String capitalizeText(String text) {
        String[] words = text.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            builder.append(word.substring(0, 1).toUpperCase())
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
        }
        return builder.toString();
    }
}
