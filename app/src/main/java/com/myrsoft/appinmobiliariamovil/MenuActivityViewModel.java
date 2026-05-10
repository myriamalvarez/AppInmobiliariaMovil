package com.myrsoft.appinmobiliariamovil;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.myrsoft.appinmobiliariamovil.modelo.Propietario;
import com.myrsoft.appinmobiliariamovil.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivityViewModel extends AndroidViewModel {
    private Context context;
    private MutableLiveData<Propietario> propietarioMutable;
    public MenuActivityViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }
    public LiveData<Propietario> getPropietarioMutable(){
        if(propietarioMutable == null){
            propietarioMutable = new MutableLiveData<>();
        }
        return propietarioMutable;
    }
    public void LeerUsuario() {
        String token =ApiClient.leerToken(context);
        Log.d("Token", token);
        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
        Call<Propietario> call = servicio.getPropietario(token);
        call.enqueue(new Callback<Propietario>(){
            @Override
            public void onResponse(Call<Propietario> call, Response<Propietario> response) {
                if (response.isSuccessful()) {
                    Propietario p = response.body();
                    propietarioMutable.postValue(p);
            }else{
                    Log.d("Error", response.message());
                }
        }

            @Override
            public void onFailure(Call<Propietario> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }

        });
    }
}
