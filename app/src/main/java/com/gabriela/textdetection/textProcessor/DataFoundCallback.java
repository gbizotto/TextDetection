package com.gabriela.textdetection.textProcessor;

import java.util.Date;

/**
 * Created by gabriela on 5/31/17.
 */

public interface DataFoundCallback {
    void dataFound(String cpf, Date birthDate, String name);
}
