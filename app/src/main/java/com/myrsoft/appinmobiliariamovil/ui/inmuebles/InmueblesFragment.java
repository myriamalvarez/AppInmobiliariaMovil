package com.myrsoft.appinmobiliariamovil.ui.inmuebles;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myrsoft.appinmobiliariamovil.R;
import com.myrsoft.appinmobiliariamovil.databinding.FragmentInmueblesBinding;
import com.myrsoft.appinmobiliariamovil.modelo.Inmueble;

import java.util.List;

public class InmueblesFragment extends Fragment {
    private InmueblesViewModel vm;
    private FragmentInmueblesBinding binding;

    public static InmueblesFragment newInstance() {

        return new InmueblesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(InmueblesViewModel.class);
        binding = FragmentInmueblesBinding.inflate(inflater, container, false);

        vm.getListaMutable().observe(getViewLifecycleOwner(), new Observer<List<Inmueble>>(){

            @Override
            public void onChanged(List<Inmueble> inmuebles) {
                InmuebleAdapter adapter = new InmuebleAdapter(inmuebles, getLayoutInflater());
                binding.rvInmuebles.setAdapter(adapter);
                GridLayoutManager glm = new GridLayoutManager(getContext(),2, GridLayoutManager.VERTICAL, false);
                binding.rvInmuebles.setLayoutManager(glm);

            }
        });
        vm.obtenerInmuebles();
        return binding.getRoot();
    }



}