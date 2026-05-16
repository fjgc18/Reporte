package com.reporteciudadano.app.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import com.reporteciudadano.app.R;
import com.reporteciudadano.app.models.ReporteRequest;
import com.reporteciudadano.app.models.ReporteResponse;
import com.reporteciudadano.app.network.ApiClient;
import com.reporteciudadano.app.network.ApiService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReporteActivity extends AppCompatActivity {

    private String base64Image = "";
    private Uri photoUri;

    // View declarations
    private EditText etNombre;
    private AutoCompleteTextView atvColonia;
    private EditText etDireccion;
    private EditText etCelular;
    private EditText etCorreo;
    private EditText etDescripcion;
    private Spinner spinnerTipo;
    private Button btnSeleccionarImagen;
    private Button btnEnviar;
    private ImageView ivPreview;
    private ProgressBar progressBar;
    private Button btnNavReporte;
    private Button btnNavContacto;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(this, "Permiso denegado para usar la cámara", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && photoUri != null) {
                    ivPreview.setImageURI(photoUri);
                    ivPreview.setVisibility(View.VISIBLE);
                    base64Image = convertImageUriToBase64(photoUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);

        // Initialize views
        etNombre = findViewById(R.id.etNombre);
        atvColonia = findViewById(R.id.atvColonia);
        etDireccion = findViewById(R.id.etDireccion);
        etCelular = findViewById(R.id.etCelular);
        etCorreo = findViewById(R.id.etCorreo);
        etDescripcion = findViewById(R.id.etDescripcion);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnEnviar = findViewById(R.id.btnEnviar);
        ivPreview = findViewById(R.id.ivPreview);
        progressBar = findViewById(R.id.progressBar);
        btnNavReporte = findViewById(R.id.btnNavReporte);
        btnNavContacto = findViewById(R.id.btnNavContacto);

        btnNavContacto.setOnClickListener(v -> {
            startActivity(new Intent(ReporteActivity.this, ContactoActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        setupSpinners();
        
        btnSeleccionarImagen.setOnClickListener(v -> checkPermissionAndOpenCamera());
        btnEnviar.setOnClickListener(v -> enviarReporte());
    }

    private void setupSpinners() {
        String[] colonias = getResources().getStringArray(R.array.colonias);
        ArrayAdapter<String> adapterColonias = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, colonias);
        atvColonia.setAdapter(adapterColonias);

        String[] tipos = getResources().getStringArray(R.array.tipos_reporte);
        ArrayAdapter<String> adapterTipos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipos);
        adapterTipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipos);
    }

    private void checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void dispatchTakePictureIntent() {
        File photoFile = null;
        try {
            photoFile = File.createTempFile("reporte_", ".jpg", getExternalCacheDir());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
            takePictureLauncher.launch(photoUri);
        }
    }

    private void enviarReporte() {
        String nombre = etNombre.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String colonia = atvColonia.getText().toString().trim();
        String celular = etCelular.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String tipo = spinnerTipo.getSelectedItem().toString();

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Campo requerido");
            return;
        }
        if (TextUtils.isEmpty(colonia)) {
            atvColonia.setError("Campo requerido");
            return;
        }
        if (TextUtils.isEmpty(direccion)) {
            etDireccion.setError("Campo requerido");
            return;
        }
        if (TextUtils.isEmpty(celular) || celular.length() < 10) {
            etCelular.setError("Celular inválido");
            return;
        }
        if (TextUtils.isEmpty(correo) || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Correo inválido");
            return;
        }
        if (TextUtils.isEmpty(descripcion)) {
            etDescripcion.setError("Campo requerido");
            return;
        }
        if (TextUtils.isEmpty(base64Image)) {
            Toast.makeText(this, "Debe tomar una foto", Toast.LENGTH_SHORT).show();
            return;
        }

        ReporteRequest request = new ReporteRequest(nombre, direccion, colonia, celular, correo, tipo, descripcion, base64Image);

        progressBar.setVisibility(View.VISIBLE);
        btnEnviar.setEnabled(false);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ReporteResponse> call = apiService.enviarReporte("Bearer " + ApiClient.API_TOKEN, request);
        
        call.enqueue(new Callback<ReporteResponse>() {
            @Override
            public void onResponse(Call<ReporteResponse> call, Response<ReporteResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnEnviar.setEnabled(true);
                
                if (response.isSuccessful()) {
                    Toast.makeText(ReporteActivity.this, "Reporte enviado exitosamente", Toast.LENGTH_LONG).show();
                    limpiarFormulario();
                } else {
                    Toast.makeText(ReporteActivity.this, "Error al enviar reporte: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ReporteResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnEnviar.setEnabled(true);
                Toast.makeText(ReporteActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void limpiarFormulario() {
        etNombre.setText("");
        etDireccion.setText("");
        atvColonia.setText("");
        etCelular.setText("");
        etCorreo.setText("");
        etDescripcion.setText("");
        spinnerTipo.setSelection(0);
        ivPreview.setVisibility(View.GONE);
        base64Image = "";
        photoUri = null;
    }

    private String convertImageUriToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            Bitmap resizedBitmap = getResizedBitmap(bitmap, 800);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            return "data:image/png;base64," + android.util.Base64.encodeToString(imageBytes, android.util.Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
