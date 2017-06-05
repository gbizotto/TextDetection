package com.gabriela.textdetection.dictionary;

import java.util.Arrays;
import java.util.List;

public class Dictionary {

    private static final double MAX_SIMILARITY = 0.71;

    private static final List<String> FORBIDDEN_WORDS = Arrays.asList(
            "carteira",
            "cidade",
            "data",
            "departamento",
            "emissor",
            "federativa",
            "filiacao",
            "habilitação",
            "habilitacao",
            "identidade",
            "ministério",
            "ministe",
            "ministerio",
            "nome",
            "permissão",
            "permissao",
            "pública",
            "publica",
            "rativa",
            "registro",
            "república",
            "territorio",
            "trânsito",
            "transito",
            "valida",
            "validade");

    private static final Dictionary instance = new Dictionary();

    private Dictionary() {
    }

    public static Dictionary getInstance() {
        return instance;
    }

    public boolean isInForbidden(final String compare) {
        JaroWinkler jaroWinkler = new JaroWinkler();
        for (String word : FORBIDDEN_WORDS) {
            double similarity = jaroWinkler.similarity(compare, word);
            if (similarity > MAX_SIMILARITY) {
                return true;
            }
        }
        return false;
    }
}
