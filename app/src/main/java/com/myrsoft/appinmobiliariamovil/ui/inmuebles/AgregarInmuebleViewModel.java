package com.myrsoft.appinmobiliariamovil.ui.inmuebles;

import static android.app.Activity.RESULT_OK;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.myrsoft.appinmobiliariamovil.modelo.Inmueble;
import com.myrsoft.appinmobiliariamovil.request.ApiClient;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarInmuebleViewModel extends AndroidViewModel {
    private MutableLiveData<Uri> uriMutable;
    private static final String TAG = "AgregarInmueble";

    public AgregarInmuebleViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Uri> getUriMutable() {
        if (uriMutable == null) {
            uriMutable = new MutableLiveData<>();
        }
        return uriMutable;
    }

    public void recibirFoto(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                Toast.makeText(getApplication(), "Foto seleccionada", Toast.LENGTH_SHORT).show();
                uriMutable.postValue(uri);
            } else {
                Toast.makeText(getApplication(), "Error", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplication(), "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show();
        }
    }
    public void guardarInmueble(String direccion, String tipo, String uso, String ambientes, String valor, boolean disponible) {

        if (direccion.isEmpty() || tipo.isEmpty() || uso.isEmpty() || ambientes.isEmpty() || valor.isEmpty()) {
            Toast.makeText(getApplication(), "Todos los campos son obligatorios", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            int amb = Integer.parseInt(ambientes);
            double val = Double.parseDouble(valor);

            Inmueble nuevoInmueble = new Inmueble();
            nuevoInmueble.setDireccion(direccion);
            nuevoInmueble.setTipo(tipo);
            nuevoInmueble.setUso(uso);
            nuevoInmueble.setAmbientes(amb);
            nuevoInmueble.setValor(val);
            nuevoInmueble.setDisponible(disponible);

            // TOAST 3: Inmueble creado
            Toast.makeText(getApplication(), "Inmueble creado: " + direccion, Toast.LENGTH_SHORT).show();

            byte[] imagen = transformarImagen();
            if (imagen.length == 0) {
                Toast.makeText(getApplication(), "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            String inmuebleJson = new Gson().toJson(nuevoInmueble);

            RequestBody inmuebleBody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), inmuebleJson);
            RequestBody imagenBody = RequestBody.create(MediaType.parse("image/jpeg"), imagen);
            MultipartBody.Part imagenPart = MultipartBody.Part.createFormData("imagen", "imagen.jpg", imagenBody);

            ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
            String token = ApiClient.leerToken(getApplication());
            Call<Inmueble> call = servicio.agregarInmueble(token, imagenPart, inmuebleBody);
            call.enqueue(new Callback<Inmueble>() {
                @Override
                public void onResponse(Call<Inmueble> call, Response<Inmueble> response) {
                    if (response.isSuccessful()) {
                        Inmueble inmuebleNuevo = response.body();
                        if (inmuebleNuevo != null) {
                            Toast.makeText(getApplication(), "Inmueble agregado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplication(), "Error al crear el inmueble", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                            String errorBody = "";
                            try {
                                errorBody = response.errorBody().string();
                                Toast.makeText(getApplication(), "Error al agregar el inmueble: " + errorBody, Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                Toast.makeText(getApplication(), "Error al agregar el inmueble", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(getApplication(), " Error: " + response.code() + " - " + response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                @Override
                public void onFailure(Call<Inmueble> call, Throwable t) {
                        Toast.makeText(getApplication(), " Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }catch (NumberFormatException e) {
            Toast.makeText(getApplication(), "Debe ingresar números en ambientes y precio", Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            Toast.makeText(getApplication(), " Error inesperado: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private byte[] transformarImagen() {
            try {
                Uri uri = uriMutable.getValue();
                if (uri == null) {
                    Toast.makeText(getApplication(), "URI de imagen es nula", Toast.LENGTH_SHORT).show();
                    return new byte[]{};
                }
                InputStream inputStream = getApplication().getContentResolver().openInputStream(uri);
                if (inputStream == null) {
                    Toast.makeText(getApplication(), "No se pudo abrir la imagen", Toast.LENGTH_SHORT).show();
                    return new byte[]{};
                }

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap == null) {
                    Toast.makeText(getApplication(), "No se pudo decodificar la imagen", Toast.LENGTH_SHORT).show();
                    return new byte[]{};
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                return imageBytes;

            } catch (FileNotFoundException e) {
                Toast.makeText(getApplication(), "Archivo de imagen no encontrado", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "FileNotFoundException: ", e);
                return new byte[]{};
            } catch (Exception e) {
                Toast.makeText(getApplication(), "Error al procesar imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error transformarImagen: ", e);
                return new byte[]{};
            }
    }
}