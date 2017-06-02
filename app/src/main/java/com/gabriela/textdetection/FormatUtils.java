package com.gabriela.textdetection;

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

    public static String formatCpf(String cpf) {
        if (Formatador.CPF.podeSerFormatado(cpf)) {
            return Formatador.CPF.formata(cpf);
        }
        return cpf;
    }
}
