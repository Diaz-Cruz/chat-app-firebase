package com.pucmm.chatapp.ui.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pucmm.chatapp.R;
import com.pucmm.chatapp.databinding.ActivityUsersBinding;
import com.pucmm.chatapp.ui.auth.LoginActivity;
import com.pucmm.chatapp.ui.chat.ChatActivity;

public class UsersActivity extends AppCompatActivity {

    private ActivityUsersBinding binding;
    private UsersViewModel viewModel;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Usuarios");

        viewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        adapter = new UserAdapter(user -> {
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("otroUid", user.getUid());
            i.putExtra("otroNombre", user.getName());
            startActivity(i);
        });

        binding.rvUsuarios.setLayoutManager(new LinearLayoutManager(this));
        binding.rvUsuarios.setAdapter(adapter);

        viewModel.getUsuarios().observe(this, adapter::setUsuarios);
        viewModel.getError().observe(this, m ->
                Toast.makeText(this, m, Toast.LENGTH_LONG).show());

        viewModel.cargarUsuarios();
        viewModel.guardarTokenFcm();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_users, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            viewModel.cerrarSesion();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}