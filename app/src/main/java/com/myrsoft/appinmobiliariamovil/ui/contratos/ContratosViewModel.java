package com.myrsoft.appinmobiliariamovil.ui.contratos;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.myrsoft.appinmobiliariamovil.modelo.Inmueble;
import com.myrsoft.appinmobiliariamovil.request.ApiClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContratosViewModel extends AndroidViewModel {
    private MutableLiveData<List<Inmueble>> inmueblesMutable = new MutableLiveData<>();
    private MutableLiveData<String> mensajeMutable = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingMutable = new MutableLiveData<>();
    private static final String TAG = "ContratosVM";

    public ContratosViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Inmueble>> getInmueblesMutable() {
        return inmueblesMutable;
    }

    public LiveData<String> getMensajeMutable() {
        return mensajeMutable;
    }

    public LiveData<Boolean> getIsLoadingMutable() {
        return isLoadingMutable;
    }

    public void cargarContratosVigentes() {
        isLoadingMutable.setValue(true);
        
        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            mensajeMutable.setValue("Debe iniciar sesión primero");
            isLoadingMutable.setValue(false);
            return;
        }
        
        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
        Call<List<Inmueble>> call = servicio.getInmueblesConContratoVigente(token);
        
        call.enqueue(new Callback<List<Inmueble>>() {
            @Override
            public void onResponse(Call<List<Inmueble>> call, Response<List<Inmueble>> response) {
                isLoadingMutable.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    inmueblesMutable.setValue(response.body());
                    if (response.body().isEmpty()) {
                        mensajeMutable.setValue("No hay contratos vigentes");
                    }
                } else {
                    if (response.code() == 401) {
                        mensajeMutable.setValue("Sesión expirada. Inicie sesión nuevamente");
                    } else {
                        mensajeMutable.setValue("Error al cargar: " + response.message());
                    }
                    Log.e(TAG, "Error: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<List<Inmueble>> call, Throwable t) {
                isLoadingMutable.setValue(false);
                mensajeMutable.setValue("Error de conexión: " + t.getMessage());
                Log.e(TAG, "Failure: ", t);
            }
        });
    }
}