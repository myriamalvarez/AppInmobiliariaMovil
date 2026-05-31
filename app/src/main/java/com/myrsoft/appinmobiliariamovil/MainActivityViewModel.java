package com.myrsoft.appinmobiliariamovil;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.myrsoft.appinmobiliariamovil.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends AndroidViewModel {
    private Context context;
    private MutableLiveData<String> mensajeMutable;
    private MutableLiveData<String> mensajeResetMutable;
    private MutableLiveData<Boolean> resetExitosoMutable;
    private static final String TAG = "MainActivityVM";

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    public LiveData<String> getMensajeMutable() {
        if (mensajeMutable == null) {
            mensajeMutable = new MutableLiveData<>();
        }
        return mensajeMutable;
    }

    public LiveData<String> getMensajeResetMutable() {
        if (mensajeResetMutable == null) {
            mensajeResetMutable = new MutableLiveData<>();
        }
        return mensajeResetMutable;
    }

    public LiveData<Boolean> getResetExitosoMutable() {
        if (resetExitosoMutable == null) {
            resetExitosoMutable = new MutableLiveData<>();
        }
        return resetExitosoMutable;
    }

    public void recuperarDatos(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            mensajeMutable.setValue("Por favor complete los campos");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mensajeMutable.setValue("Por favor ingrese un email válido");
            return;
        }
        if (password.length() < 6) {
            mensajeMutable.setValue("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
        Call<String> call = servicio.login(email, password);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body();
                    ApiClient.guardarToken(context, token);
                    Log.d("token", token);
                    Intent intent = new Intent(context, MenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    mensajeMutable.setValue("Usuario y/o contraseña incorrecto");
                    Log.d("Error", response.message());
                    Log.d("Error", response.code() + "");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mensajeMutable.setValue("🌐 Error de conexión: " + t.getMessage());
                Log.d("Mensaje", t.getMessage());
            }
        });
    }

    public void resetearPassword(String email) {
        if (email.isEmpty()) {
            mensajeResetMutable.setValue("Por favor ingrese su email");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mensajeResetMutable.setValue("Por favor ingrese un email válido");
            return;
        }

        mensajeResetMutable.setValue("Enviando solicitud...");

        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
        Call<String> call = servicio.resetearContrasena(email);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String respuesta = response.body();
                    if (respuesta != null && respuesta.contains("exitoso") || respuesta.contains("enviado")) {
                        mensajeResetMutable.setValue("Se han enviado instrucciones a su email");
                    } else {
                        mensajeResetMutable.setValue("Solicitud recibida. Revise su email");
                    }
                    resetExitosoMutable.setValue(true);
                    Log.d(TAG, "Reset exitoso: " + respuesta);
                } else {
                    if (response.code() == 404) {
                        mensajeResetMutable.setValue("Email no registrado en el sistema");
                    } else {
                        mensajeResetMutable.setValue("Error al enviar solicitud: " + response.message());
                    }
                    Log.e(TAG, "Error reset: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mensajeResetMutable.setValue("Error de conexión: " + t.getMessage());
                Log.e(TAG, "Failure reset: ", t);
            }
        });
    }
}