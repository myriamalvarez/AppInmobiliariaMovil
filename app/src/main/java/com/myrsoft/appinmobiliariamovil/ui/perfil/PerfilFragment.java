package com.myrsoft.appinmobiliariamovil.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.myrsoft.appinmobiliariamovil.R;
import com.myrsoft.appinmobiliariamovil.databinding.FragmentPerfilBinding;
import com.myrsoft.appinmobiliariamovil.modelo.Propietario;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel vm;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(PerfilViewModel.class);

        vm.getPropietarioMutable().observe(getViewLifecycleOwner(),new Observer<Propietario>() {

            @Override
            public void onChanged(Propietario propietario) {
                binding.etNombre.setText(propietario.getNombre());
                binding.etApellido.setText(propietario.getApellido());
                binding.etDni.setText(propietario.getDni());
                binding.etEmail.setText(propietario.getEmail());
               // binding.etPassword.setText(propietario.getClave());
                binding.etTelefono.setText(propietario.getTelefono());
            }
        });
        vm.cargarPerfil();

        vm.getEditableMutable().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.etNombre.setEnabled(aBoolean);
                binding.etApellido.setEnabled(aBoolean);
                binding.etDni.setEnabled(aBoolean);
                binding.etEmail.setEnabled(aBoolean);
                //binding.etPassword.setEnabled(aBoolean);
                binding.etTelefono.setEnabled(aBoolean);
            }
        });

        vm.getBotonMutable().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.btnEditar.setText(s);
            }
        });

        binding.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vm.CambiarEstado(binding.btnEditar.getText().toString(),
                        binding.etNombre.getText().toString(),
                        binding.etApellido.getText().toString(),
                        binding.etDni.getText().toString(),
                        binding.etEmail.getText().toString(),
                        binding.etTelefono.getText().toString());
            }
        });
        vm.getMensajeMutable().observe(getViewLifecycleOwner(), mensaje -> {
            binding.tvMensaje.setText(mensaje);
            binding.tvMensaje.setTextColor(android.graphics.Color.RED);
        });

        binding.btnCambiarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("propietario",vm.getPropietarioMutable().getValue());
                Navigation.findNavController(v).navigate(R.id.action_nav_perfil_to_cambiarClaveFragment,bundle);
            }
        });

        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}