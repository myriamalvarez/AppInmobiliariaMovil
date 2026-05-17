package com.myrsoft.appinmobiliariamovil.ui.inicio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.myrsoft.appinmobiliariamovil.databinding.FragmentInicioBinding;

import org.maplibre.android.MapLibre;

public class InicioFragment extends Fragment {
    private FragmentInicioBinding binding;
    private InicioViewModel vm;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MapLibre.getInstance(requireContext());

        binding = FragmentInicioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        vm = new ViewModelProvider(this).get(InicioViewModel.class);

        vm.getMapaMutable().observe(getViewLifecycleOwner(), mapa -> {
            binding.mapa.getMapAsync(mapa);
            });
        vm.cargarMapa();
        return root;
    }
    @Override
    public void onStart() {
        super.onStart();
        binding.mapa.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapa.onResume();
    }

    @Override
    public void onPause() {
        binding.mapa.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        binding.mapa.onStop();
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapa.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        binding.mapa.onDestroy();
        super.onDestroyView();
        binding = null;
    }
}