package com.myrsoft.appinmobiliariamovil.ui.inmuebles;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.myrsoft.appinmobiliariamovil.R;
import com.myrsoft.appinmobiliariamovil.databinding.FragmentDetalleInmuebleBinding;
import com.myrsoft.appinmobiliariamovil.modelo.Inmueble;
import com.myrsoft.appinmobiliariamovil.request.ApiClient;

public class DetalleInmuebleFragment extends Fragment {

    private DetalleInmuebleViewModel vm;
    private FragmentDetalleInmuebleBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


            vm = new ViewModelProvider(this).get(DetalleInmuebleViewModel.class);
            binding = FragmentDetalleInmuebleBinding.inflate(inflater, container, false);

            // 1. OBSERVER PRINCIPAL: Carga los datos del Inmueble
            vm.getInmuebleMutable().observe(getViewLifecycleOwner(), new Observer<Inmueble>() {
                @Override
                public void onChanged(Inmueble inmuebleObjeto) {
                    if (inmuebleObjeto != null) {
                        binding.tvCodigoDetalle.setText(String.valueOf(inmuebleObjeto.getIdInmueble()));
                        binding.tvDireccionDetalle.setText(inmuebleObjeto.getDireccion());
                        binding.tvTipoDetalle.setText(inmuebleObjeto.getTipo());
                        binding.tvUsoDetalle.setText(inmuebleObjeto.getUso());
                        binding.tvAmbientesDetalle.setText(vm.formatearAmbientes(inmuebleObjeto.getAmbientes()));
                        binding.tvPrecioDetalle.setText(vm.formatearPrecio(inmuebleObjeto.getValor()));

                        // Seteamos el switch sin disparar listener de forma manual inicialmente
                        binding.switchDisponibilidad.setChecked(inmuebleObjeto.isDisponible());

                        Glide.with(requireContext())
                                .load(ApiClient.BASE_URL + inmuebleObjeto.getImagen())
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .into(binding.ivDetalleInmueble);
                    }
                }
            });

            // 2. OBSERVER DE DISPONIBILIDAD (Texto y Color)
            vm.getEstadoTextoMutable().observe(getViewLifecycleOwner(), texto -> {
                binding.tvEstadoDetalle.setText(texto);
            });

            vm.getEstadoColorMutable().observe(getViewLifecycleOwner(), color -> {
                binding.tvEstadoDetalle.setTextColor(color);
            });

            // Sincroniza el switch si cambia desde el ViewModel de forma externa
            vm.getDisponibilidadMutable().observe(getViewLifecycleOwner(), disponible -> {
                if (binding.switchDisponibilidad.isChecked() != disponible) {
                    binding.switchDisponibilidad.setChecked(disponible);
                }
            });

            // 3. OBSERVER DE MENSAJES (Alertas de error/éxito)
            vm.getMensajeMutable().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String mensaje) {
                    if (mensaje != null && !mensaje.isEmpty()) {
                        binding.tvMensaje.setText(mensaje);
                        binding.cardMensaje.setVisibility(View.VISIBLE);
                        binding.cardMensaje.postDelayed(() -> {
                            if (binding != null) binding.cardMensaje.setVisibility(View.GONE);
                        }, 3000);
                    }
                }
            });

            // 4. OBSERVER DE CARGANDO (ProgressBar)
            vm.getCargandoMutable().observe(getViewLifecycleOwner(), isLoading -> {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                binding.switchDisponibilidad.setEnabled(!isLoading);
            });

            // Listener para cerrar manualmente el mensaje flotante
            binding.ivCerrarMensaje.setOnClickListener(v -> binding.cardMensaje.setVisibility(View.GONE));

            // 5. LISTENER DEL SWITCH: Captura la acción del usuario
            binding.switchDisponibilidad.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Evaluamos isPressed() para asegurarnos que fue una pulsación real del usuario y no un cambio por código
                if (buttonView.isPressed()) {
                    vm.actualizarDisponibilidad(isChecked);
                }
            });

            // 6. DISPARADOR: Mandamos a buscar el inmueble (FUERA de los observers)
            vm.recuperarInmueble(getArguments());

            return binding.getRoot();
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }
    }
