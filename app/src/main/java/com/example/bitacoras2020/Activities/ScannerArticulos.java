package com.example.bitacoras2020.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.bitacoras2020.Adapters.AdapterArticulosEscaneados;
import com.example.bitacoras2020.Adapters.AdapterEquiposInstalacion;
import com.example.bitacoras2020.Callbacks.CancelarArticuloEscaneado;
import com.example.bitacoras2020.Database.Articuloscan;
import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.example.bitacoras2020.Database.Equipoinstalacion;
import com.example.bitacoras2020.Models.ModelArticulosEscaneados;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bitacoras2020.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class ScannerArticulos extends AppCompatActivity implements CancelarArticuloEscaneado {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_articulos);

        ImageView btBack =(ImageView) findViewById(R.id.btBack);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(ScannerArticulos.this).initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() != null) {
                DatabaseAssistant.insertarArticulosEscaneados("Cafetera", result.getContents(), "");
                consultarArticulosEscaneados();
                Toast.makeText(this, "Articulo guardado correctamente", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "No se puede escanear el código.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void consultarArticulosEscaneados() {
        RecyclerView rvDocumentos;
        GridLayoutManager gridLayoutManager;
        rvDocumentos = (RecyclerView) findViewById(R.id.rvArticulos);
        rvDocumentos.setHasFixedSize(true);
        LinearLayout frameSinDatos =(LinearLayout) findViewById(R.id.frameSinDatos);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rvDocumentos.setLayoutManager(gridLayoutManager);
        List<ModelArticulosEscaneados> modelDocumentos = new ArrayList<>();
        AdapterArticulosEscaneados adapterDocumentos = null;

        List<Articuloscan> documentosList = Articuloscan.findWithQuery(Articuloscan.class, "SELECT * FROM ARTICULOSCAN ORDER BY id DESC");
        if (documentosList.size() > 0) {
            rvDocumentos.setVisibility(View.VISIBLE);
            frameSinDatos.setVisibility(View.GONE);
            modelDocumentos.clear();
            for (int i = 0; i <= documentosList.size() - 1; i++) {
                ModelArticulosEscaneados product = new ModelArticulosEscaneados(
                        "" + documentosList.get(i).getNombre(),
                        "" + documentosList.get(i).getSerie(),
                        "" + documentosList.get(i).getFecha(),
                        "" + documentosList.get(i).getSync(),
                        "" + documentosList.get(i).getBitacora(),
                        "" + documentosList.get(i).getId()
                );
                modelDocumentos.add(product);
            }

            adapterDocumentos = new AdapterArticulosEscaneados(getApplicationContext(), modelDocumentos, ScannerArticulos.this);
            rvDocumentos.setAdapter(adapterDocumentos);
        } else {
            modelDocumentos.clear();
            adapterDocumentos = null;
            rvDocumentos.setVisibility(View.GONE);
            frameSinDatos.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        consultarArticulosEscaneados();
    }

    @Override
    public void onClickCancelarArticulo(int position, String id) {

    }
}