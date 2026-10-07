package com.pucmm.chatapp.data.repository;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.pucmm.chatapp.data.model.Message;
import com.pucmm.chatapp.util.Callback;
import android.net.Uri;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    public void enviarImagen(String chatId, Uri uri, String miUid, String miNombre,
                             @NonNull Callback cb) {

        String nombreArchivo = "chat_images/" + chatId + "/" + System.currentTimeMillis() + ".jpg";
        StorageReference ref = FirebaseStorage.getInstance().getReference(nombreArchivo);

        ref.putFile(uri)
                .addOnSuccessListener(tarea -> ref.getDownloadUrl()
                        .addOnSuccessListener(url -> {
                            Message m = new Message();
                            m.setSenderId(miUid);
                            m.setSenderName(miNombre);
                            m.setType(Message.TIPO_IMAGEN);
                            m.setImageUrl(url.toString());
                            enviarMensaje(chatId, m, cb);
                        })
                        .addOnFailureListener(e -> cb.onError("No se pudo obtener la URL de la imagen")))
                .addOnFailureListener(e -> cb.onError("No se pudo subir la imagen"));
    }
}