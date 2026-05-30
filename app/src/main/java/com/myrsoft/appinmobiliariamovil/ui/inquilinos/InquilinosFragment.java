package com.myrsoft.appinmobiliariamovil.ui.inquilinos;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.myrsoft.appinmobiliariamovil.R;
import com.myrsoft.appinmobiliariamovil.databinding.FragmentInquilinosBinding;

public class InquilinosFragment extends Fragment {

    private InquilinosViewModel vm;
    private FragmentInquilinosBinding binding;
    private InquilinoAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(InquilinosViewModel.class);
        binding = FragmentInquilinosBinding.inflate(inflater, container, false);

        // Observar la lista de inmuebles alquilados
        vm.getInmueblesMutable().observe(getViewLifecycleOwner(), inmuebles -> {
            if (inmuebles != null) {
                if (inmuebles.isEmpty()) {
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                    binding.rvInquilinos.setVisibility(View.GONE);
                } else {
                    binding.layoutEmpty.setVisibility(View.GONE);
                    binding.rvInquilinos.setVisibility(View.VISIBLE);
                    adapter = new InquilinoAdapter(inmuebles, getContext(), getLayoutInflater());
                    binding.rvInquilinos.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.rvInquilinos.setAdapter(adapter);
                }
            }
        });

        // Observar mensajes
        vm.getMensajeMutable().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                binding.tvMensaje.setText(mensaje);
                binding.cardMensaje.setVisibility(View.VISIBLE);
                binding.cardMensaje.postDelayed(() -> binding.cardMensaje.setVisibility(View.GONE), 3000);
            }
        });

        // Observar loading state
        vm.getIsLoadingMutable().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Cerrar mensaje
        binding.ivCerrarMensaje.setOnClickListener(v -> binding.cardMensaje.setVisibility(View.GONE));
        
        // Cargar los inmuebles alquilados
        vm.cargarInmueblesAlquilados();
        
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}