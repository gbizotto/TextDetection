package com.gabriela.textdetection.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.concretesolutions.canarinho.formatador.Formatador;

/**
 * Created by gabriela on 6/1/17.
 */

public final class FormatUtils {
    private FormatUtils() {
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

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
