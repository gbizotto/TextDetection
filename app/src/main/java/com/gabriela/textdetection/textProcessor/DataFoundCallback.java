package com.gabriela.textdetection.textProcessor;

import java.util.Date;

public interface DataFoundCallback {
    void cpfFound(String cpf);
    void birthDateFound(Date birthDate);
    void nameFound(String name);
}
