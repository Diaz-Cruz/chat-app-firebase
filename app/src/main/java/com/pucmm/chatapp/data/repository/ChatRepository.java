package com.pucmm.chatapp.data.repository;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.pucmm.chatapp.data.model.Message;
import com.pucmm.chatapp.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    public interface MensajesCallback {
        void onMensajes(List<Message> mensajes);
        void onError(String mensaje);
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * El id del chat se construye ordenando los dos uid alfabeticamente.
     * Asi los dos participantes llegan siempre al mismo documento sin
     * consultar la base de datos y sin riesgo de crear chats duplicados.
     */
    public static String construirChatId(String uidA, String uidB) {
        return uidA.compareTo(uidB) < 0 ? uidA + "_" + uidB : uidB + "_" + uidA;
    }

    public ListenerRegistration escucharMensajes(String chatId, @NonNull MensajesCallback cb) {
        return db.collection("chats").document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        cb.onError("No se pudieron cargar los mensajes");
                        return;
                    }
                    if (snapshot == null) return;

                    List<Message> lista = new ArrayList<>();
                    snapshot.forEach(doc -> lista.add(doc.toObject(Message.class)));
                    cb.onMensajes(lista);
                });
    }

    public void enviarMensaje(String chatId, Message mensaje, @NonNull Callback cb) {
        db.collection("chats").document(chatId)
                .collection("messages")
                .add(mensaje)
                .addOnSuccessListener(ref -> cb.onSuccess())
                .addOnFailureListener(e -> cb.onError("No se pudo enviar el mensaje"));
    }
}