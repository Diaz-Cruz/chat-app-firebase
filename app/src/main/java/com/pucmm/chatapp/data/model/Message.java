package com.pucmm.chatapp.data.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class Message {

    public static final String TIPO_TEXTO = "text";
    public static final String TIPO_IMAGEN = "image";

    private String senderId;
    private String senderName;
    private String text;
    private String imageUrl;
    private String type;

    //Hace que Firestore ponga la hora del servidor cuando el documento se escribe en vez de la hora del celular
    @ServerTimestamp
    private Timestamp timestamp;

    public Message() { }

    public Message(String senderId, String senderName, String text) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.text = text;
        this.type = TIPO_TEXTO;
    }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}