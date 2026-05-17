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
    public void recuperarDatos(String email, String password){
        if(email.isEmpty() || password.isEmpty()){
            mensajeMutable.setValue("Por favor complete los campos");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mensajeMutable.setValue("Por favor ingrese un email válido");
            return;
        }
        if (password.length() < 6) {
            mensajeMutable.setValue("La contraseña debe tener al menos 6 caracteres");

        }else {
        //implementar interface
        ApiClient.MiServicioInmobiliaria servicio =  ApiClient.getServicio();
        Call<String> call = servicio.login(email, password);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String token = response.body();
                    ApiClient.guardarToken(context, token);
                    Log.d("token", token);
                    Intent intent = new Intent(context, MenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }else{
                    mensajeMutable.setValue("usuario y/o contraseña incorrecto");
                    Log.d("Error", response.message()); //mensaje de error que devuelve
                    Log.d("Error", response.code()+""); // muestra codigo del error
                    Log.d("Error", response.errorBody().toString()+""); //trae mensaje y codigo de eroror
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Mensaje", t.getMessage());
            }
        });

        }
    }
}
