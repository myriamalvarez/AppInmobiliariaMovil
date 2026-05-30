
package com.myrsoft.appinmobiliariamovil.ui.inquilinos;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.myrsoft.appinmobiliariamovil.modelo.Contrato;
import com.myrsoft.appinmobiliariamovil.modelo.Inmueble;
import com.myrsoft.appinmobiliariamovil.modelo.Inquilino;
import com.myrsoft.appinmobiliariamovil.request.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleInquilinoViewModel extends AndroidViewModel {
    private static final String TAG = DetalleInquilinoViewModel.class.getSimpleName();
    private MutableLiveData<Contrato> contratoMutable = new MutableLiveData<>();
    private MutableLiveData<Inmueble> inmuebleMutable = new MutableLiveData<>();
    private MutableLiveData<Inquilino> inquilinoMutable = new MutableLiveData<>();
    private MutableLiveData<String> mensajeMutable = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingMutable = new MutableLiveData<>();

    public DetalleInquilinoViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Contrato> getContratoMutable() {
        return contratoMutable;
    }

    public MutableLiveData<Inmueble> getInmuebleMutable() {
        return inmuebleMutable;
    }

    public MutableLiveData<Inquilino> getInquilinoMutable() {
        return inquilinoMutable;
    }

    public MutableLiveData<String> getMensajeMutable() {
        return mensajeMutable;
    }

    public MutableLiveData<Boolean> getIsLoadingMutable() {
        return isLoadingMutable;
    }

    public void cargarDetalleInquilino(Inmueble inmueble) {
        if (inmueble != null) {
            inmuebleMutable.setValue(inmueble);
            cargarContratoPorInmueble(inmueble.getIdInmueble());
        } else {
            mensajeMutable.setValue("No se pudo cargar el inmueble");
        }
    }

    private void cargarContratoPorInmueble(int idInmueble) {
        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            mensajeMutable.setValue("Sesiòn expirada. Inicie sesiòn nuevamente");
            return;
        }

        isLoadingMutable.setValue(true);
        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
        Call<Contrato> call = servicio.getContratoPorInmueble(token, idInmueble);

        call.enqueue(new Callback<Contrato>() {
            @Override
            public void onResponse(@NonNull Call<Contrato> call, @NonNull Response<Contrato> response) {
                isLoadingMutable.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Contrato contrato = response.body();
                    contratoMutable.setValue(contrato);

                    if (contrato.getInquilino() != null) {
                        inquilinoMutable.setValue(contrato.getInquilino());
                    } else {
                        mensajeMutable.setValue("No se encontraron datos del inquilino");
                    }

                    if (contrato.getInmueble() != null) {
                        inmuebleMutable.setValue(contrato.getInmueble());
                    }
                } else {
                    if (response.code() == 401) {
                        mensajeMutable.setValue("Sesiòn expirada. Inicie sesiòn nuevamente");
                    } else if (response.code() == 404) {
                        mensajeMutable.setValue("No se pudo cargar el contrato");
                    } else {
                        mensajeMutable.setValue("No se pudo cargar el contrato: " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Contrato> call, @NonNull Throwable t) {
                isLoadingMutable.setValue(false);
                mensajeMutable.setValue("Error de conexión: " + t.getMessage());
                Log.e(TAG, "Failure: ", t);
            }
        });
    }
}
