package com.pucmm.chatapp.ui.chat;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.pucmm.chatapp.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private ChatViewModel viewModel;
    private MessageAdapter adapter;
    private String miNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String otroUid = getIntent().getStringExtra("otroUid");
        String otroNombre = getIntent().getStringExtra("otroNombre");
        setTitle(otroNombre);

        // El nombre se guardo en el perfil de Auth al registrarse (ver AuthRepository)
        miNombre = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (miNombre == null) miNombre = "Usuario";

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        adapter = new MessageAdapter(viewModel.getMiUid());

        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);          // la lista arranca pegada abajo
        binding.rvMensajes.setLayoutManager(lm);
        binding.rvMensajes.setAdapter(adapter);

        viewModel.getMensajes().observe(this, lista -> {
            adapter.setMensajes(lista);
            if (!lista.isEmpty()) {
                binding.rvMensajes.smoothScrollToPosition(lista.size() - 1);
            }
        });

        viewModel.getError().observe(this, m ->
                Toast.makeText(this, m, Toast.LENGTH_LONG).show());

        binding.btnEnviar.setOnClickListener(v -> {
            viewModel.enviarTexto(binding.etMensaje.getText().toString(), miNombre);
            binding.etMensaje.setText("");
        });

        viewModel.iniciar(otroUid);
    }
}