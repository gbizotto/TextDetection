package com.gabriela.textdetection.dictionary;

/**
 * Created by gabriela on 6/2/17.
 */

public class Dictionary {

    private static final Dictionary instance = new Dictionary();

    private BloomFilter<String> bloomFilter;

    private Dictionary() {
    }

    public static Dictionary getInstance() {
        return instance;
    }

    public boolean isInDictionary(String text) {
        if (bloomFilter == null) {
            initFilter();
        }
        return bloomFilter.contains(text);
    }

    private void initFilter() {
        bloomFilter = new BloomFilter<>(0.1, 100);
        bloomFilter.add("república");
        bloomFilter.add("federativa");
        bloomFilter.add("ministério");
        bloomFilter.add("cidade");
        bloomFilter.add("departamento");
        bloomFilter.add("trânsito");
        bloomFilter.add("carteira");
        bloomFilter.add("habilitação");
        bloomFilter.add("pública");
        bloomFilter.add("publica");
        bloomFilter.add("transito");
        bloomFilter.add("valida");
        bloomFilter.add("territorio");
        bloomFilter.add("filiacao");
        bloomFilter.add("ministerio");
        bloomFilter.add("identidade");
        bloomFilter.add("emissor");
        bloomFilter.add("data");
        bloomFilter.add("habilitacao");
        bloomFilter.add("permissão");
        bloomFilter.add("permissao");
        bloomFilter.add("registro");
        bloomFilter.add("nome");
        bloomFilter.add("validade");
    }
}
