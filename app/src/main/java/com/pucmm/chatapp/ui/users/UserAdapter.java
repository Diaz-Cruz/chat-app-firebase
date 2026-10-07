package com.pucmm.chatapp.ui.users;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucmm.chatapp.data.model.User;
import com.pucmm.chatapp.databinding.ItemUserBinding;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnUserClick {
        void onClick(User user);
    }

    private final List<User> usuarios = new ArrayList<>();
    private final OnUserClick listener;

    public UserAdapter(OnUserClick listener) {
        this.listener = listener;
    }

    public void setUsuarios(List<User> nuevos) {
        usuarios.clear();
        usuarios.addAll(nuevos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding b = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(usuarios.get(position));
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private final ItemUserBinding binding;

        UserViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(User user) {
            binding.tvNombre.setText(user.getName());
            binding.tvEmail.setText(user.getEmail());
            binding.getRoot().setOnClickListener(v -> listener.onClick(user));
        }
    }
}