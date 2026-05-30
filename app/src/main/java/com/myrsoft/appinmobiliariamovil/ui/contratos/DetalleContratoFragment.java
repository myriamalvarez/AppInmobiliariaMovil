package com.myrsoft.appinmobiliariamovil.ui.contratos;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.myrsoft.appinmobiliariamovil.R;
import com.myrsoft.appinmobiliariamovil.databinding.FragmentDetalleContratoBinding;
import com.myrsoft.appinmobiliariamovil.modelo.Contrato;
import com.myrsoft.appinmobiliariamovil.modelo.Inmueble;
import com.myrsoft.appinmobiliariamovil.modelo.Inquilino;

public class DetalleContratoFragment extends Fragment {

    private DetalleContratoViewModel vm;
    private FragmentDetalleContratoBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(DetalleContratoViewModel.class);
        binding = FragmentDetalleContratoBinding.inflate(inflater, container, false);

        // Observar inmueble
        vm.getInmuebleMutable().observe(getViewLifecycleOwner(), this::cargarDatosInmueble);
        
        // Observar inquilino
        vm.getInquilinoMutable().observe(getViewLifecycleOwner(), this::cargarDatosInquilino);
        
        // Observar contrato
        vm.getContratoMutable().observe(getViewLifecycleOwner(), this::cargarDatosContrato);
        
        // Observar estado del contrato
        vm.getEstadoContratoMutable().observe(getViewLifecycleOwner(), 
            texto -> binding.tvEstadoContrato.setText(texto));
        
        vm.getEstadoColorMutable().observe(getViewLifecycleOwner(), 
            color -> binding.tvEstadoContrato.setTextColor(color));
        
        // Observar mensajes
        vm.getMensajeMutable().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                binding.tvMensaje.setText(mensaje);
                binding.cardMensaje.setVisibility(View.VISIBLE);
                binding.cardMensaje.postDelayed(() -> binding.cardMensaje.setVisibility(View.GONE), 3000);
            }
        });
        
        // Observar loading
        vm.getIsLoadingMutable().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        
        // Botón para ver pagos
        binding.btnVerPagos.setOnClickListener(v -> {
            int idContrato = vm.getIdContrato();
            if (idContrato != -1) {
                Bundle bundle = new Bundle();
                bundle.putInt("idContrato", idContrato);
                Navigation.findNavController(v).navigate(R.id.pagosFragment, bundle);
            } else {
                binding.tvMensaje.setText("No se puede cargar el historial de pagos");
                binding.cardMensaje.setVisibility(View.VISIBLE);
                binding.cardMensaje.postDelayed(() -> binding.cardMensaje.setVisibility(View.GONE), 3000);
            }
        });
        
        binding.ivCerrarMensaje.setOnClickListener(v -> binding.cardMensaje.setVisibility(View.GONE));
        
        vm.cargarDetalleContrato(getArguments());
        return binding.getRoot();
    }
    
    private void cargarDatosInmueble(Inmueble inmueble) {
        if (inmueble == null) return;
        
        binding.tvDireccion.setText(inmueble.getDireccion());
        binding.tvTipoUso.setText(vm.formatearTipoUso(inmueble));
        binding.tvAmbientes.setText(inmueble.getAmbientes() + " ambientes");
    }
    
    private void cargarDatosInquilino(Inquilino inquilino) {
        if (inquilino == null) return;
        
        binding.tvNombreInquilino.setText(inquilino.getNombre() + " " + inquilino.getApellido());
        binding.tvDniInquilino.setText("DNI: " + (inquilino.getDni() != null ? inquilino.getDni() : "No especificado"));
        binding.tvTelefonoInquilino.setText("Tel: " + (inquilino.getTelefono() != null ? inquilino.getTelefono() : "No especificado"));
        binding.tvEmailInquilino.setText("Email: " + (inquilino.getEmail() != null ? inquilino.getEmail() : "No especificado"));
    }
    
    private void cargarDatosContrato(Contrato contrato) {
        if (contrato == null) return;
        
        binding.tvNumeroContrato.setText("Contrato N°: " + contrato.getIdContrato());
        binding.tvMontoMensual.setText(vm.formatearPrecio(contrato.getMontoAlquiler()) + " mensuales");
        // Las fechas vienen como String directamente de la API
        binding.tvFechaInicio.setText("Inicio: " + (contrato.getFechaInicio() != null ? contrato.getFechaInicio() : "No especificada"));
        binding.tvFechaFin.setText("Fin: " + (contrato.getFechaFinalizacion() != null ? contrato.getFechaFinalizacion() : "No especificada"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}