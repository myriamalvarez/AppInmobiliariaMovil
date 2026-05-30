package com.myrsoft.appinmobiliariamovil.ui.contratos;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.myrsoft.appinmobiliariamovil.modelo.Contrato;
import com.myrsoft.appinmobiliariamovil.modelo.Inmueble;
import com.myrsoft.appinmobiliariamovil.modelo.Inquilino;
import com.myrsoft.appinmobiliariamovil.request.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleContratoViewModel extends AndroidViewModel {
    private MutableLiveData<Contrato> contratoMutable = new MutableLiveData<>();
    private MutableLiveData<Inmueble> inmuebleMutable = new MutableLiveData<>();
    private MutableLiveData<Inquilino> inquilinoMutable = new MutableLiveData<>();
    private MutableLiveData<String> mensajeMutable = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingMutable = new MutableLiveData<>();
    private MutableLiveData<String> estadoContratoMutable = new MutableLiveData<>();
    private MutableLiveData<Integer> estadoColorMutable = new MutableLiveData<>();
    private static final String TAG = "DetalleContratoVM";

    public DetalleContratoViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Contrato> getContratoMutable() { return contratoMutable; }
    public LiveData<Inmueble> getInmuebleMutable() { return inmuebleMutable; }
    public LiveData<Inquilino> getInquilinoMutable() { return inquilinoMutable; }
    public LiveData<String> getMensajeMutable() { return mensajeMutable; }
    public LiveData<Boolean> getIsLoadingMutable() { return isLoadingMutable; }
    public LiveData<String> getEstadoContratoMutable() { return estadoContratoMutable; }
    public LiveData<Integer> getEstadoColorMutable() { return estadoColorMutable; }

    public void cargarDetalleContrato(Bundle bundle) {
        if (bundle != null && bundle.containsKey("idInmueble")) {
            int idInmueble = bundle.getInt("idInmueble");
            isLoadingMutable.setValue(true);
            cargarContratoPorInmueble(idInmueble);
        } else {
            mensajeMutable.setValue("No se recibió el ID del inmueble");
        }
    }
    
    private void cargarContratoPorInmueble(int idInmueble) {
        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            mensajeMutable.setValue("Sesión expirada. Inicie sesión nuevamente");
            isLoadingMutable.setValue(false);
            return;
        }
        
        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
        Call<Contrato> call = servicio.getContratoPorInmueble(token, idInmueble);
        
        call.enqueue(new Callback<Contrato>() {
            @Override
            public void onResponse(Call<Contrato> call, Response<Contrato> response) {
                isLoadingMutable.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Contrato contrato = response.body();
                    contratoMutable.setValue(contrato);
                    
                    actualizarEstadoUI(contrato.isEstado());
                    
                    if (contrato.getInquilino() != null) {
                        inquilinoMutable.setValue(contrato.getInquilino());
                    }
                    
                    if (contrato.getInmueble() != null) {
                        inmuebleMutable.setValue(contrato.getInmueble());
                    }
                } else {
                    if (response.code() == 401) {
                        mensajeMutable.setValue("Sesión expirada");
                    } else if (response.code() == 404) {
                        mensajeMutable.setValue("No se encontró contrato para este inmueble");
                    } else {
                        mensajeMutable.setValue("Error al cargar contrato: " + response.message());
                    }
                    Log.e(TAG, "Error: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Contrato> call, Throwable t) {
                isLoadingMutable.setValue(false);
                mensajeMutable.setValue("Error de conexión: " + t.getMessage());
                Log.e(TAG, "Failure: ", t);
            }
        });
    }
    
    private void actualizarEstadoUI(boolean vigente) {
        if (vigente) {
            estadoContratoMutable.setValue("Contrato Vigente");
            estadoColorMutable.setValue(getApplication().getResources().getColor(android.R.color.holo_green_dark));
        } else {
            estadoContratoMutable.setValue("Contrato Finalizado");
            estadoColorMutable.setValue(getApplication().getResources().getColor(android.R.color.holo_red_dark));
        }
    }
    
    public String formatearPrecio(double precio) {
        return "$ " + String.format("%,.0f", precio);
    }
    
    public String formatearTipoUso(Inmueble inmueble) {
        if (inmueble == null) return "";
        return inmueble.getTipo() + " - " + inmueble.getUso();
    }
    
    public int getIdContrato() {
        Contrato contrato = contratoMutable.getValue();
        return contrato != null ? contrato.getIdContrato() : -1;
    }
}