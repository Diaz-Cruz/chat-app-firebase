package com.pucmm.chatapp.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pucmm.chatapp.data.repository.AuthRepository;
import com.pucmm.chatapp.util.Callback;
import com.pucmm.chatapp.util.Validators;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repo = new AuthRepository();

    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> exito = new MutableLiveData<>();

    public LiveData<Boolean> getCargando() { return cargando; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getExito() { return exito; }

    public void registrar(String nombre, String email, String password, String confirmacion) {
        if (!Validators.noVacio(nombre)) { error.setValue("Escribe tu nombre"); return; }
        if (!Validators.emailValido(email)) { error.setValue("El correo no es valido"); return; }
        if (!Validators.passwordValida(password)) {
            error.setValue("La contrasena debe tener al menos 6 caracteres");
            return;
        }
        if (!password.equals(confirmacion)) {
            error.setValue("Las contrasenas no coinciden");
            return;
        }

        cargando.setValue(true);
        repo.registrar(nombre.trim(), email.trim(), password, new Callback() {
            @Override public void onSuccess() {
                cargando.setValue(false);
                exito.setValue(true);
            }
            @Override public void onError(String mensaje) {
                cargando.setValue(false);
                error.setValue(mensaje);
            }
        });
    }

    public void iniciarSesion(String email, String password) {
        if (!Validators.emailValido(email)) { error.setValue("El correo no es valido"); return; }
        if (!Validators.noVacio(password)) { error.setValue("Escribe tu contrasena"); return; }

        cargando.setValue(true);
        repo.iniciarSesion(email.trim(), password, new Callback() {
            @Override public void onSuccess() {
                cargando.setValue(false);
                exito.setValue(true);
            }
            @Override public void onError(String mensaje) {
                cargando.setValue(false);
                error.setValue(mensaje);
            }
        });
    }

    public boolean haySesionActiva() {
        return repo.haySesionActiva();
    }
}