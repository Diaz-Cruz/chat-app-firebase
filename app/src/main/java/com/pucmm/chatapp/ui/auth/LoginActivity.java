package com.pucmm.chatapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.pucmm.chatapp.data.repository.AuthRepository;
import com.pucmm.chatapp.databinding.ActivityLoginBinding;
import com.pucmm.chatapp.ui.users.UsersActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        observarViewModel();

        binding.btnLogin.setOnClickListener(v -> viewModel.iniciarSesion(
                binding.etEmail.getText().toString(),
                binding.etPassword.getText().toString()));

        binding.tvIrARegistro.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void observarViewModel() {
        viewModel.getCargando().observe(this, cargando -> {
            binding.progressBar.setVisibility(cargando ? View.VISIBLE : View.GONE);
            binding.btnLogin.setEnabled(!cargando);
        });

        viewModel.getError().observe(this, mensaje -> {
            if (mensaje != null) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getExito().observe(this, ok -> {
            if (Boolean.TRUE.equals(ok)) {
                Intent i = new Intent(this, UsersActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Si ya hay sesion abierta, se salta el login
        if (new AuthRepository().haySesionActiva()) {
            startActivity(new Intent(this, UsersActivity.class));
            finish();
        }
    }
}