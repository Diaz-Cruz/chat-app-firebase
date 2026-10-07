package com.pucmm.chatapp.data.repository;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.pucmm.chatapp.data.model.User;
import com.pucmm.chatapp.util.Callback;

public class AuthRepository {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirebaseUser getUsuarioActual() {
        return auth.getCurrentUser();
    }

    public boolean haySesionActiva() {
        return auth.getCurrentUser() != null;
    }

    public void registrar(String nombre, String email, String password, @NonNull Callback cb) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser usuarioFirebase = result.getUser();
                    String uid = usuarioFirebase.getUid();

                    UserProfileChangeRequest perfil = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nombre)
                            .build();

                    usuarioFirebase.updateProfile(perfil).addOnCompleteListener(t -> {
                        User user = new User(uid, nombre, email);
                        db.collection("users").document(uid).set(user)
                                .addOnSuccessListener(v -> cb.onSuccess())
                                .addOnFailureListener(e -> cb.onError("No se pudo guardar el perfil"));
                    });
                })
                .addOnFailureListener(e -> cb.onError(traducirError(e)));
    }

    public void iniciarSesion(String email, String password, @NonNull Callback cb) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> cb.onSuccess())
                .addOnFailureListener(e -> cb.onError(traducirError(e)));
    }

    public void cerrarSesion() {
        auth.signOut();
    }

    // Firebase devuelve mensajes en ingles y poco claros para el usuario final.
    private String traducirError(Exception e) {
        String m = e.getMessage() == null ? "" : e.getMessage();
        if (m.contains("email address is already in use")) {
            return "Ese correo ya esta registrado";
        }
        if (m.contains("password is invalid") || m.contains("INVALID_LOGIN_CREDENTIALS")) {
            return "Correo o contrasena incorrectos";
        }
        if (m.contains("no user record")) {
            return "No existe una cuenta con ese correo";
        }
        if (m.contains("network error")) {
            return "Sin conexion a internet";
        }
        return "Error de autenticacion";
    }
}