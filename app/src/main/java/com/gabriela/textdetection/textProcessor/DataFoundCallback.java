package com.gabriela.textdetection.textProcessor;

import android.app.Activity;

import java.util.Date;

public interface DataFoundCallback {
    void cpfFound(String cpf, Activity activity);
    void birthDateFound(Date birthDate, Activity activity);
    void nameFound(String name, Activity activity);
}
