const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");
admin.initializeApp();

exports.notificarMensaje = onDocumentCreated(
  "chats/{chatId}/messages/{messageId}",
  async (event) => {
    const mensaje = event.data.data();

    // Los participantes salen del propio chatId, que es "uidA_uidB".
    // No se lee chats/{chatId} porque esta app no crea ese documento.
    const participantes = event.params.chatId.split("_");
    const destinatario = participantes.find((uid) => uid !== mensaje.senderId);
    if (!destinatario) return;

    const usuario = await admin.firestore().collection("users").doc(destinatario).get();
    const token = usuario.data()?.fcmToken;
    if (!token) return;

    const cuerpo = mensaje.type === "image" ? "Te envio una imagen" : mensaje.text;

    await admin.messaging().send({
      token: token,
      data: {
        titulo: mensaje.senderName || "Nuevo mensaje",
        cuerpo: cuerpo || "",
      },
    });
  }
);