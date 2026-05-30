package com.myrsoft.appinmobiliariamovil.ui.pagos;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.myrsoft.appinmobiliariamovil.modelo.Pago;
import com.myrsoft.appinmobiliariamovil.request.ApiClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagosViewModel extends AndroidViewModel {
    private MutableLiveData<List<Pago>> pagosMutable = new MutableLiveData<>();
    private MutableLiveData<String> mensajeMutable = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingMutable = new MutableLiveData<>();
    private static final String TAG = "PagosVM";

    public PagosViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Pago>> getPagosMutable() {
        return pagosMutable;
    }

    public LiveData<String> getMensajeMutable() {
        return mensajeMutable;
    }

    public LiveData<Boolean> getIsLoadingMutable() {
        return isLoadingMutable;
    }

    public void cargarPagos(int idContrato) {
        isLoadingMutable.setValue(true);
        
        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            mensajeMutable.setValue("Debe iniciar sesión primero");
            isLoadingMutable.setValue(false);
            return;
        }
        
        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
        Call<List<Pago>> call = servicio.getPagosPorContrato(token, idContrato);
        
        call.enqueue(new Callback<List<Pago>>() {
            @Override
            public void onResponse(Call<List<Pago>> call, Response<List<Pago>> response) {
                isLoadingMutable.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    pagosMutable.setValue(response.body());
                    if (response.body().isEmpty()) {
                        mensajeMutable.setValue("No hay pagos registrados para este contrato");
                    }
                } else {
                    if (response.code() == 401) {
                        mensajeMutable.setValue("Sesión expirada. Inicie sesión nuevamente");
                    } else if (response.code() == 404) {
                        mensajeMutable.setValue("No se encontraron pagos para este contrato");
                    } else {
                        mensajeMutable.setValue("Error al cargar pagos: " + response.message());
                    }
                    Log.e(TAG, "Error: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<List<Pago>> call, Throwable t) {
                isLoadingMutable.setValue(false);
                mensajeMutable.setValue("Error de conexión: " + t.getMessage());
                Log.e(TAG, "Failure: ", t);
            }
        });
    }
}