package com.myrsoft.appinmobiliariamovil.ui.inicio;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.maplibre.android.annotations.MarkerOptions;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.OnMapReadyCallback;
import org.maplibre.android.maps.Style;

public class InicioViewModel extends AndroidViewModel {
    private MutableLiveData<MapaActual> mapaMutable;

    public InicioViewModel(@NonNull Application application) {
        super(application);
    }
    public LiveData<MapaActual> getMapaMutable() {
        if (mapaMutable == null) {
            mapaMutable = new MutableLiveData<>();
            mapaMutable.postValue(new MapaActual());
        }
        return mapaMutable;
    }
    public static class MapaActual implements OnMapReadyCallback {
        LatLng inmobiliaria = new LatLng(-33.184304,-66.312575);
        @Override
        public void onMapReady(@NonNull MapLibreMap mapLibreMap) {
            String styleUrl ="https://api.maptiler.com/maps/streets/style.json?key=yKKuWFZXKbmHGdFmyQap";
            mapLibreMap.setStyle(styleUrl, new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    mapLibreMap.addMarker(new MarkerOptions()
                            .position(inmobiliaria)
                            .title("Inmobiliaria La Punta")
                            .snippet("Hacé click para ver más información"));

                    CameraPosition posicionCamara = new CameraPosition.Builder()
                            .target(inmobiliaria)
                            .zoom(16)
                            .bearing(0)
                            .tilt(30)
                            .build();
                    mapLibreMap.setCameraPosition(posicionCamara);

                    mapLibreMap.getUiSettings().setCompassEnabled(true);
                    mapLibreMap.getUiSettings().setZoomGesturesEnabled(true);

                }
            });
        }
    }
    public void cargarMapa() {
        MapaActual mapa = new MapaActual();
        mapaMutable.postValue(mapa);
    }
}


