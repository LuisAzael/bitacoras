package com.example.bitacoras2020.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.bitacoras2020.Database.Choferes;
import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.example.bitacoras2020.Database.LoginZone;
import com.example.bitacoras2020.Database.Lugares;
import com.example.bitacoras2020.R;
import com.example.bitacoras2020.Utils.ApplicationResourcesProvider;
import com.example.bitacoras2020.Utils.ConstantsBitacoras;
import com.example.bitacoras2020.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Welcome extends AppCompatActivity {

    private static final String TAG = "WELCOME";
    Button btLatino;
    TextView btProveedor;
    Dialog dialogoError;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        btLatino =(Button) findViewById(R.id.btLatino);
        btProveedor =(TextView) findViewById(R.id.btProveedor);
        dialogoError = new Dialog(Welcome.this);

        if(!ApplicationResourcesProvider.checkInternetConnection())
            showErrorDialog("No hay conexión a internet");

        btLatino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Login.class);
                startActivity(intent);
            }
        });

        btProveedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Login_Proveedor.class);
                startActivity(intent);
            }
        });
    }

    public void showErrorDialog(final String codeError) {
        final Button btNo, btSi;
        TextView tvCodeError;
        dialogoError.setContentView(R.layout.layout_error);
        dialogoError.setCancelable(false);
        btNo = (Button) dialogoError.findViewById(R.id.btNo);
        btSi = (Button) dialogoError.findViewById(R.id.btSi);
        tvCodeError = (TextView) dialogoError.findViewById(R.id.tvCodeError);
        tvCodeError.setText(codeError);

        if (codeError.equals("No hay conexión a internet")){
            btSi.setVisibility(View.GONE);
            btNo.setText("Salir");
        }

        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogoError.dismiss();
                finishAffinity();
            }
        });

        dialogoError.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogoError.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}