package com.myrsoft.appinmobiliariamovil.request;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myrsoft.appinmobiliariamovil.modelo.Inmueble;
import com.myrsoft.appinmobiliariamovil.modelo.Propietario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public class ApiClient {
    public final static String BASE_URL = "https://capacitacion.alwaysdata.net/";
    public static MiServicioInmobiliaria getServicio() {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(MiServicioInmobiliaria.class);
    }
    public interface MiServicioInmobiliaria {
        @FormUrlEncoded
        @POST("api/Propietarios/login")
        Call<String> login(@Field("Usuario") String usuario, @Field("Clave") String clave);

        //llamarpropietario
        @GET("api/Propietarios")
        Call<Propietario> getPropietario(@Header("Authorization") String token);


        @PUT("api/Propietarios/actualizar")
        Call<Propietario> actualizarProp(@Header("Authorization") String token, @Body Propietario p);

        @FormUrlEncoded
        @PUT("api/Propietarios/changePassword")
        Call<Void> cambiarPassword(@Header("Authorization") String token,@Field("currentPassword") String actual, @Field("newPassword") String nueva);

        @FormUrlEncoded
        @POST("api/Propietarios/email")
        Call<String> resetearContrasena(@Field("email") String email);

        @GET("api/Auth/Inmuebles")
        Call<List<Inmueble>> listarInmuebles(@Header("Authorization") String token);

    }
    public static void guardarToken(Context context, String token) {
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", "Bearer "+ token);
        editor.apply();
    }
    public static String leerToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        return sp.getString("token", null);
    }


}
