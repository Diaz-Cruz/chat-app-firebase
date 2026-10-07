package com.pucmm.chatapp.ui.users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pucmm.chatapp.data.model.User;
import com.pucmm.chatapp.data.repository.AuthRepository;
import com.pucmm.chatapp.data.repository.UserRepository;

import java.util.List;

public class UsersViewModel extends ViewModel {

    private final UserRepository userRepo = new UserRepository();
    private final AuthRepository authRepo = new AuthRepository();

    private final MutableLiveData<List<User>> usuarios = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<List<User>> getUsuarios() { return usuarios; }
    public LiveData<String> getError() { return error; }

    public void cargarUsuarios() {
        userRepo.obtenerOtrosUsuarios(new UserRepository.ListaCallback() {
            @Override public void onUsuarios(List<User> lista) { usuarios.setValue(lista); }
            @Override public void onError(String mensaje) { error.setValue(mensaje); }
        });
    }

    public void cerrarSesion() {
        authRepo.cerrarSesion();
    }
}
