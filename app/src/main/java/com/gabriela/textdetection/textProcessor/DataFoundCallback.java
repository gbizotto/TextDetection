package com.gabriela.textdetection.textProcessor;

import java.util.Date;

public interface DataFoundCallback {
    void dataFound(String cpf, Date birthDate, String name);
}
