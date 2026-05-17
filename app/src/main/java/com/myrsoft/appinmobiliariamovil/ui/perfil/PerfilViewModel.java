package com.myrsoft.appinmobiliariamovil.ui.perfil;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
private MutableLiveData<String> mensajeMutable;
private MutableLiveData<Boolean> editableMutable = new MutableLiveData<>();
private MutableLiveData<String> botonMutable = new MutableLiveData<>();
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
    public LiveData<String> getMensajeMutable() {
        if (mensajeMutable == null) {
            mensajeMutable = new MutableLiveData<>();
        }
        return mensajeMutable;
    }
    public LiveData<Boolean> getEditableMutable() {
        return editableMutable;
    }

    public LiveData<String> getBotonMutable() {
        return botonMutable;
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
                    mensajeMutable.postValue("Error al cargar el perfil");
                }
            }

            @Override
            public void onFailure(Call<Propietario> call, Throwable t) {
                mensajeMutable.postValue("No se puede acceder a la API");
            }
        });
    }
    public void CambiarEstado(String nombreBoton, String nombre, String apellido, String dni, String mail, String telefono){
        if (nombreBoton.equalsIgnoreCase("editar")) {
            editableMutable.setValue(true);
            botonMutable.setValue("Guardar");
        }else if (nombreBoton.equalsIgnoreCase("guardar")) {

            // Validar que realmente tengamos el objeto base cargado para evitar crasheos
            if (propietarioMutable.getValue() == null) {
                mensajeMutable.setValue("Error de sincronización, intente nuevamente");
                return;
            }

            if (nombre.trim().isEmpty() || apellido.trim().isEmpty() ||
                    dni.trim().isEmpty() || mail.trim().isEmpty() || telefono.trim().isEmpty()) {
                mensajeMutable.setValue("Ningún campo puede quedar vacío");
                return;
            }

            if (nombre.trim().isEmpty() || apellido.trim().isEmpty() ||
                    dni.trim().isEmpty() || mail.trim().isEmpty() || telefono.trim().isEmpty()) {
                mensajeMutable.setValue("Ningún campo puede quedar vacío");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail.trim()).matches()) {
                mensajeMutable.setValue("Por favor, ingrese un email válido");
                return;
            }

            if (!dni.trim().matches("\\d+") || dni.trim().length() < 7 || dni.trim().length() > 9) {
                mensajeMutable.setValue("El DNI debe contener solo números (entre 7 y 9 dígitos)");
                return;
            }

            if (telefono.trim().length() < 6) {
                mensajeMutable.setValue("Por favor, ingrese un número de teléfono válido");
                return;
            }

            editableMutable.setValue(false);
            botonMutable.setValue("Editar");
            Propietario nuevo = new Propietario();
            nuevo.setIdPropietario(propietarioMutable.getValue().getIdPropietario());
            nuevo.setNombre(nombre);
            nuevo.setApellido(apellido);
            nuevo.setDni(dni);
            nuevo.setEmail(mail);
            nuevo.setTelefono(telefono);
            nuevo.setClave(null);
            String token = ApiClient.leerToken(getApplication());
            ApiClient.MiServicioInmobiliaria api = ApiClient.getServicio();
            Call<Propietario> call = api.actualizarProp(token, nuevo);
            call.enqueue(new Callback<Propietario>() {
                @Override
                public void onResponse(Call<Propietario> call, Response<Propietario> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(getApplication(), "Datos guardados con éxito", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplication(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                        //Log.d
                    }
                }

                @Override
                public void onFailure(Call<Propietario> call, Throwable throwable) {
                    Toast.makeText(getApplication(), "No se puede acceder a la API", Toast.LENGTH_SHORT).show();
                    //Log.e
                }
            });
        }

    }
}