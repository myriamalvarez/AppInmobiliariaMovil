package com.myrsoft.appinmobiliariamovil.ui.inmuebles;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.myrsoft.appinmobiliariamovil.R;
import com.myrsoft.appinmobiliariamovil.databinding.FragmentAgregarInmuebleBinding;

public class AgregarInmuebleFragment extends Fragment {

    private AgregarInmuebleViewModel vm;
    private FragmentAgregarInmuebleBinding binding;
    private Intent intent;
    private ActivityResultLauncher<Intent> launcher;
    //private static final String TAG = "AgregarInmuebleFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAgregarInmuebleBinding.inflate(inflater, container, false);

        vm = new ViewModelProvider(this).get(AgregarInmuebleViewModel.class);

        abrirGaleria();

        binding.btnSeleccionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(intent);
            }
        });
        binding.btnGuardarInmueble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "Botón guardar clickeado");
                cargarInmueble();
            }
        });
        vm.getUriMutable().observe(getViewLifecycleOwner(), new Observer<Uri>() {
                    @Override
                    public void onChanged(Uri uri) {
                        if (uri != null) {
                            //Log.d(TAG, "URI observada: " + uri.toString());
                            binding.ivFotoInmueble.setImageURI(uri);
                            Toast.makeText(getContext(), "Foto cargada correctamente", Toast.LENGTH_SHORT).show();
                        }
                    }
        });

        return binding.getRoot();

    }
    private void cargarInmueble() {
        String direccion = binding.etDireccion.getText().toString();
        String tipo = binding.etTipo.getText().toString();
        String uso = binding.etUso.getText().toString();
        String ambientes = binding.etAmbientes.getText().toString();
        String valor = binding.etPrecio.getText().toString();
        boolean disponible = binding.switchDisponible.isChecked();
        vm.guardarInmueble(direccion, tipo, uso, ambientes, valor, disponible);

    }
    private void abrirGaleria() {
        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                vm.recibirFoto(result);
                binding.btnGuardarInmueble.setEnabled(true);
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}