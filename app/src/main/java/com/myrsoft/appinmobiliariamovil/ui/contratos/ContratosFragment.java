package com.myrsoft.appinmobiliariamovil.ui.contratos;

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
import com.myrsoft.appinmobiliariamovil.databinding.FragmentContratosBinding;

public class ContratosFragment extends Fragment {

    private ContratosViewModel vm;
    private FragmentContratosBinding binding;
    private ContratoAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(ContratosViewModel.class);
        binding = FragmentContratosBinding.inflate(inflater, container, false);

        vm.getInmueblesMutable().observe(getViewLifecycleOwner(), inmuebles -> {
            if (inmuebles != null) {
                if (inmuebles.isEmpty()) {
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                    binding.rvContratos.setVisibility(View.GONE);
                } else {
                    binding.layoutEmpty.setVisibility(View.GONE);
                    binding.rvContratos.setVisibility(View.VISIBLE);
                    adapter = new ContratoAdapter(inmuebles, getContext(), getLayoutInflater());
                    binding.rvContratos.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.rvContratos.setAdapter(adapter);
                }
            }
        });

        vm.getMensajeMutable().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                binding.tvMensaje.setText(mensaje);
                binding.cardMensaje.setVisibility(View.VISIBLE);
                binding.cardMensaje.postDelayed(() -> binding.cardMensaje.setVisibility(View.GONE), 3000);
            }
        });

        vm.getIsLoadingMutable().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        binding.ivCerrarMensaje.setOnClickListener(v -> binding.cardMensaje.setVisibility(View.GONE));
        
        vm.cargarContratosVigentes();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}