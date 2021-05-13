package com.example.bitacoras2020.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.example.bitacoras2020.MainActivity;
import com.example.bitacoras2020.R;
import com.example.bitacoras2020.Utils.ApplicationResourcesProvider;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import soup.neumorphism.NeumorphButton;


public class Scanner extends AppCompatActivity{

    static String TAG = "SCANNER_BARRAS";
    public String bitacora="";
    Button btScanner;
    TextView tvCodigoBarras, tvMensaje;
    Dialog dialogoError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        btScanner =(Button) findViewById(R.id.btScanner);
        tvCodigoBarras =(TextView) findViewById(R.id.tvCodigoBarras);
        dialogoError = new Dialog(this);
        tvMensaje =(TextView) findViewById(R.id.tvTextoMensaje);

        btScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(Scanner.this).initiateScan();
            }
        });

        final Bundle extras = getIntent().getExtras();
        if(extras!=null)
            if(extras.containsKey("bitacora"))
                bitacora = extras.getString("bitacora");
            else
                Log.v(TAG, "No se recuperaron datos de extras");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() != null) {
                tvCodigoBarras.setText(result.getContents());
                tvMensaje.setText("Tu código fue escaneado");
                saveBarCode(result.getContents());
            }
            else {
                tvCodigoBarras.setText("00000000000000");
                tvMensaje.setText("No se pudo escanear el código, intenta nuevamente");
            }
        }
    }

    private void saveBarCode(String contents)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        String hora = horaFormat.format(cal.getTime());

        if (bitacora.length()>0) {
            DatabaseAssistant.insertarCodigosDeBarras(
                    "" + bitacora,
                    "" + dateFormat.format(new Date()),
                    "" + hora,
                    "" + contents
            );

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup)findViewById(R.id.relativeLayout1));

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(view);
            toast.show();
            finish();
        }
    }

    public void showErrorDialog(final String codeError) {
        final NeumorphButton btNo, btSi;
        TextView tvCodeError;
        dialogoError.setContentView(R.layout.layout_error);
        dialogoError.setCancelable(false);
        btNo = (NeumorphButton) dialogoError.findViewById(R.id.btNo);
        btSi = (NeumorphButton) dialogoError.findViewById(R.id.btSi);
        tvCodeError = (TextView) dialogoError.findViewById(R.id.tvCodeError);
        tvCodeError.setText(codeError);
        btSi.setVisibility(View.GONE);

        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogoError.dismiss();
            }
        });

        dialogoError.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogoError.show();

    }

}