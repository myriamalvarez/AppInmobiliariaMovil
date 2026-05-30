package com.myrsoft.appinmobiliariamovil.ui.pagos;

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
import com.myrsoft.appinmobiliariamovil.databinding.FragmentPagosBinding;

public class PagosFragment extends Fragment {

    private PagosViewModel vm;
    private FragmentPagosBinding binding;
    private PagoAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(PagosViewModel.class);
        binding = FragmentPagosBinding.inflate(inflater, container, false);

        // Observar la lista de pagos
        vm.getPagosMutable().observe(getViewLifecycleOwner(), pagos -> {
            if (pagos != null) {
                if (pagos.isEmpty()) {
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                    binding.rvPagos.setVisibility(View.GONE);
                } else {
                    binding.layoutEmpty.setVisibility(View.GONE);
                    binding.rvPagos.setVisibility(View.VISIBLE);
                    adapter = new PagoAdapter(pagos, getContext(), getLayoutInflater());
                    binding.rvPagos.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.rvPagos.setAdapter(adapter);
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

        // Obtener el idContrato de los argumentos
        if (getArguments() != null && getArguments().containsKey("idContrato")) {
            int idContrato = getArguments().getInt("idContrato");
            vm.cargarPagos(idContrato);
        } else {
            binding.tvMensaje.setText("No se recibió el ID del contrato");
            binding.cardMensaje.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}