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
    private MutableLiveData<String> mensajeMutable = new MutableLiveData<>();
    private MutableLiveData<Boolean> cargandoMutable = new MutableLiveData<>();


    public InmueblesViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Inmueble>> getListaMutable(){
        return listaMutable;
    }

    public LiveData<String> getMensajeMutable(){
        if(mensajeMutable == null){
            mensajeMutable = new MutableLiveData<>();
        }
        return mensajeMutable;
    }

    public LiveData<Boolean> getCargandoMutable(){
        if(cargandoMutable == null){
            cargandoMutable = new MutableLiveData<>();
        }
        return cargandoMutable;
    }

    public void obtenerInmuebles(){
        cargandoMutable.setValue(true);
        String token = ApiClient.leerToken(getApplication());

        if(token == null){
            mensajeMutable.setValue("Debe iniciar sesión para ver los inmuebles");
            cargandoMutable.setValue(false);
            return;
        }

        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
        Call<List<Inmueble>> call = servicio.listarInmuebles(token);
        call.enqueue(new Callback<List<Inmueble>>() {
            @Override
            public void onResponse(Call<List<Inmueble>> call, Response<List<Inmueble>> response) {
                cargandoMutable.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    listaMutable.postValue(response.body());
                    if (response.body().isEmpty()) {
                        mensajeMutable.setValue("No hay inmuebles para listar");
                    }
                }else {
                    if(response.code() == 401){
                        mensajeMutable.setValue("Sesión expirada. Inicie sesión nuevamente");
                    }else{
                        mensajeMutable.setValue("Error al cargar inmuebles: " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Inmueble>> call, Throwable t) {
                cargandoMutable.setValue(false);
                mensajeMutable.setValue("Error de conexión: " + t.getMessage());
                Log.d("Error", t.getMessage());
            }
        });
    }

}
