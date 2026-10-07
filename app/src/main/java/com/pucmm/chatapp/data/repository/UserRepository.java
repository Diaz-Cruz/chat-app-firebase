package com.pucmm.chatapp.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pucmm.chatapp.data.model.User;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.messaging.FirebaseMessaging;

public class UserRepository {

    public interface ListaCallback {
        void onUsuarios(List<User> usuarios);
        void onError(String mensaje);
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void obtenerOtrosUsuarios(ListaCallback cb) {
        String miUid = FirebaseAuth.getInstance().getUid();

        db.collection("users").get()
                .addOnSuccessListener(snapshot -> {
                    List<User> lista = new ArrayList<>();
                    snapshot.forEach(doc -> {
                        User u = doc.toObject(User.class);
                        // No me muestro a mi mismo en la lista
                        if (u.getUid() != null && !u.getUid().equals(miUid)) {
                            lista.add(u);
                        }
                    });
                    cb.onUsuarios(lista);
                })
                .addOnFailureListener(e -> cb.onError("No se pudo cargar la lista de usuarios"));
    }

    public void guardarTokenFcm() {
        String miUid = FirebaseAuth.getInstance().getUid();
        if (miUid == null) return;

        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token ->
                        db.collection("users").document(miUid).update("fcmToken", token));
    }
}