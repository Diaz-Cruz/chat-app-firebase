package com.pucmm.chatapp.util;

import android.util.Patterns;

public class Validators {

    public static boolean emailValido(String email) {
        return email != null
                && !email.trim().isEmpty()
                && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    public static boolean passwordValida(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean noVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }
}