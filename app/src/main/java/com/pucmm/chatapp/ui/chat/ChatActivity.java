package com.pucmm.chatapp.ui.chat;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pucmm.chatapp.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private ChatViewModel viewModel;
    private MessageAdapter adapter;

    private final ActivityResultLauncher<String> selectorImagen =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    viewModel.enviarImagen(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String otroUid = getIntent().getStringExtra("otroUid");
        String otroNombre = getIntent().getStringExtra("otroNombre");
        setTitle(otroNombre);

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
            viewModel.enviarTexto(binding.etMensaje.getText().toString());
            binding.etMensaje.setText("");
        });

        // El boton de la galeria abre el selector del sistema
        binding.btnAdjuntar.setOnClickListener(v -> selectorImagen.launch("image/*"));

        viewModel.iniciar(otroUid);
    }
}