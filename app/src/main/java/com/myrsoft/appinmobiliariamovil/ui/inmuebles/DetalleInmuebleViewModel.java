package com.myrsoft.appinmobiliariamovil.ui.inmuebles;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.myrsoft.appinmobiliariamovil.R;
import com.myrsoft.appinmobiliariamovil.modelo.Inmueble;
import com.myrsoft.appinmobiliariamovil.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleInmuebleViewModel extends AndroidViewModel {
    private MutableLiveData<Inmueble> inmuebleMutable = new MutableLiveData<>();
    private MutableLiveData<String> mensajeMutable = new MutableLiveData<>();
    private MutableLiveData<Boolean> cargandoMutable = new MutableLiveData<>();
    private MutableLiveData<Boolean> disponibilidadMutable = new MutableLiveData<>();
    private MutableLiveData<Integer> estadoColorMutable = new MutableLiveData<>();
    private MutableLiveData<String> estadoTextoMutable = new MutableLiveData<>();

    private Inmueble inmueble;

    public DetalleInmuebleViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Inmueble> getInmuebleMutable() { return inmuebleMutable; }
    public LiveData<String> getMensajeMutable() { return mensajeMutable; }
    public LiveData<Boolean> getCargandoMutable() { return cargandoMutable; }
    public LiveData<Boolean> getDisponibilidadMutable() { return disponibilidadMutable; }
    public LiveData<Integer> getEstadoColorMutable() { return estadoColorMutable; }
    public LiveData<String> getEstadoTextoMutable() { return estadoTextoMutable; }

    public void recuperarInmueble(Bundle bundle) {
        if (bundle != null) {
            inmueble = (Inmueble) bundle.getSerializable("inmueble");
            if (inmueble != null) {
                inmuebleMutable.setValue(inmueble);
                disponibilidadMutable.setValue(inmueble.isDisponible());
                actualizarEstadoUI(inmueble.isDisponible());
            } else {
                mensajeMutable.setValue("Error: No se recibieron los datos del inmueble");
            }
        }
    }

    public void actualizarDisponibilidad(boolean nuevoEstado) {
        if (inmueble == null) return;

        boolean estadoAnterior = inmueble.isDisponible();
        cargandoMutable.setValue(true);

        inmueble.setDisponible(nuevoEstado);

        String token = ApiClient.leerToken(getApplication());
        ApiClient.MiServicioInmobiliaria api = ApiClient.getServicio();

        Call<Inmueble> call = api.actualizarInmueble(token, inmueble);
        call.enqueue(new Callback<Inmueble>() {
            @Override
            public void onResponse(Call<Inmueble> call, Response<Inmueble> response) {
                cargandoMutable.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    inmueble = response.body(); // Actualizamos el objeto local con la respuesta de la API
                    inmuebleMutable.setValue(inmueble);
                    disponibilidadMutable.setValue(inmueble.isDisponible());
                    actualizarEstadoUI(inmueble.isDisponible());
                    mensajeMutable.setValue("Estado actualizado con éxito");
                } else {
                    // Revertimos el estado si falló
                    inmueble.setDisponible(estadoAnterior);
                    disponibilidadMutable.setValue(estadoAnterior);
                    actualizarEstadoUI(estadoAnterior);
                    if (response.code() == 401) {
                        mensajeMutable.setValue("Sesión expirada. Inicie sesión nuevamente");
                    } else {
                        mensajeMutable.setValue("Error al actualizar la disponibilidad");
                    }
                }
            }

            @Override
            public void onFailure(Call<Inmueble> call, Throwable t) {
                cargandoMutable.setValue(false);
                disponibilidadMutable.setValue(estadoAnterior);
                actualizarEstadoUI(estadoAnterior);
                mensajeMutable.setValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void actualizarEstadoUI(boolean disponible) {
        if (disponible) {
            estadoTextoMutable.setValue("✓ Disponible");
            estadoColorMutable.setValue(getApplication().getResources().getColor(R.color.disponible));
        } else {
            estadoTextoMutable.setValue("✗ No disponible");
            estadoColorMutable.setValue(getApplication().getResources().getColor(R.color.no_disponible));
        }
    }

    public String formatearPrecio(double valor) {
        return "$" + String.format("%,.2f", valor);
    }

    public String formatearAmbientes(int ambientes) {
        return ambientes + " ambientes";
    }
}