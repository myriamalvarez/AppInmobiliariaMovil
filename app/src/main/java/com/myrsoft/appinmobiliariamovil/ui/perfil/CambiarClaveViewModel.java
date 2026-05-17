package com.myrsoft.appinmobiliariamovil.ui.perfil;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.myrsoft.appinmobiliariamovil.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CambiarClaveViewModel extends AndroidViewModel {
    private MutableLiveData<String> mensajeMutable;
    private MutableLiveData<Boolean> reLoginMutable = new MutableLiveData<>();

    public CambiarClaveViewModel(@NonNull Application application) {
        super(application);
    }
    public LiveData<String> getMensajeMutable() {
        if (mensajeMutable == null) {
            mensajeMutable = new MutableLiveData<>();
        }
        return mensajeMutable;
    }

    public LiveData<Boolean> getReLoginMutable() {
        return reLoginMutable;
    }
    public void cambiarClave(String actual, String nueva) {
        if (actual.trim().isEmpty() || nueva.trim().isEmpty()) {
            mensajeMutable.setValue("Por favor complete los campos");
            return;
        }
        if (nueva.length() < 6) {
            mensajeMutable.setValue("La contraseña nueva debe tener al menos 6 caracteres");
            return;
        }
        if (actual.equals(nueva)) {
            mensajeMutable.setValue("Las contraseñas no pueden ser iguales");
            return;
        }

        String token = ApiClient.leerToken(getApplication());
        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();

        Call<Void> call = servicio.cambiarPassword(token, actual, nueva);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    //Toast.makeText(getApplication(), "Cambio exitoso", Toast.LENGTH_SHORT).show();
                    mensajeMutable.postValue("Contraseña cambiada con éxito");
                    reLoginMutable.postValue(true);
            }else{
                    //Toast.makeText(getApplication(), "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show();
                    mensajeMutable.postValue("Error al cambiar la contraseña");
                }
        }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplication(), "Error del servidor", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
