package com.reporteciudadano.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


import com.reporteciudadano.app.R;

public class ContactoActivity extends AppCompatActivity {

    private Button btnNavReporte;
    private Button btnNavContacto;
    private Button btnMapa;
    private Button btnCorreo;
    private Button btnLlamar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        btnNavReporte = findViewById(R.id.btnNavReporte);
        btnNavContacto = findViewById(R.id.btnNavContacto);
        btnMapa = findViewById(R.id.btnMapa);
        btnCorreo = findViewById(R.id.btnCorreo);
        btnLlamar = findViewById(R.id.btnLlamar);

        btnNavReporte.setOnClickListener(v -> {
            startActivity(new Intent(ContactoActivity.this, ReporteActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        // Intent
        btnMapa.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode("Guaymas, Sonora"));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=" + Uri.encode("Guaymas, Sonora"))));
            }
        });

        btnCorreo.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + "contacto@mcaconsultores.com.mx"));
            startActivity(Intent.createChooser(emailIntent, "Enviar correo"));
        });

        btnLlamar.setOnClickListener(v -> {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + "6221234567"));
            startActivity(dialIntent);
        });
    }
}
