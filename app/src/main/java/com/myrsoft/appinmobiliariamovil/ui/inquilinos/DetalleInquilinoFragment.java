
package com.myrsoft.appinmobiliariamovil.ui.inquilinos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.myrsoft.appinmobiliariamovil.databinding.FragmentDetalleInquilinoBinding;
import com.myrsoft.appinmobiliariamovil.modelo.Inmueble;
import com.myrsoft.appinmobiliariamovil.modelo.Inquilino;

public class DetalleInquilinoFragment extends Fragment {

    private DetalleInquilinoViewModel vm;
    private FragmentDetalleInquilinoBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDetalleInquilinoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        vm = new ViewModelProvider(this).get(DetalleInquilinoViewModel.class);

        vm.getInquilinoMutable().observe(getViewLifecycleOwner(), this::cargarDatosInquilino);
        vm.getInmuebleMutable().observe(getViewLifecycleOwner(), this::cargarDatosInmueble);

        vm.getMensajeMutable().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                binding.tvMensaje.setText(mensaje);
                binding.cardMensaje.setVisibility(View.VISIBLE);
                binding.cardMensaje.postDelayed(() -> binding.cardMensaje.setVisibility(View.GONE), 3000);
            }
        });

        binding.ivCerrarMensaje.setOnClickListener(v -> binding.cardMensaje.setVisibility(View.GONE));

        if (getArguments() != null && getArguments().containsKey("inmueble")) {
            Inmueble inmueble = (Inmueble) getArguments().getSerializable("inmueble");
            vm.cargarDetalleInquilino(inmueble);
        } else {
            binding.tvMensaje.setText("No se recibieron datos del inmueble");
            binding.cardMensaje.setVisibility(View.VISIBLE);
        }

        return root;
    }

    private void cargarDatosInquilino(Inquilino inquilino) {
        if (inquilino == null) return;

        binding.tvNombreCompleto.setText(inquilino.getNombre() + " " + inquilino.getApellido());
        binding.tvDni.setText("DNI: " + (inquilino.getDni() != null ? inquilino.getDni() : "No especificado"));
        binding.tvTelefono.setText("Teléfono: " + (inquilino.getTelefono() != null ? inquilino.getTelefono() : "No especificado"));
        binding.tvEmail.setText("Email: " + (inquilino.getEmail() != null ? inquilino.getEmail() : "No especificado"));
    }

    private void cargarDatosInmueble(Inmueble inmueble) {
        if (inmueble == null) return;

        binding.tvDireccionInmueble.setText(inmueble.getDireccion());
        binding.tvTipoInmueble.setText(inmueble.getTipo() + " - " + inmueble.getUso() + " - " + inmueble.getAmbientes() + " ambientes");
        binding.tvMontoAlquiler.setText("$ " + String.format("%,.0f", inmueble.getValor()) + " mensuales");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
