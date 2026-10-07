package com.pucmm.chatapp.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;

import com.pucmm.chatapp.data.model.Message;
import com.pucmm.chatapp.data.repository.ChatRepository;
import com.pucmm.chatapp.util.Callback;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private final ChatRepository repo = new ChatRepository();

    private final MutableLiveData<List<Message>> mensajes = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private ListenerRegistration registro;
    private String chatId;

    public LiveData<List<Message>> getMensajes() { return mensajes; }
    public LiveData<String> getError() { return error; }

    public String getMiUid() { return FirebaseAuth.getInstance().getUid(); }

    public String getMiNombre() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        String nombre = usuario != null ? usuario.getDisplayName() : null;
        return nombre != null ? nombre : "Usuario";
    }

    public void iniciar(String otroUid) {
        if (registro != null) return;      // evita abrir dos listeners

        this.chatId = ChatRepository.construirChatId(getMiUid(), otroUid);

        registro = repo.escucharMensajes(chatId, new ChatRepository.MensajesCallback() {
            @Override public void onMensajes(List<Message> lista) { mensajes.setValue(lista); }
            @Override public void onError(String m) { error.setValue(m); }
        });
    }

    public void enviarTexto(String texto, String miNombre) {
        // Requisito del enunciado: no permitir mensajes vacios
        if (texto == null || texto.trim().isEmpty()) return;

        Message m = new Message(getMiUid(), miNombre, texto.trim());
        repo.enviarMensaje(chatId, m, new Callback() {
            @Override public void onSuccess() { }
            @Override public void onError(String msg) { error.setValue(msg); }
        });
    }

    public void enviarImagen(Uri uri) {
        repo.enviarImagen(chatId, uri, getMiUid(), getMiNombre(), new Callback() {
            @Override public void onSuccess() { }
            @Override public void onError(String m) { error.setValue(m); }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Cerrar el listener cuando el ViewModel muere evita fugas de memoria
        if (registro != null) registro.remove();
    }
}