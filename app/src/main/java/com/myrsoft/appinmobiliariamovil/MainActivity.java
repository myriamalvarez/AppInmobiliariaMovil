package com.myrsoft.appinmobiliariamovil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.myrsoft.appinmobiliariamovil.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private ActivityMainBinding binding;
    private MainActivityViewModel vm;
    private TextView tvOlvidePassword;
    
    // Sensores para detectar agitación
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final int SHAKE_THRESHOLD = 15;
    private static final long SHAKE_INTERVAL = 1000;
    private long lastShakeTime = 0;
    private static final String TELEFONO_INMOBILIARIA = "2664123456"; 
    private static final int PERMISO_LLAMADA = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        vm = ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()).create(MainActivityViewModel.class);

        // Solicitar permiso de llamada si no lo tiene
        verificarPermisoLlamada();

        // Inicializar sensores
        inicializarSensores();

        tvOlvidePassword = binding.tvOlvidePassword;

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etUsuario.getText().toString();
            String password = binding.etPassword.getText().toString();
            vm.recuperarDatos(email, password);
        });

        tvOlvidePassword.setOnClickListener(v -> mostrarDialogoResetearPassword());

        vm.getMensajeMutable().observe(this, mensaje -> binding.tvMensaje.setText(mensaje));
        vm.getMensajeResetMutable().observe(this, mensaje -> binding.tvMensaje.setText(mensaje));
    }

    private void verificarPermisoLlamada() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.CALL_PHONE}, PERMISO_LLAMADA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_LLAMADA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.tvMensaje.setText("Permiso de llamada concedido");
            } else {
                binding.tvMensaje.setText("Permiso de llamada denegado. No podrá llamar desde la app");
            }
        }
    }

    private void inicializarSensores() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                binding.tvMensaje.setText("Acelerómetro no disponible");
            }
        } else {
            binding.tvMensaje.setText("Sensor Manager no disponible");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            detectarAgitacion(event);
        }
    }

    private void detectarAgitacion(SensorEvent event) {
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastShakeTime < SHAKE_INTERVAL) {
            return;
        }

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float aceleracion = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        
        if (aceleracion >= SHAKE_THRESHOLD) {
            lastShakeTime = currentTime;
            realizarLlamada();
        }
    }

    private void realizarLlamada() {
        // Verificar permiso antes de llamar
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) 
                == PackageManager.PERMISSION_GRANTED) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + TELEFONO_INMOBILIARIA));
                startActivity(intent);
                binding.tvMensaje.setText("Llamando a la inmobiliaria...");
            } catch (SecurityException e) {
                binding.tvMensaje.setText("Error al realizar la llamada");
            }
        } else {
            binding.tvMensaje.setText("Permiso de llamada no concedido");
            verificarPermisoLlamada();
        }
    }

    private void mostrarDialogoResetearPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recuperar contraseña");
        builder.setMessage("Ingrese su email para recibir instrucciones de recuperación");

        final android.widget.EditText inputEmail = new android.widget.EditText(this);
        inputEmail.setHint("correo@ejemplo.com");
        inputEmail.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(inputEmail);

        builder.setPositiveButton("Enviar", (dialog, which) -> {
            String email = inputEmail.getText().toString().trim();
            if (!email.isEmpty()) {
                vm.resetearPassword(email);
            } else {
                binding.tvMensaje.setText("Por favor ingrese su email");
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}