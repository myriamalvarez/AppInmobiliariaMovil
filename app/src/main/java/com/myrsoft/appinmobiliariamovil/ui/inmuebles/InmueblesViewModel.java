package com.myrsoft.appinmobiliariamovil.ui.inmuebles;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.myrsoft.appinmobiliariamovil.modelo.Inmueble;
import com.myrsoft.appinmobiliariamovil.request.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InmueblesViewModel extends AndroidViewModel {
    private MutableLiveData<List<Inmueble>> listaMutable = new MutableLiveData<>();
    public InmueblesViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Inmueble>> getListaMutable(){
        return listaMutable;
    }

    public void obtenerInmuebles(){
        String token = ApiClient.leerToken(getApplication());
        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
        Call<List<Inmueble>> call = servicio.listarInmuebles(token);
        call.enqueue(new Callback<List<Inmueble>>() {
            @Override
            public void onResponse(Call<List<Inmueble>> call, Response<List<Inmueble>> response) {
                if (response.isSuccessful()) {
                    listaMutable.postValue(response.body());
                }else {
                    Toast.makeText(getApplication(), "No hay inmuebles para listar", Toast.LENGTH_SHORT).show();
                    Log.d("Error", response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Inmueble>> call, Throwable t) {
                Toast.makeText(getApplication(), "No se puede acceder a la API", Toast.LENGTH_SHORT).show();
                Log.d("Error", t.getMessage());
            }
        });
    }

}
