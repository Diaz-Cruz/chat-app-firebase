package com.pucmm.chatapp.ui.chat;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;

import com.pucmm.chatapp.data.model.Message;
import com.pucmm.chatapp.databinding.ItemMessageReceivedBinding;
import com.pucmm.chatapp.databinding.ItemMessageSentBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_ENVIADO = 1;
    private static final int TIPO_RECIBIDO = 2;

    private final List<Message> mensajes = new ArrayList<>();
    private final String miUid;
    private final SimpleDateFormat formato =
            new SimpleDateFormat("dd/MM HH:mm", new Locale("es"));

    public MessageAdapter(String miUid) {
        this.miUid = miUid;
    }

    public void setMensajes(List<Message> nuevos) {
        mensajes.clear();
        mensajes.addAll(nuevos);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mensajes.get(position).getSenderId().equals(miUid) ? TIPO_ENVIADO : TIPO_RECIBIDO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TIPO_ENVIADO) {
            return new EnviadoVH(ItemMessageSentBinding.inflate(inflater, parent, false));
        }
        return new RecibidoVH(ItemMessageReceivedBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message m = mensajes.get(position);
        if (holder instanceof EnviadoVH) {
            ((EnviadoVH) holder).bind(m);
        } else {
            ((RecibidoVH) holder).bind(m);
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    private String formatearHora(Timestamp t) {
        // Mientras el servidor confirma, el timestamp local llega nulo
        return t == null ? "Enviando..." : formato.format(t.toDate());
    }

    class EnviadoVH extends RecyclerView.ViewHolder {
        private final ItemMessageSentBinding b;

        EnviadoVH(ItemMessageSentBinding b) { super(b.getRoot()); this.b = b; }

        void bind(Message m) {
            b.tvHora.setText(formatearHora(m.getTimestamp()));

            if (Message.TIPO_IMAGEN.equals(m.getType())) {
                b.tvTexto.setVisibility(android.view.View.GONE);
                b.ivImagen.setVisibility(android.view.View.VISIBLE);
                Glide.with(b.ivImagen).load(m.getImageUrl()).into(b.ivImagen);
            } else {
                b.ivImagen.setVisibility(android.view.View.GONE);
                b.tvTexto.setVisibility(android.view.View.VISIBLE);
                b.tvTexto.setText(m.getText());
            }
        }
    }

    class RecibidoVH extends RecyclerView.ViewHolder {
        private final ItemMessageReceivedBinding b;

        RecibidoVH(ItemMessageReceivedBinding b) { super(b.getRoot()); this.b = b; }

        void bind(Message m) {
            b.tvNombre.setText(m.getSenderName());
            b.tvHora.setText(formatearHora(m.getTimestamp()));

            if (Message.TIPO_IMAGEN.equals(m.getType())) {
                b.tvTexto.setVisibility(android.view.View.GONE);
                b.ivImagen.setVisibility(android.view.View.VISIBLE);
                Glide.with(b.ivImagen).load(m.getImageUrl()).into(b.ivImagen);
            } else {
                b.ivImagen.setVisibility(android.view.View.GONE);
                b.tvTexto.setVisibility(android.view.View.VISIBLE);
                b.tvTexto.setText(m.getText());
            }
        }
    }
}