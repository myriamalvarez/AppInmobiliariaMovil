package com.myrsoft.appinmobiliariamovil.ui.perfil;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.myrsoft.appinmobiliariamovil.modelo.Propietario;
import com.myrsoft.appinmobiliariamovil.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends AndroidViewModel {
private MutableLiveData<Propietario> propietarioMutable;
private MutableLiveData<Boolean> editableMutable;
private Context context;

    public PerfilViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }
    public LiveData<Propietario> getPropietarioMutable() {
        if (propietarioMutable == null) {
            propietarioMutable = new MutableLiveData<>();
        }
        return propietarioMutable;
    }
    public LiveData<Boolean> getEditableMutable() {
        if (editableMutable == null) {
            editableMutable = new MutableLiveData<>();
            editableMutable.postValue(false);
        }
        return editableMutable;
    }
    public void cargarPerfil() {
        String token = ApiClient.leerToken(getApplication());
        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
        Call<Propietario> call = servicio.getPropietario(token);
        call.enqueue(new Callback<Propietario>() {
            @Override
            public void onResponse(Call<Propietario> call, Response<Propietario> response) {
                if (response.isSuccessful()) {
                    Propietario p = response.body();
                    propietarioMutable.postValue(p);
                } else {
                    Log.d("Error", response.message());
                }
            }

            @Override
            public void onFailure(Call<Propietario> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }
    public void CambiarEstado(){
        Boolean editable = editableMutable.getValue();
        if (editable == null){
            editable = false;
        }
        editableMutable.postValue(!editable);
    }
    public void GuardarPropietario(Propietario p){
        String token = ApiClient.leerToken(context);
        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();

    }
}