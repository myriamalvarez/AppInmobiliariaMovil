package com.myrsoft.appinmobiliariamovil.ui.perfil;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myrsoft.appinmobiliariamovil.R;
import com.myrsoft.appinmobiliariamovil.databinding.FragmentCambiarClaveBinding;

public class CambiarClaveFragment extends Fragment {

    private CambiarClaveViewModel vm;
    private FragmentCambiarClaveBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(CambiarClaveViewModel.class);
        binding = FragmentCambiarClaveBinding.inflate(inflater, container, false);
        binding.btnCambiarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String claveActual = binding.etPasswordActual.getText().toString().trim();
                String claveNueva = binding.etPasswordNueva.getText().toString().trim();
                vm.cambiarClave(claveActual, claveNueva);
            }
        });
        vm.getMensajeMutable().observe(getViewLifecycleOwner(), mensaje -> {
            binding.tvMensaje.setText(mensaje);
            binding.tvMensaje.setTextColor(android.graphics.Color.RED);
        });
        vm.getReLoginMutable().observe(getViewLifecycleOwner(), reLogin -> {
            if (reLogin) {
                // Creamos el intent para ir a la pantalla de Login (MainActivity)
                android.content.Intent intent = new android.content.Intent(requireContext(), com.myrsoft.appinmobiliariamovil.MainActivity.class);

                // Flags de seguridad: limpia el historial de pantallas para que no pueda volver al menú con el botón "atrás"
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);

                // Finaliza la Activity actual (MenuActivity) donde reside el Fragment
                requireActivity().finish();
            }

        });
        return binding.getRoot();

    }

}