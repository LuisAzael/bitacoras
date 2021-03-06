package com.example.bitacoras2020.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bitacoras2020.Adapters.AdapterArticulosEscaneados;
import com.example.bitacoras2020.Adapters.AdapterComentarios;
import com.example.bitacoras2020.Adapters.AdapterDocumentos;
import com.example.bitacoras2020.Adapters.AdapterEquiposCortejo;
import com.example.bitacoras2020.Adapters.AdapterEquiposInstalacion;
import com.example.bitacoras2020.Adapters.AdapterEquiposRecoleccion;
import com.example.bitacoras2020.Adapters.AdapterEquiposTraslado;
import com.example.bitacoras2020.Adapters.AdapterNotificaciones;
import com.example.bitacoras2020.Callbacks.CancelarArticuloCortejo;
import com.example.bitacoras2020.Callbacks.CancelarArticuloEscaneado;
import com.example.bitacoras2020.Callbacks.CancelarArticuloInstalacion;
import com.example.bitacoras2020.Callbacks.CancelarArticuloRecoleccion;
import com.example.bitacoras2020.Callbacks.CancelarArticuloTraslado;
import com.example.bitacoras2020.Database.Adicional;
import com.example.bitacoras2020.Database.Articuloscan;
import com.example.bitacoras2020.Database.Bitacoras;
import com.example.bitacoras2020.Database.CatalogoArticulos;
import com.example.bitacoras2020.Database.Comentarios;
import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.example.bitacoras2020.Database.Documentos;
import com.example.bitacoras2020.Database.EquipoRecoleccion;
import com.example.bitacoras2020.Database.EquipoTraslado;
import com.example.bitacoras2020.Database.Equipocortejo;
import com.example.bitacoras2020.Database.Equipoinstalacion;
import com.example.bitacoras2020.Database.Eventos;

import com.example.bitacoras2020.Database.Inventario;
import com.example.bitacoras2020.Database.LoginZone;
import com.example.bitacoras2020.Database.Notificaciones;
import com.example.bitacoras2020.MainActivity;
import com.example.bitacoras2020.Models.ModelArticulosEscaneados;
import com.example.bitacoras2020.Models.ModelComentarios;
import com.example.bitacoras2020.Models.ModelNotificaciones;
import com.example.bitacoras2020.R;
import com.example.bitacoras2020.Utils.ApplicationResourcesProvider;
import com.example.bitacoras2020.Utils.BaseActivity;
import com.example.bitacoras2020.Utils.ConstantsBitacoras;
import com.example.bitacoras2020.Utils.Preferences;
import com.example.bitacoras2020.Utils.VolleySingleton;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;

public class BitacoraDetalle extends BaseActivity implements CancelarArticuloEscaneado, CancelarArticuloInstalacion, CancelarArticuloCortejo, CancelarArticuloRecoleccion, CancelarArticuloTraslado {


    private static final String TAG = "BITACORA_DETALLE";
    //************* variables **********
    ImageView btBack;
    LottieAnimationView imgSuccess;
    String bitacora="";
    NeumorphCardView btScanner, btSalida, btllegada;
    Button btFinalizarBitacora;
    Dialog dialogoSalidas;
    SimpleDateFormat dateFormat;
    ImageView btComentarios;
    Dialog mBottomSheetDialogComentarios;
    int status;
    String descripcionPeticion ="", fechaEstatica="";
    boolean requesterCanceled = false;

    private boolean equipoDeTraslado = false, equipoDeCortejo = false, equipoDeInstalacion = false, equipoRecoleccion = false,  isArticuloDeVelacion = false, scannerAtaurna= false;

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();
    //Variables para obtener la hora hora
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);

    RadioButton rbSi ;
    RadioButton rbNo ;
    RadioButton rbSiEncapsulado, rbNoEncapsulado ;

    CheckBox cbIneFinado ;
    CheckBox cbActaDeNacimientoFinado ;
    CheckBox cbIneFamiliar ;
    CheckBox cbActaDeNacimientoFamiliar ;
    CheckBox cbCertificadoDeDefuncion ;
    CheckBox cbTituloDePropiedadCementerio ;
    CheckBox cbOtros ;


    TextInputEditText etObservacionesInstalacion;
    TextInputEditText etObservacionesCortejo ;
    Button btGuardarDatosDeRecoleccion;
    String horaSeleccionadaDeRecoleccion = "";

    Spinner spLugarDeVelacion, spTipoServicio, spTipoProcedimiento, spLaboratorios;
    JSONObject jsonDocumentos = new JSONObject();

    String lugarDeVelacion = "", tipoDeServicio = "", tipoProcedimiento="", laboratorio="";

    LinearLayout frameOtros, layoutUrna, layoutAtaud;

    TextView tvAnadirDocumento, tvEliminarAtaud, tvEliminarUrna;
    EditText etNombreDocumentoAnadido;


    TextView btAnadirEquipoRecoleccion;
    RecyclerView rvEquiposEscaneadosRecoleccion;
    TextInputEditText etObservacionesRecoleccion, etObservacionesTraslado;




    private void consultarDocumentosAnadidos(String bitacora) {
        RecyclerView rvDocumentos;
        GridLayoutManager gridLayoutManager;
        rvDocumentos = (RecyclerView) findViewById(R.id.rvDocumentosExtras);
        rvDocumentos.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rvDocumentos.setLayoutManager(gridLayoutManager);
        List<Documentos> modelDocumentos = new ArrayList<>();
        AdapterDocumentos adapterDocumentos = null;

        List<Documentos> documentosList = Documentos.findWithQuery(Documentos.class, "SELECT * FROM DOCUMENTOS WHERE bitacora = '" + bitacora + "' ORDER BY id ASC");
        if (documentosList.size() > 0) {
            rvDocumentos.setVisibility(View.VISIBLE);
            //frameSinDatos.setVisibility(View.GONE);
            modelDocumentos.clear();
            for (int i = 0; i <= documentosList.size() - 1; i++) {
                Documentos product = new Documentos(
                        "" + documentosList.get(i).getBitacora(),
                        "" + documentosList.get(i).getDocumento(),
                        "" + documentosList.get(i).getFecha(),
                        "" + documentosList.get(i).getSync()
                );
                modelDocumentos.add(product);
            }

            adapterDocumentos = new AdapterDocumentos(getApplicationContext(), modelDocumentos);
            rvDocumentos.setAdapter(adapterDocumentos);
        } else {
            modelDocumentos.clear();
            adapterDocumentos = null;
            rvDocumentos.setVisibility(View.GONE);
            //frameSinDatos.setVisibility(View.VISIBLE);
        }


    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.fragment_bitacora_detalle);
        setTypefaceTextViews();
        android.transition.Fade fade = new android.transition.Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);



        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fechaEstatica = dateFormat.format(new Date());
        spTipoProcedimiento = (Spinner) findViewById(R.id.spTipoProcedimiento);
        spLaboratorios = (Spinner) findViewById(R.id.spLaboratorio);

        dialogoSalidas = new Dialog(this);
        btScanner =( NeumorphCardView) findViewById(R.id.btScanner);
        btSalida =( NeumorphCardView) findViewById(R.id.btSalida);
        btllegada =( NeumorphCardView) findViewById(R.id.btLlegada);
        btBack =( ImageView) findViewById(R.id.btBack);
        imgSuccess =(LottieAnimationView) findViewById(R.id.imgSuccess);
        btFinalizarBitacora =( Button ) findViewById(R.id.btFinalizarBitacora);
        frameOtros =( LinearLayout ) findViewById(R.id.frameOtros);
        tvAnadirDocumento =( TextView ) findViewById(R.id.tvAnadirDocumento);
        etNombreDocumentoAnadido =( EditText ) findViewById(R.id.etNombreDocumentoAnadido);
        btComentarios =(ImageView) findViewById(R.id.btComentarios);

        tvEliminarAtaud =( TextView ) findViewById(R.id.tvEliminarAtaud);
        tvEliminarUrna =( TextView ) findViewById(R.id.tvEliminarUrna);

        layoutAtaud =( LinearLayout ) findViewById(R.id.layoutAtaud);
        layoutUrna =( LinearLayout ) findViewById(R.id.layoutUrna);


        btAnadirEquipoRecoleccion = (TextView) findViewById(R.id.btAnadirEquipoRecoleccion);
        rvEquiposEscaneadosRecoleccion = (RecyclerView) findViewById(R.id.rvEquiposEscaneadosRecoleccion);
        etObservacionesRecoleccion = (TextInputEditText) findViewById(R.id.etObservacionesRecoleccion);
        etObservacionesTraslado = (TextInputEditText) findViewById(R.id.etObservacionesTraslado);




        LinearLayout tvInformacionAdicional = (LinearLayout) findViewById(R.id.tvInformacionAdicional);
        tvInformacionAdicional.setVisibility(View.VISIBLE);


        spTipoProcedimiento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoProcedimiento = spTipoProcedimiento.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tipoProcedimiento = "";
            }
        });

        spLaboratorios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                laboratorio = spLaboratorios.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                laboratorio = "";
            }
        });


        btComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //showAnimationFromSuccessRecord();
                showComentariosDialog(v, bitacora);
            }
        });

        Button btCancelarPeticion =(Button) findViewById(R.id.btCancelarPeticion);
        btCancelarPeticion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = -1;
                requesterCanceled = true;
                hideLoadingDialog();
            }
        });


        tvAnadirDocumento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etNombreDocumentoAnadido.getText().toString().equals("")){
                    if(etNombreDocumentoAnadido.getText().toString().length()<=2)
                        Toast.makeText(BitacoraDetalle.this, "Nombre muy corto para ser guardado", Toast.LENGTH_SHORT).show();
                    else {
                        DatabaseAssistant.insertarDocumentoExtra("" + bitacora, "" + etNombreDocumentoAnadido.getText().toString());
                        consultarDocumentosAnadidos(bitacora);
                        Toast.makeText(BitacoraDetalle.this, "Documento a??adido correctamente", Toast.LENGTH_SHORT).show();
                        etNombreDocumentoAnadido.setText("");
                    }
                }
                else
                    Toast.makeText(BitacoraDetalle.this, "Ingresa un nombre de documento", Toast.LENGTH_SHORT).show();
            }
        });



        TextView tvAyuda = (TextView) findViewById(R.id.tvAyuda);
        tvAyuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artuculosDeVelacionTutorial();
            }
        });

        //***************************

        FrameLayout cardContainerArticulosDeVelacion = (FrameLayout) findViewById(R.id.card_containerr);
        TextView btAnadirArticulo = (TextView) findViewById(R.id.btAnadirArticulo);
        cardContainerArticulosDeVelacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout expand_vieww = (LinearLayout) findViewById(R.id.expand_vieww);
                if(expand_vieww.getVisibility() == View.GONE) {
                    consultarArticulosEscaneados(bitacora);
                    expand_vieww.setVisibility(View.VISIBLE);
                }else {
                    expand_vieww.setVisibility(View.GONE);
                }
            }
        });

        /*btAnadirArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                equipoDeCortejo = false;
                equipoDeInstalacion = false;
                isArticuloDeVelacion = true;
                new IntentIntegrator(BitacoraDetalle.this).initiateScan();
            }
        });*/


        tvEliminarAtaud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(BitacoraDetalle.this);
                dialogo1.setCancelable(false);
                dialogo1.setTitle("Informaci??n");
                dialogo1.setMessage("??Seguro que des??as eliminar el ata??d?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {

                        TextView tvCodigo = ( TextView ) findViewById(R.id.tvCodigoAtaud);
                        String codigo = tvCodigo.getText().toString();

                        List<Inventario> inventarioList = Inventario.findWithQuery(Inventario.class, "SELECT * FROM INVENTARIO WHERE codigo = '" + codigo + "' order by id desc limit 1");
                        if (inventarioList.size() > 0)
                        {

                            DatabaseAssistant.insertarArticuloCancelado(
                                    "" + inventarioList.get(0).getCodigo(),
                                    "" + inventarioList.get(0).getDescripcion(),
                                    "" + inventarioList.get(0).getSerie(),
                                    "" + inventarioList.get(0).getFecha(),
                                    "" + inventarioList.get(0).getProveedor(),
                                    "" + inventarioList.get(0).getBitacora(),
                                    "" + inventarioList.get(0).getCapturado()
                            );

                            //Inventario.executeQuery("DELETE FROM INVENTARIO WHERE codigo ='" + codigo + "'");
                            Inventario.executeQuery("UPDATE INVENTARIO SET borrado = '1' WHERE codigo ='" + codigo + "'");
                            llenarCamposDeAtaurna(bitacora);
                        }else{
                            Log.d(TAG, "onClick: NO hay ataudes con el codigo descrito");
                        }

                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.dismiss();
                    }
                });
                dialogo1.show();
            }
        });

        tvEliminarUrna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(BitacoraDetalle.this);
                dialogo1.setCancelable(false);
                dialogo1.setTitle("Informaci??n");
                dialogo1.setMessage("??Seguro que des??as eliminar la Urna?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {

                        TextView tvUrna = ( TextView ) findViewById(R.id.tvCodigoUrna);
                        String codigo = tvUrna.getText().toString();

                        List<Inventario> inventarioList = Inventario.findWithQuery(Inventario.class, "SELECT * FROM INVENTARIO WHERE codigo = '" + codigo + "' order by id desc limit 1");
                        if (inventarioList.size() > 0)
                        {

                            DatabaseAssistant.insertarArticuloCancelado(
                                    "" + inventarioList.get(0).getCodigo(),
                                    "" + inventarioList.get(0).getDescripcion(),
                                    "" + inventarioList.get(0).getSerie(),
                                    "" + inventarioList.get(0).getFecha(),
                                    "" + inventarioList.get(0).getProveedor(),
                                    "" + inventarioList.get(0).getBitacora(),
                                    "" + inventarioList.get(0).getCapturado()
                            );

                            //Inventario.executeQuery("DELETE FROM INVENTARIO WHERE codigo ='" + codigo + "'");
                            Inventario.executeQuery("UPDATE INVENTARIO SET borrado = '1' WHERE codigo ='" + codigo + "'");
                            llenarCamposDeAtaurna(bitacora);
                        }else{
                            Log.d(TAG, "onClick: NO hay ataudes con el codigo descrito");
                        }

                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.dismiss();
                    }
                });
                dialogo1.show();
            }
        });
        //***********************


        //*********** Recolecci??n ****************************//
        FrameLayout cardContainer = (FrameLayout) findViewById(R.id.card_container);
        cardContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout expandView = (LinearLayout) findViewById(R.id.expand_view);
                if(expandView.getVisibility() == View.GONE){

                    ArrayAdapter<String> adapterLugaresDeVelaciones = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.getLugares());
                    spLugarDeVelacion.setAdapter(adapterLugaresDeVelaciones);

                    ArrayAdapter<String> adapterLaboratorios = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.getLaboratorios());
                    spLaboratorios.setAdapter(adapterLaboratorios);

                    String[] opcionesDeTraslado = {"Selecciona una opci??n...", "INHUMACI??N", "CREMACI??N", "TRASLADO"};
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, opcionesDeTraslado);
                    spTipoServicio.setAdapter(adapter);

                    String[] procedimientos ={"Selecciona una opci??n...", "Embalsamado", "Arreglo est??tico"};
                    ArrayAdapter<String> adapterProcedimientos = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, procedimientos);
                    spTipoProcedimiento.setAdapter(adapterProcedimientos);

                    List<Adicional> adicionalinfoList = Adicional.findWithQuery(Adicional.class, "SELECT * FROM ADICIONAL WHERE bitacora = '"+ bitacora +"' ORDER BY id DESC LIMIT 1");
                    if(adicionalinfoList.size()>0) {

                        try {
                            TextView tvSeleccionarHora = (TextView) findViewById(R.id.tvSeleccionarHora);
                            tvSeleccionarHora.setText(adicionalinfoList.get(0).getHoraRecoleccion());

                            if (adicionalinfoList.get(0).getRopaEntregada().equals("")) {
                                rbSi.setChecked(false);
                                rbNo.setChecked(false);
                            } else {
                                if (adicionalinfoList.get(0).getRopaEntregada().equals("SI"))
                                    rbSi.setChecked(true);
                                else if (adicionalinfoList.get(0).getRopaEntregada().equals("NO"))
                                    rbNo.setChecked(true);
                            }


                            if (adicionalinfoList.get(0).getEncapsulado().equals("")) {
                                rbSiEncapsulado.setChecked(false);
                                rbNoEncapsulado.setChecked(false);
                            } else {
                                if (adicionalinfoList.get(0).getEncapsulado().equals("ENCAPSULADA"))
                                    rbSiEncapsulado.setChecked(true);
                                else if (adicionalinfoList.get(0).getEncapsulado().equals("SIN ENCAPSULAR"))
                                    rbNoEncapsulado.setChecked(true);
                            }

                            try {
                                spLugarDeVelacion.setSelection(adapterLugaresDeVelaciones.getPosition(adicionalinfoList.get(0).getLugarDeVelacion()));
                                spTipoServicio.setSelection(adapter.getPosition(adicionalinfoList.get(0).getTipoDeServicio()));
                                spTipoProcedimiento.setSelection(adapterProcedimientos.getPosition(adicionalinfoList.get(0).getProcedimiento()));
                                spLaboratorios.setSelection(adapterLaboratorios.getPosition(adicionalinfoList.get(0).getLaboratorio()));
                            }catch (Throwable e){
                                Log.e(TAG, "onClick: Error: " + e.getMessage());
                            }


                            try {
                                if (adicionalinfoList.get(0).getJsonAdicionalInfo().contains("Documentos")) {
                                    JSONObject json = new JSONObject(adicionalinfoList.get(0).getJsonAdicionalInfo());

                                    if (json.getJSONObject("Documentos").getBoolean("INE_FINADO"))
                                        cbIneFinado.setChecked(true);
                                    else
                                        cbIneFinado.setChecked(false);

                                    if (json.getJSONObject("Documentos").getBoolean("ACTA_NACIMIENTO_FINADO"))
                                        cbActaDeNacimientoFinado.setChecked(true);
                                    else
                                        cbActaDeNacimientoFinado.setChecked(false);

                                    if (json.getJSONObject("Documentos").getBoolean("INE_FAMILIAR"))
                                        cbIneFamiliar.setChecked(true);
                                    else
                                        cbIneFamiliar.setChecked(false);

                                    if (json.getJSONObject("Documentos").getBoolean("ACTA_NACIMIENTO_FAMILIAR"))
                                        cbActaDeNacimientoFamiliar.setChecked(true);
                                    else
                                        cbActaDeNacimientoFamiliar.setChecked(false);

                                    if (json.getJSONObject("Documentos").getBoolean("CERTIFICADO_DEFUNCION"))
                                        cbCertificadoDeDefuncion.setChecked(true);
                                    else
                                        cbCertificadoDeDefuncion.setChecked(false);

                                    if (json.getJSONObject("Documentos").getBoolean("TITULO_PROPIEDAD_CEMENTERIO"))
                                        cbTituloDePropiedadCementerio.setChecked(true);
                                    else
                                        cbTituloDePropiedadCementerio.setChecked(false);

                                    if (json.getJSONObject("Documentos").getBoolean("OTROS_DOCUMENTOS"))
                                        cbOtros.setChecked(true);
                                    else
                                        cbOtros.setChecked(false);

                                } else {
                                    Log.d(TAG, "onClick: No tiene Documentos");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            etObservacionesInstalacion.setText(adicionalinfoList.get(0).getObservacionesInstalacion());
                            etObservacionesCortejo.setText(adicionalinfoList.get(0).getObservacionesCortejo());
                            etObservacionesRecoleccion.setText(adicionalinfoList.get(0).getObservacionesrecoleccion());
                            etObservacionesTraslado.setText(adicionalinfoList.get(0).getObservacionestraslado());
                        } catch (Throwable e) {
                            Log.e(TAG, "onClick: Error en condulta de detalles de bitacora: " + e.getLocalizedMessage());
                        }
                    }

                    try {
                        consultarEquiposDeInstalacion(bitacora);
                        consultarEquiposDeCortejo(bitacora);
                        consultarEquiposDeRecoleccion(bitacora);
                        consultarEquiposDeTraslado(bitacora);
                        expandView.setVisibility(View.VISIBLE);
                    }catch (Throwable e){
                        Log.e(TAG, "onClick: Error al expandir la informaci??n detallada: " + e.getMessage());
                        Log.e(TAG, "onClick: Error al expandir la informaci??n detallada: " + e.getLocalizedMessage());
                    }
                }else {
                    expandView.setVisibility(View.GONE);
                }
            }
        });

        TextView tvSeleccionarHora = (TextView) findViewById(R.id.tvSeleccionarHora);
        tvSeleccionarHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerHora();
            }
        });


        rbSi = (RadioButton) findViewById(R.id.rbSi);
        rbNo = (RadioButton) findViewById(R.id.rbNo);
        rbSiEncapsulado = (RadioButton) findViewById(R.id.rbSiEncapsulado);
        rbNoEncapsulado = (RadioButton) findViewById(R.id.rbNoEncapsulado);
        cbIneFinado = (CheckBox) findViewById(R.id.cbIneFinado);
        cbActaDeNacimientoFinado = (CheckBox) findViewById(R.id.cbActaDeNacimientoFinado);
        cbIneFamiliar = (CheckBox) findViewById(R.id.cbIneFamiliar);
        cbActaDeNacimientoFamiliar = (CheckBox) findViewById(R.id.cbActaDeNacimientoFamiliar);
        cbCertificadoDeDefuncion = (CheckBox) findViewById(R.id.cbCertificadoDeDefuncion);
        cbTituloDePropiedadCementerio = (CheckBox) findViewById(R.id.cbTituloDePropiedadCementerio);
        cbOtros = (CheckBox) findViewById(R.id.cbOtros);
        etObservacionesInstalacion = (TextInputEditText) findViewById(R.id.etObservacionesInstalacion);
        //etNumeroEquipoCortejo = (TextInputEditText) findViewById(R.id.etNumeroEquipoCortejo);
        etObservacionesCortejo = (TextInputEditText) findViewById(R.id.etObservacionesCortejo);
        btGuardarDatosDeRecoleccion = (Button) findViewById(R.id.btGuardarDatosDeRecoleccion);
        spLugarDeVelacion = (Spinner) findViewById(R.id.spLugarDeVelacion);
        spTipoServicio = (Spinner) findViewById(R.id.spTipoServicio);

        spLugarDeVelacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lugarDeVelacion = spLugarDeVelacion.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                lugarDeVelacion = "";
            }
        });
        spTipoServicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoDeServicio = spTipoServicio.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tipoDeServicio = "";
            }
        });
        btGuardarDatosDeRecoleccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ropaEntregada = "", ropaEncapsulada = "";

                if (rbSi.isChecked())
                    ropaEntregada = "SI";
                else if (rbNo.isChecked())
                    ropaEntregada = "NO";
                else
                    ropaEntregada = "Desconocido";


                if (rbSiEncapsulado.isChecked())
                     ropaEncapsulada = "ENCAPSULADA";
                else if(rbNoEncapsulado.isChecked())
                    ropaEncapsulada = "SIN ENCAPSULAR";
                else
                    ropaEncapsulada = "Desconocido";

                JSONObject jsonGeneral = new JSONObject();
                try {
                    jsonDocumentos.put("INE_FINADO", cbIneFinado.isChecked());
                    jsonDocumentos.put("ACTA_NACIMIENTO_FINADO",  cbActaDeNacimientoFinado.isChecked());
                    jsonDocumentos.put("INE_FAMILIAR",  cbIneFamiliar.isChecked());
                    jsonDocumentos.put("ACTA_NACIMIENTO_FAMILIAR",  cbActaDeNacimientoFamiliar.isChecked());
                    jsonDocumentos.put("CERTIFICADO_DEFUNCION", cbCertificadoDeDefuncion.isChecked());
                    jsonDocumentos.put("TITULO_PROPIEDAD_CEMENTERIO",  cbTituloDePropiedadCementerio.isChecked());
                    jsonDocumentos.put("OTROS_DOCUMENTOS",  cbOtros.isChecked());
                    jsonGeneral.put("Documentos", jsonDocumentos);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Adicional.executeQuery("DELETE FROM ADICIONAL WHERE bitacora = '" + bitacora + "'");

                String seleccionarHora="00:00:00";
                if (tvSeleccionarHora.getText().toString().equals("Selecciona aqui..."))
                    seleccionarHora = "00:00:00";

                DatabaseAssistant.insertarInformacionAdicional(
                        "" + seleccionarHora,
                        "" + ropaEntregada,
                        "" + lugarDeVelacion,
                        "" + tipoDeServicio,
                        "" + "etNumeroEquipoInstalacion.getText().toString()" ,
                        "" + etObservacionesInstalacion.getText().toString(),
                        "" + "etNumeroEquipoCortejo.getText().toString()",
                        "" + etObservacionesCortejo.getText().toString(),
                        "" + jsonGeneral.toString(),
                        bitacora,
                        ropaEncapsulada,
                        "" + etObservacionesRecoleccion.getText().toString(),
                        "" + etObservacionesTraslado.getText().toString(),
                        tipoProcedimiento.equals("Selecciona una opci??n...") ? "" : tipoProcedimiento,
                        laboratorio.equals("Selecciona una opci??n...") ? "" : laboratorio,
                        DatabaseAssistant.getIDFromLaboratorioString(laboratorio)
                );

                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup)findViewById(R.id.relativeLayout1));
                LottieAnimationView lottieAnimationView = view.findViewById(R.id.imageView1);
                lottieAnimationView.setAnimation("success_toast.json");
                lottieAnimationView.loop(false);
                lottieAnimationView.playAnimation();
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view);
                toast.show();

                LinearLayout expandView = (LinearLayout) findViewById(R.id.expand_view);
                expandView.setVisibility(View.GONE);
            }
        });


        cbOtros.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    consultarDocumentosAnadidos(bitacora);
                    frameOtros.setVisibility(View.VISIBLE);
                }
                else{
                    frameOtros.setVisibility(View.GONE);
                }
            }
        });


        //*************************************************************




        final Bundle extras = getIntent().getExtras();
        if(extras!=null)
            if(extras.containsKey("bitacora"))
                bitacora = extras.getString("bitacora");
        else
            Log.v(TAG, "No se recuperaron datos de extras");

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView btAnadirEquipo = (TextView) findViewById(R.id.btAnadirEquipo);
        btAnadirEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                equipoDeCortejo = false;
                equipoDeInstalacion = true;
                isArticuloDeVelacion = false;
                scannerAtaurna = false;
                equipoRecoleccion = false;
                equipoDeTraslado = false;
                new IntentIntegrator(BitacoraDetalle.this).initiateScan();
            }
        });

        TextView btAnadirEquipoDeTraslado = (TextView) findViewById(R.id.btAnadirEquipoTraslado);
        btAnadirEquipoDeTraslado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                equipoDeCortejo = false;
                equipoDeInstalacion = false;
                isArticuloDeVelacion = false;
                scannerAtaurna = false;
                equipoRecoleccion = false;
                equipoDeTraslado = true;
                new IntentIntegrator(BitacoraDetalle.this).initiateScan();
            }
        });

        TextView btAnadirEquipoCortejo = (TextView) findViewById(R.id.btAnadirEquipoCortejo);
        btAnadirEquipoCortejo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                equipoDeCortejo = true;
                equipoDeInstalacion = false;
                isArticuloDeVelacion = false;
                scannerAtaurna = false;
                equipoRecoleccion = false;
                equipoDeTraslado = false;
                new IntentIntegrator(BitacoraDetalle.this).initiateScan();
            }
        });
        btScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ApplicationResourcesProvider.checkInternetConnection()){
                    scannerAtaurna = true;
                    equipoDeCortejo = false;
                    equipoDeInstalacion = false;
                    isArticuloDeVelacion = false;
                    equipoRecoleccion = false;
                    equipoDeTraslado = false;
                    new IntentIntegrator(BitacoraDetalle.this).initiateScan();
                }else{
                    showErrorDialog("Necesitas conexi??n a internet para realizar el escaneo del Ata??d o Urna.", "");
                }
            }
        });

        btAnadirEquipoRecoleccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scannerAtaurna = false;
                equipoDeCortejo = false;
                equipoDeInstalacion = false;
                isArticuloDeVelacion = false;
                equipoRecoleccion = true;
                equipoDeTraslado = false;
                new IntentIntegrator(BitacoraDetalle.this).initiateScan();
            }
        });

        btFinalizarBitacora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showErrorDialog("Estas a punto de terminar la bit??cora\n??Est??s de acuerdo?", bitacora);

                //verificar aqui si la bitacora esta completa de equipos de velacion.
                /*if(DatabaseAssistant.articulosDeVelacionYCortejoEstanCompletos(bitacora)
                        && DatabaseAssistant.articulosDeRecoleccionEstanCompletos(bitacora)
                        && DatabaseAssistant.articulosDeTrasladoEstanCompletos(bitacora)) {
                    //Realizar el cerrado de la bitacora por medio de request
                    showErrorDialog("Estas a punto de terminar la bit??cora\n??Est??s de acuerdo?", bitacora);
                }
                else {
                    //showErrorDialog("No puedes terminar la bit??cora, porque los articulos de velaci??n no estan completos, verifica nuevamente.", bitacora);
                    showErrorDialog("Parece que los articulos de velaci??n no estan completos, necesitas autorizaci??n para terminar la bit??cora.", bitacora);
                }*/

            }
        });

        btSalida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] evento = DatabaseAssistant.getUltimoEvento(bitacora);
                String destino = evento[1];

                if(!destino.equals("") || !destino.isEmpty())
                    showErrorDialog("Ya tienes una salida actualmente, debes registrar una llegada al destino.","");
                else{
                    showSalidaLlegada(1, true, evento[0]);
                    /*String[] datosBitacoraActiva = DatabaseAssistant.getDatosDeBitacoraActiva(bitacora);
                    if (datosBitacoraActiva.length > 0) {
                        ApplicationResourcesProvider.insertarMovimiento(datosBitacoraActiva[1], datosBitacoraActiva[2], "REGISTRO DE SALIDA");
                    }*/
                }
            }
        });

        btllegada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] evento = DatabaseAssistant.getUltimoEvento(bitacora);
                String destino = evento[1];

                if(destino.equals("") || destino.isEmpty()){
                    showErrorDialog("Ya tienes una llegada actualmente, debes registrar una salida.","");
                }else{
                    registrarLlegadaSinPedirDestino("" +destino, 2 );
                    /*String[] datosBitacoraActiva = DatabaseAssistant.getDatosDeBitacoraActiva(bitacora);
                    if (datosBitacoraActiva.length > 0) {
                        ApplicationResourcesProvider.insertarMovimiento(datosBitacoraActiva[1], datosBitacoraActiva[2], "REGISTRO DE LLEGADA");
                    }*/
                }
            }
        });

        NeumorphButton btGuardarProcedimiento =(NeumorphButton) findViewById(R.id.btGuardarProcedimiento);
        btGuardarProcedimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (spTipoProcedimiento.getSelectedItemPosition() != 0 && spLaboratorios.getSelectedItemPosition() != 0)
                {
                    try
                    {
                        String ropaEntregada = "", ropaEncapsulada = "";
                        if (rbSi.isChecked())
                            ropaEntregada = "SI";
                        else if (rbNo.isChecked())
                            ropaEntregada = "NO";
                        else
                            ropaEntregada = "Desconocido";


                        if (rbSiEncapsulado.isChecked())
                            ropaEncapsulada = "ENCAPSULADA";
                        else if (rbNoEncapsulado.isChecked())
                            ropaEncapsulada = "SIN ENCAPSULAR";
                        else
                            ropaEncapsulada = "Desconocido";


                        JSONObject jsonGeneral = new JSONObject();
                        try {
                            jsonDocumentos.put("INE_FINADO", cbIneFinado.isChecked());
                            jsonDocumentos.put("ACTA_NACIMIENTO_FINADO", cbActaDeNacimientoFinado.isChecked());
                            jsonDocumentos.put("INE_FAMILIAR", cbIneFamiliar.isChecked());
                            jsonDocumentos.put("ACTA_NACIMIENTO_FAMILIAR", cbActaDeNacimientoFamiliar.isChecked());
                            jsonDocumentos.put("CERTIFICADO_DEFUNCION", cbCertificadoDeDefuncion.isChecked());
                            jsonDocumentos.put("TITULO_PROPIEDAD_CEMENTERIO", cbTituloDePropiedadCementerio.isChecked());
                            jsonDocumentos.put("OTROS_DOCUMENTOS", cbOtros.isChecked());
                            jsonGeneral.put("Documentos", jsonDocumentos);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Adicional.executeQuery("DELETE FROM ADICIONAL WHERE bitacora = '" + bitacora + "'");

                        String seleccionarHora="00:00:00";
                        if (tvSeleccionarHora.getText().toString().equals("Selecciona aqui..."))
                            seleccionarHora = "00:00:00";

                        DatabaseAssistant.insertarInformacionAdicional(
                                "" + seleccionarHora,
                                "" + ropaEntregada,
                                "" + lugarDeVelacion,
                                "" + tipoDeServicio,
                                "" + "etNumeroEquipoInstalacion.getText().toString()",
                                "" + etObservacionesInstalacion.getText().toString(),
                                "" + "etNumeroEquipoCortejo.getText().toString()",
                                "" + etObservacionesCortejo.getText().toString(),
                                "" + jsonGeneral.toString(),
                                bitacora,
                                ropaEncapsulada,
                                "" + etObservacionesRecoleccion.getText().toString(),
                                "" + etObservacionesTraslado.getText().toString(),
                                tipoProcedimiento.equals("Selecciona una opci??n...") ? "" : tipoProcedimiento,
                                laboratorio.equals("Selecciona una opci??n...") ? "" : laboratorio,
                                DatabaseAssistant.getIDFromLaboratorioString(laboratorio)
                        );

                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup) findViewById(R.id.relativeLayout1));
                        LottieAnimationView lottieAnimationView = view.findViewById(R.id.imageView1);
                        lottieAnimationView.setAnimation("success_toast.json");
                        lottieAnimationView.loop(false);
                        lottieAnimationView.playAnimation();
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(view);
                        toast.show();

                        LinearLayout expandView = (LinearLayout) findViewById(R.id.expand_view);
                        expandView.setVisibility(View.GONE);
                        btGuardarProcedimiento.setEnabled(false);
                    } catch (Throwable e) {
                        Log.d(TAG, "onClick: Error: " + e.getMessage());
                    }
                }else
                    Toast.makeText(BitacoraDetalle.this, "Tienes que seleccionar el procedimiento y el laboratorio.", Toast.LENGTH_SHORT).show();

            }
        });


        NeumorphButton btGuardarLaboratorio =(NeumorphButton) findViewById(R.id.btGuardarLaboratorio);
        btGuardarLaboratorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (spTipoProcedimiento.getSelectedItemPosition() != 0 && spLaboratorios.getSelectedItemPosition() != 0)
                {
                    try {
                        String ropaEntregada = "", ropaEncapsulada = "";
                        if (rbSi.isChecked())
                            ropaEntregada = "SI";
                        else if (rbNo.isChecked())
                            ropaEntregada = "NO";
                        else
                            ropaEntregada = "Desconocido";


                        if (rbSiEncapsulado.isChecked())
                            ropaEncapsulada = "ENCAPSULADA";
                        else if (rbNoEncapsulado.isChecked())
                            ropaEncapsulada = "SIN ENCAPSULAR";
                        else
                            ropaEncapsulada = "Desconocido";


                        JSONObject jsonGeneral = new JSONObject();
                        try {
                            jsonDocumentos.put("INE_FINADO", cbIneFinado.isChecked());
                            jsonDocumentos.put("ACTA_NACIMIENTO_FINADO", cbActaDeNacimientoFinado.isChecked());
                            jsonDocumentos.put("INE_FAMILIAR", cbIneFamiliar.isChecked());
                            jsonDocumentos.put("ACTA_NACIMIENTO_FAMILIAR", cbActaDeNacimientoFamiliar.isChecked());
                            jsonDocumentos.put("CERTIFICADO_DEFUNCION", cbCertificadoDeDefuncion.isChecked());
                            jsonDocumentos.put("TITULO_PROPIEDAD_CEMENTERIO", cbTituloDePropiedadCementerio.isChecked());
                            jsonDocumentos.put("OTROS_DOCUMENTOS", cbOtros.isChecked());
                            jsonGeneral.put("Documentos", jsonDocumentos);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Adicional.executeQuery("DELETE FROM ADICIONAL WHERE bitacora = '" + bitacora + "'");

                        String seleccionarHora="00:00:00";
                        if (tvSeleccionarHora.getText().toString().equals("Selecciona aqui..."))
                            seleccionarHora = "00:00:00";


                        DatabaseAssistant.insertarInformacionAdicional(
                                "" + seleccionarHora,
                                "" + ropaEntregada,
                                "" + lugarDeVelacion,
                                "" + tipoDeServicio,
                                "" + "etNumeroEquipoInstalacion.getText().toString()",
                                "" + etObservacionesInstalacion.getText().toString(),
                                "" + "etNumeroEquipoCortejo.getText().toString()",
                                "" + etObservacionesCortejo.getText().toString(),
                                "" + jsonGeneral.toString(),
                                bitacora,
                                ropaEncapsulada,
                                "" + etObservacionesRecoleccion.getText().toString(),
                                "" + etObservacionesTraslado.getText().toString(),
                                tipoProcedimiento.equals("Selecciona una opci??n...") ? "" : tipoProcedimiento,
                                laboratorio.equals("Selecciona una opci??n...") ? "" : laboratorio,
                                DatabaseAssistant.getIDFromLaboratorioString(laboratorio)
                        );

                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup) findViewById(R.id.relativeLayout1));
                        LottieAnimationView lottieAnimationView = view.findViewById(R.id.imageView1);
                        lottieAnimationView.setAnimation("success_toast.json");
                        lottieAnimationView.loop(false);
                        lottieAnimationView.playAnimation();
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(view);
                        toast.show();

                        LinearLayout expandView = (LinearLayout) findViewById(R.id.expand_view);
                        expandView.setVisibility(View.GONE);
                        btGuardarLaboratorio.setEnabled(false);
                    } catch (Throwable e) {
                        Log.d(TAG, "onClick: Error: " + e.getMessage());
                    }
                }else
                    Toast.makeText(BitacoraDetalle.this, "Tienes que seleccionar el procedimiento y el laboratorio.", Toast.LENGTH_SHORT).show();

            }
        });


        llenarCamposDeDetalle(bitacora);
        FrameLayout cardContainerDos = (FrameLayout) findViewById(R.id.card_container);
        cardContainerDos.performClick();
    }

    private void setTypefaceTextViews() {
        TextView tv_mensaje_loading = (TextView) findViewById(R.id.tv_mensaje_loading);
        tv_mensaje_loading.setTypeface(ApplicationResourcesProvider.regular);

        TextView tvTipoBitacora = (TextView) findViewById(R.id.tvTipoBitacora);
        tvTipoBitacora.setTypeface(ApplicationResourcesProvider.bold);

        TextView tvBitacora = (TextView) findViewById(R.id.tvBitacora);
        tvBitacora.setTypeface(ApplicationResourcesProvider.bold);

        TextView tvSubtitulo = (TextView) findViewById(R.id.tvSubtitulo);
        tvSubtitulo.setTypeface(ApplicationResourcesProvider.regular);

        TextView tvOrigen = (TextView) findViewById(R.id.tvOrigen);
        tvOrigen.setTypeface(ApplicationResourcesProvider.bold);

        TextView tvDestino = (TextView) findViewById(R.id.tvDestino);
        tvDestino.setTypeface(ApplicationResourcesProvider.bold);

        TextView tvSalidasLlegadas = (TextView) findViewById(R.id.tvSalidasLlegadas);
        tvSalidasLlegadas.setTypeface(ApplicationResourcesProvider.bold);

        TextView tvAtaurna = (TextView) findViewById(R.id.tvAtaurna);
        tvAtaurna.setTypeface(ApplicationResourcesProvider.light);

        TextView tvSaida = (TextView) findViewById(R.id.tvSaida);
        tvSaida.setTypeface(ApplicationResourcesProvider.light);

        TextView tvllegada = (TextView) findViewById(R.id.tvllegada);
        tvllegada.setTypeface(ApplicationResourcesProvider.light);


        TextView tvPrimercontacto = (TextView) findViewById(R.id.tvPrimercontacto);
        tvPrimercontacto.setTypeface(ApplicationResourcesProvider.bold);

        TextView tvNombre = (TextView) findViewById(R.id.tvNombre);
        tvNombre.setTypeface(ApplicationResourcesProvider.bold);

        TextView tvNombrePrimerContacto = (TextView) findViewById(R.id.tvNombrePrimerContacto);
        tvNombrePrimerContacto.setTypeface(ApplicationResourcesProvider.regular);

        TextView tvDomi = (TextView) findViewById(R.id.tvDomi);
        tvDomi.setTypeface(ApplicationResourcesProvider.bold);
        TextView tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvAddress.setTypeface(ApplicationResourcesProvider.regular);


        TextView tvTele = (TextView) findViewById(R.id.tvTele);
        tvTele.setTypeface(ApplicationResourcesProvider.bold);
        TextView tvPhones = (TextView) findViewById(R.id.tvPhones);
        tvPhones.setTypeface(ApplicationResourcesProvider.regular);


        TextView tvDetallesServ = (TextView) findViewById(R.id.tvDetallesServ);
        tvDetallesServ.setTypeface(ApplicationResourcesProvider.bold);

        TextView tvVhocer = (TextView) findViewById(R.id.tvVhocer);
        tvVhocer.setTypeface(ApplicationResourcesProvider.bold);
        TextView tvChofer = (TextView) findViewById(R.id.tvChofer);
        tvChofer.setTypeface(ApplicationResourcesProvider.regular);


        TextView tvAtudadnnnte = (TextView) findViewById(R.id.tvAtudadnnnte);
        tvAtudadnnnte.setTypeface(ApplicationResourcesProvider.bold);
        TextView tvAyudante = (TextView) findViewById(R.id.tvAyudante);
        tvAyudante.setTypeface(ApplicationResourcesProvider.regular);


        TextView dsfds = (TextView) findViewById(R.id.dsfds);
        dsfds.setTypeface(ApplicationResourcesProvider.bold);
        TextView tvVehiculo = (TextView) findViewById(R.id.tvVehiculo);
        tvVehiculo.setTypeface(ApplicationResourcesProvider.regular);


        TextView tvsdsc = (TextView) findViewById(R.id.tvsdsc);
        tvsdsc.setTypeface(ApplicationResourcesProvider.bold);
        TextView tvCodigoAtaud = (TextView) findViewById(R.id.tvCodigoAtaud);
        tvCodigoAtaud.setTypeface(ApplicationResourcesProvider.regular);


        TextView tdddvsdsc = (TextView) findViewById(R.id.tdddvsdsc);
        tdddvsdsc.setTypeface(ApplicationResourcesProvider.bold);
        TextView tvPanteon = (TextView) findViewById(R.id.tvPanteon);
        tvPanteon.setTypeface(ApplicationResourcesProvider.regular);


        TextView tvCrema = (TextView) findViewById(R.id.tvCrema);
        tvCrema.setTypeface(ApplicationResourcesProvider.bold);
        TextView tvCrematorio = (TextView) findViewById(R.id.tvCrematorio);
        tvCrematorio.setTypeface(ApplicationResourcesProvider.regular);


        TextView tvdsfsdsdf = (TextView) findViewById(R.id.tvdsfsdsdf);
        tvdsfsdsdf.setTypeface(ApplicationResourcesProvider.bold);
        TextView tvInicioVelacion = (TextView) findViewById(R.id.tvInicioVelacion);
        tvInicioVelacion.setTypeface(ApplicationResourcesProvider.regular);

        TextView sddsfsdffds = (TextView) findViewById(R.id.sddsfsdffds);
        sddsfsdffds.setTypeface(ApplicationResourcesProvider.bold);
        TextView tvInicioCotejo = (TextView) findViewById(R.id.tvInicioCotejo);
        tvInicioCotejo.setTypeface(ApplicationResourcesProvider.regular);

        TextView asdasdds = (TextView) findViewById(R.id.asdasdds);
        asdasdds.setTypeface(ApplicationResourcesProvider.bold);
        TextView tvTemplo = (TextView) findViewById(R.id.tvTemplo);
        tvTemplo.setTypeface(ApplicationResourcesProvider.regular);

        TextView tvAdicional = (TextView) findViewById(R.id.tvAdicional);
        tvAdicional.setTypeface(ApplicationResourcesProvider.bold);
        TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(ApplicationResourcesProvider.regular);

        TextView proedimiento = (TextView) findViewById(R.id.proedimiento);
        proedimiento.setTypeface(ApplicationResourcesProvider.bold);

        TextView laboratorio = (TextView) findViewById(R.id.laboratorio);
        laboratorio.setTypeface(ApplicationResourcesProvider.bold);


        TextView subtitle1 =(TextView) findViewById(R.id.subtitle1);
        subtitle1.setTypeface(ApplicationResourcesProvider.regular);

        TextView subtitle2 =(TextView) findViewById(R.id.subtitle2);
        subtitle2.setTypeface(ApplicationResourcesProvider.regular);

    }

    private void llenarCamposDeDetalle(String bitacora) {
        List<Eventos> eventosList = Eventos.findWithQuery(Eventos.class, "SELECT * FROM EVENTOS where bitacora ='" + bitacora +"' ORDER BY id DESC LIMIT 1");
        if(eventosList.size()>0)
        {
            TextView tvBitacora, tvChofer, tvAyudante, tvVehiculo, tvAddress, tvPhones, tvNombrePrimerContacto, tvTipoBitacora;

            tvBitacora =( TextView) findViewById(R.id.tvBitacora);
            tvChofer =( TextView) findViewById(R.id.tvChofer);
            tvAyudante =( TextView) findViewById(R.id.tvAyudante);
            tvVehiculo =( TextView) findViewById(R.id.tvVehiculo);
            tvAddress =( TextView) findViewById(R.id.tvAddress);
            tvPhones =( TextView) findViewById(R.id.tvPhones);
            tvTipoBitacora =( TextView) findViewById(R.id.tvTipoBitacora);
            tvNombrePrimerContacto =( TextView) findViewById(R.id.tvNombrePrimerContacto);

            tvBitacora.setText(eventosList.get(0).getBitacora());


            tvTipoBitacora.setText(eventosList.get(0).getMovimiento());
            TextPaint paint = tvTipoBitacora.getPaint();
            float width = paint.measureText(eventosList.get(0).getMovimiento());
            Shader textShader = new LinearGradient(0, 0, width, tvTipoBitacora.getTextSize(),
                    new int[]{
                            Color.parseColor("#a63fff"),
                            Color.parseColor("#d40aff"),
                    }, null, Shader.TileMode.CLAMP);
            tvTipoBitacora.getPaint().setShader(textShader);


            tvChofer.setText(eventosList.get(0).getChofer());
            if(eventosList.get(0).getNombre() != null) {
                if (eventosList.get(0).getNombre().equals(""))
                    tvNombrePrimerContacto.setText("No capturado");
                else
                    tvNombrePrimerContacto.setText(eventosList.get(0).getNombre());
            }
            else
                tvNombrePrimerContacto.setText("No capturado");

            tvAyudante.setText(eventosList.get(0).getAyudante());
            tvVehiculo.setText(eventosList.get(0).getCarro());


            if(eventosList.get(0).getDomicilio() != null) {
                if (eventosList.get(0).getDomicilio().equals(""))
                    tvAddress.setText("No capturado");
                else
                    tvAddress.setText(eventosList.get(0).getDomicilio());
            }
            else
                tvAddress.setText("No capturado");


            tvPhones.setText(eventosList.get(0).getTelefonos());

            //************************
            /*TextView tvAtaud = (TextView) findViewById(R.id.tvCodigoAtaud);
            tvAtaud.setText(DatabaseAssistant.getAtaudPorBitacora(bitacora));
            LinearLayout layoutAtaud = (LinearLayout) findViewById(R.id.layoutAtaud);
            layoutAtaud.setVisibility(View.VISIBLE);*/
            llenarCamposDeAtaurna(bitacora);

            TextView tvPanteon = (TextView) findViewById(R.id.tvPanteon);
            tvPanteon.setText(DatabaseAssistant.getPanteonPorBitacora(bitacora));
            LinearLayout layoutPanteon = (LinearLayout) findViewById(R.id.layoutPanteon);
            layoutPanteon.setVisibility(View.VISIBLE);


            TextView tvInicioVelacion = (TextView) findViewById(R.id.tvInicioVelacion);
            tvInicioVelacion.setText(DatabaseAssistant.getHoraInicioDeVelacion(bitacora));

            TextView tvInicioCotejo = (TextView) findViewById(R.id.tvInicioCotejo);
            tvInicioCotejo.setText(DatabaseAssistant.getHoraInicioDeCortejo(bitacora));

            TextView tvTemplo = (TextView) findViewById(R.id.tvTemplo);
            tvTemplo.setText(DatabaseAssistant.getTemplo(bitacora));


            TextView tvCrematorio = (TextView) findViewById(R.id.tvCrematorio);
            tvCrematorio.setText(DatabaseAssistant.getCrematorioPorBitacora(bitacora));
            LinearLayout layoutSevicio = (LinearLayout) findViewById(R.id.layoutSevicio);
            layoutSevicio.setVisibility(View.VISIBLE);
            //*************************

            updateLastesPlace(eventosList.get(0).getBitacora());



            String[] procedimientos ={"Embalsamado", "Arreglo est??tico"};
            ArrayAdapter<String> adapterProcedimientos = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, procedimientos);
            spTipoProcedimiento.setAdapter(adapterProcedimientos);

        }
        else
            Log.d(TAG, "consultarBitacorasActivas: No tenemos bitacoras acivas por el momento");
    }

    @Override
    public void onResume() {
        super.onResume();
        LinearLayout layoutCodigosDeBarras = findViewById(R.id.layoutCodigosDeBarras);
        String codigoBarras = DatabaseAssistant.getCodigoDeBarrasPorBitacora(bitacora);
        if(codigoBarras.length() > 0 )
        {
            layoutCodigosDeBarras.setVisibility(View.VISIBLE);
            TextView tvCodigos =( TextView) findViewById(R.id.tvCodigos);
            tvCodigos.setVisibility(View.VISIBLE);
            tvCodigos.setText(codigoBarras);
        }else
            layoutCodigosDeBarras.setVisibility(View.GONE);
    }

    public void showErrorDialog(final String codeError, String bitacora) {
        final NeumorphButton btNo, btSi;
        TextView tvCodeError, tvBitacora;
        EditText etDescripcionPeticion;
        TextInputLayout etDescripcionPeticion2;
        Dialog dialogoError = new Dialog(BitacoraDetalle.this);
        dialogoError.setContentView(R.layout.layout_error);
        dialogoError.setCancelable(true);
        btNo = (NeumorphButton) dialogoError.findViewById(R.id.btNo);
        btSi = (NeumorphButton) dialogoError.findViewById(R.id.btSi);
        etDescripcionPeticion = (EditText) dialogoError.findViewById(R.id.etDescripcionPeticion);
        etDescripcionPeticion2 = (TextInputLayout) dialogoError.findViewById(R.id.etDescripcionPeticion2);
        tvCodeError = (TextView) dialogoError.findViewById(R.id.tvCodeError);
        tvBitacora = (TextView) dialogoError.findViewById(R.id.tvBitacora);
        tvCodeError.setText(codeError);
        etDescripcionPeticion2.setVisibility(View.GONE);

        if(codeError.equals("Estas a punto de terminar la bit??cora\n??Est??s de acuerdo?")){
            tvBitacora.setText(bitacora);
            tvBitacora.setVisibility(View.VISIBLE);
        }
        else if( codeError.equals("Parece que los articulos de velaci??n no estan completos, necesitas autorizaci??n para terminar la bit??cora.") ){
            //etDescripcionPeticion.setVisibility(View.VISIBLE);
            etDescripcionPeticion2.setVisibility(View.VISIBLE);
            btSi.setText("Solicitar autorizaci??n");
            btNo.setText("Cancelar");
        }
        else{
            tvBitacora.setText("");
            tvBitacora.setVisibility(View.GONE);
        }

        if (codeError.equals("No hay conexi??n a internet")
                || codeError.equals("Ya tienes una salida actualmente, debes registrar una llegada al destino.")
                || codeError.equals("Ya tienes una llegada actualmente, debes registrar una salida.")
                || codeError.equals("El c??digo no corresponde a un Ata??d o Urna, verifica nuevamente")
                || codeError.equals("El c??digo no corresponde a un art??culo de velaci??n, verifica nuevamente.")
                || codeError.equals("C??digo incorrecto, asegurate que sea un c??digo de Ata??d o de Urna. Verifica nuevamente")
                || codeError.equals("C??digo repetido o desconocido para articulos de recolecci??n, verifica nuevamente")
                || codeError.equals("C??digo repetido o desconocido para articulos de instalaci??n, verifica nuevamente")
                || codeError.equals("C??digo repetido o desconocido para articulos de Cortejo, verifica nuevamente")
                || codeError.equals("C??digo repetido o desconocido para articulos de Traslado, verifica nuevamente")
                || codeError.equals("El c??digo ya tiene un registro de salida y entrada de inventario.")
                || codeError.equals("Necesitas conexi??n a internet para realizar el escaneo del Ata??d o Urna.")
                || codeError.equals("Parece que el ata??d o urna no esta disponible, ya que se encuentra asignado(a) a otra bit??cora")
                || codeError.equals("Antes de guardar debes eliminar el Ata??d o la Urna que tienes registrada")
                || codeError.equals("Salida invalida, tienes que seleccionar el destino diferente a tu llegada."))
            btSi.setVisibility(View.GONE);


        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogoError.dismiss();
            }
        });

        btSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(codeError.equals("Estas a punto de terminar la bit??cora\n??Est??s de acuerdo?")) {
                    registrarBitacoraTerminada(bitacora);
                }
                else if (codeError.equals("Parece que los articulos de velaci??n no estan completos, necesitas autorizaci??n para terminar la bit??cora.")) {


                    /*dialogoError.dismiss();
                    descripcionPeticion = etDescripcionPeticion.getText().toString();
                    doRequestForEndBinnacle();
                    requesterCanceled = false;
                    status = 0;*/

                    if(!etDescripcionPeticion.getText().toString().equals("")) {
                        dialogoError.dismiss();
                        descripcionPeticion = etDescripcionPeticion.getText().toString();
                        doRequestForEndBinnacle();
                        requesterCanceled = false;
                        status = 0;
                    }else
                    {
                        Toast.makeText(getApplicationContext(), "Ingresa el motivo de petici??n.", Toast.LENGTH_SHORT).show();
                        etDescripcionPeticion.setError("Debes inigresa el motivo de petici??n");
                    }


                }
                else
                    dialogoError.dismiss();
            }
        });

        dialogoError.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogoError.show();
    }

    void hideLoadingDialog(){
        btFinalizarBitacora.setVisibility(View.VISIBLE);
        RelativeLayout frameLoading = (RelativeLayout) findViewById(R.id.frameLoading);
        frameLoading.setVisibility(View.GONE);
    }

    void showLoadingDialog(){
        btFinalizarBitacora.setVisibility(View.GONE);
        RelativeLayout frameLoading = (RelativeLayout) findViewById(R.id.frameLoading);
        frameLoading.setVisibility(View.VISIBLE);
    }

    public void showSalidaLlegada(int tipo, boolean pedirDestino, String destinoAnterior) {
        TextView etTitulo, etSalidaLlegada, etDestino;
        Spinner spLugares, spDestino;
        NeumorphButton btCancelar, btGuardar;
        dialogoSalidas.setContentView(R.layout.layout_entrada_salida);
        dialogoSalidas.setCancelable(false);
        etTitulo = (TextView) dialogoSalidas.findViewById(R.id.etTitulo);
        etSalidaLlegada = (TextView) dialogoSalidas.findViewById(R.id.etSalidaLlegada);
        etDestino = (TextView) dialogoSalidas.findViewById(R.id.etDestino);
        spLugares = (Spinner) dialogoSalidas.findViewById(R.id.spLugares);
        spDestino = (Spinner) dialogoSalidas.findViewById(R.id.spDestino);
        btGuardar = (NeumorphButton) dialogoSalidas.findViewById(R.id.btGuardar);
        btCancelar = (NeumorphButton) dialogoSalidas.findViewById(R.id.btCancelar);

        ArrayAdapter<String> adapterColonias = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.getLugares());

        if(pedirDestino) {
            spDestino.setAdapter(adapterColonias);
            spLugares.setVisibility(View.GONE);
            etSalidaLlegada.setVisibility(View.GONE);
            spDestino.setVisibility(View.VISIBLE);
            etDestino.setVisibility(View.VISIBLE);
        }
        else
            spLugares.setAdapter(adapterColonias);


        if (tipo == 1) {
            etTitulo.setText("Nueva Salida");
            etSalidaLlegada.setText("Lugar de salida");
        } else if (tipo == 2) {
            etTitulo.setText("Nueva Llegada");
            etSalidaLlegada.setText("Lugar de llegada");
        }


        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoSalidas.dismiss();
            }
        });

        btGuardar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View v) {

                if (!spDestino.getSelectedItem().toString().equals(destinoAnterior)) {
                    try {
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
                        String hora = horaFormat.format(cal.getTime());
                        String[] datosBitacoraActiva = DatabaseAssistant.getDatosDeBitacoraActiva(bitacora);
                        String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();

                        if (datosBitacoraActiva.length > 0) {
                            DatabaseAssistant.insertarEventos(
                                    "" + datosBitacoraActiva[0],
                                    "" + datosBitacoraActiva[1],
                                    "" + datosBitacoraActiva[2],
                                    "" + datosBitacoraActiva[3],
                                    pedirDestino ? datosBitacoraActiva[11] : spLugares.getSelectedItem().toString(),
                                    "" + dateFormat.format(new Date()),
                                    "" + hora,
                                    "" + datosBitacoraActiva[4],
                                    "" + Double.parseDouble(arregloCoordenadas[0]),
                                    "" + Double.parseDouble(arregloCoordenadas[1]),
                                    "" + datosBitacoraActiva[5],
                                    "" + (tipo == 1 ? "Salida" : tipo == 2 ? "Llegada" : ""),
                                    "" + datosBitacoraActiva[6],
                                    "" + datosBitacoraActiva[7],
                                    "" + datosBitacoraActiva[8],
                                    pedirDestino ? spDestino.getSelectedItem().toString() : datosBitacoraActiva[10],
                                    "0",
                                    "" + datosBitacoraActiva[12]
                            );

                            updateLastesPlace(bitacora);

                            ApplicationResourcesProvider.insertarMovimiento( datosBitacoraActiva[1], datosBitacoraActiva[2], "REGISTRO DE " + (tipo == 1 ? "SALIDA: " : tipo == 2 ? "LLEGADA: " : "") + datosBitacoraActiva[0]);

                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup)findViewById(R.id.relativeLayout1));
                            LottieAnimationView lottieAnimationView = view.findViewById(R.id.imageView1);
                            lottieAnimationView.setAnimation("success_toast.json");
                            lottieAnimationView.loop(false);
                            lottieAnimationView.playAnimation();
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(view);
                            toast.show();

                            /*LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup) findViewById(R.id.relativeLayout1));
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(view);
                            toast.show();*/
                            dialogoSalidas.dismiss();
                        }

                    } catch (Throwable e) {
                        Log.e(TAG, "Error en click de salidas y llegadas: " + e.getMessage());
                    }
                }else
                    showErrorDialog("Salida invalida, tienes que seleccionar el destino diferente a tu llegada.", "");


            }
        });
        dialogoSalidas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogoSalidas.show();
    }


    void registrarLlegadaSinPedirDestino(String lugar, int tipo){
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
            String hora = horaFormat.format(cal.getTime());
            String[] datosBitacoraActiva = DatabaseAssistant.getDatosDeBitacoraActiva(bitacora);
            String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();

            if (datosBitacoraActiva.length > 0) {
                DatabaseAssistant.insertarEventos(
                        "" + datosBitacoraActiva[0],
                        "" + datosBitacoraActiva[1],
                        "" + datosBitacoraActiva[2],
                        "" + datosBitacoraActiva[3],
                        "" + lugar,
                        "" + dateFormat.format(new Date()),
                        "" + hora,
                        "" + datosBitacoraActiva[4],
                        "" + Double.parseDouble(arregloCoordenadas[0]),
                        "" + Double.parseDouble(arregloCoordenadas[1]),
                        "" + datosBitacoraActiva[5],
                        "" + (tipo == 1 ? "Salida" : tipo == 2 ? "Llegada" : ""),
                        "" + datosBitacoraActiva[6],
                        "" + datosBitacoraActiva[7],
                        "" + datosBitacoraActiva[8],
                        "",
                        "0",
                        "" + datosBitacoraActiva[12]
                );

                updateLastesPlace(bitacora);

                ApplicationResourcesProvider.insertarMovimiento( datosBitacoraActiva[1], datosBitacoraActiva[2], "REGISTRO DE " + (tipo == 1 ? "SALIDA: " : tipo == 2 ? "LLEGADA: " : "") + datosBitacoraActiva[0]);

                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup)findViewById(R.id.relativeLayout1));
                LottieAnimationView lottieAnimationView = view.findViewById(R.id.imageView1);
                lottieAnimationView.setAnimation("success_toast.json");
                lottieAnimationView.loop(false);
                lottieAnimationView.playAnimation();
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view);
                toast.show();


                /*LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup)findViewById(R.id.relativeLayout1));
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view);
                toast.show();*/
                //Toast.makeText(this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
            }

        } catch (Throwable e) {
            Log.e(TAG, "Error en click de salidas y llegadas: " + e.getMessage());
        }
    }

    public void updateLastesPlace(String bitacora)
    {
        ConstraintLayout layoutLastPlace = findViewById(R.id.layoutLastPlace);
        View vista1 =(View) findViewById(R.id.view2);
        View vista2 =(View) findViewById(R.id.viwwew);

        try {
            TextView tvUltimoLugar = findViewById(R.id.tvOrigen);
            TextView tvDestino = findViewById(R.id.tvDestino);

            String[] evento = DatabaseAssistant.getUltimoEvento(bitacora);

            if(!evento[1].equals("")) {
                tvUltimoLugar.setText("Salida:   " +evento[0]);
                tvDestino.setText("Destino:   " + evento[1]);
                vista1.setVisibility(View.VISIBLE);
                vista2.setVisibility(View.VISIBLE);
                tvDestino.setVisibility(View.VISIBLE);
            }else{
                tvUltimoLugar.setText("Llegada:    " + evento[0]);
                vista1.setVisibility(View.GONE);
                vista2.setVisibility(View.GONE);
                tvDestino.setVisibility(View.GONE);
            }

            /*if(evento[1].equals("") && evento[2].equals("Salida"))
                if(!dialogoSalidas.isShowing())
                    showSalidaLlegada(1, true);*/

            tvUltimoLugar.setVisibility(View.VISIBLE);
            layoutLastPlace.setVisibility(View.VISIBLE);

        } catch (Throwable e) {
            Log.e(TAG, e.getMessage());
            layoutLastPlace.setVisibility(View.GONE);
        }
    }


    void doRequestForEndBinnacle()
    {
        try {
            status = 0;
            showLoadingDialog();
            new Thread(() -> {
                while (status == 0)
                {
                    Log.d("TIMER", "doRequestForEndBinnacle: Iteracion de ciclo para saber si la bitacora fue autorizada a terminar");
                    if (ApplicationResourcesProvider.checkInternetConnection() && getApplicationContext() != null) {
                        runOnUiThread(this::getStatusBinaccle);
                    } else {
                        runOnUiThread(() -> {
                            if (getApplicationContext() != null) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Verifica tu conexi??n a internet", Toast.LENGTH_SHORT);toast.show();
                                hideLoadingDialog();
                            }
                        });
                        runOnUiThread(this::showLoadingDialog);
                    }
                    Log.d("TIMER", "doRequestForEndBinnacle: Esperando 5 segundos...");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        hideLoadingDialog();
                        Toast.makeText(this, "Error: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("TIMER", "doRequestForEndBinnacle: Error: " + e.getMessage());
                    }
                }
             Log.d("TIMER" ,"while(status == 0) -> status = " + status);
            }).start();
        }catch (Throwable e){
            Log.e("TIMER", "registrarBitacoraTerminada: " + e.getMessage());
            hideLoadingDialog();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    void registrarBitacoraTerminada(String bitacora)
    {
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
            String hora = horaFormat.format(cal.getTime());
            String[] datosBitacoraActiva = DatabaseAssistant.getLastedEvent(bitacora);
            String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();

            if (datosBitacoraActiva.length > 0) {
                DatabaseAssistant.insertarEventos(
                        "" + datosBitacoraActiva[0],
                        "" + datosBitacoraActiva[1],
                        "" + datosBitacoraActiva[2],
                        "" + datosBitacoraActiva[3],
                        "" + datosBitacoraActiva[5],
                        "" + dateFormat.format(new Date()),
                        "" + hora,
                        "" + datosBitacoraActiva[8],
                        "" + Double.parseDouble(arregloCoordenadas[0]),
                        "" + Double.parseDouble(arregloCoordenadas[1]),
                        "0",
                        "Terminada",
                        "" + datosBitacoraActiva[10],
                        "" + datosBitacoraActiva[4],
                        "" + datosBitacoraActiva[11],
                        "" + datosBitacoraActiva[6],
                        "0",
                        "" + datosBitacoraActiva[12]
                );

                DatabaseAssistant.updateBitacorasToCero(bitacora);

                ApplicationResourcesProvider.insertarMovimiento(datosBitacoraActiva[1], datosBitacoraActiva[2], "REGISTRO DE BIT??CORA TERMINADA: " + datosBitacoraActiva[0]);

                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup)findViewById(R.id.relativeLayout1));
                LottieAnimationView lottieAnimationView = view.findViewById(R.id.imageView1);
                lottieAnimationView.setAnimation("success_toast.json");
                lottieAnimationView.loop(false);
                lottieAnimationView.playAnimation();
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view);
                toast.show();
                finish();
            }
        }catch (Throwable e){
            Log.e(TAG, "registrarBitacoraTerminada: " + e.getMessage());
        }
    }

    private void obtenerHora(){
        String CERO = "0";
        String DOS_PUNTOS = ":";

        TimePickerDialog recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String horaFormateada =  (hourOfDay < 10)? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                String minutoFormateado = (minute < 10)? String.valueOf(CERO + minute):String.valueOf(minute);
                String AM_PM;
                if(hourOfDay < 12) {
                    AM_PM = "A.M.";
                } else {
                    AM_PM = "P.M.";
                }
                TextView tvSeleccionarHora = (TextView) findViewById(R.id.tvSeleccionarHora);
                tvSeleccionarHora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + " " + AM_PM);
                horaSeleccionadaDeRecoleccion = horaFormateada + DOS_PUNTOS + minutoFormateado + " " + AM_PM ;
            }
        }, hora, minuto, false);
        recogerHora.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() != null) {

                if(equipoDeInstalacion) {

                    if(result.getContents().length()<=6) {
                        boolean guardarEquipo = false, guardar4Candelabros = false;
                        String nombreDeEquipo = "DESCONOCIDO", inicialesDeEquipo = "";

                        if (!result.getContents().contains("AA")) {

                            inicialesDeEquipo = result.getContents().substring(0, 2);
                            List<CatalogoArticulos> catalogoArticulosList = CatalogoArticulos.findWithQuery(CatalogoArticulos.class, "SELECT * FROM catalogo_Articulos WHERE letras ='" + inicialesDeEquipo + "'");
                            if (catalogoArticulosList.size() > 0) {
                                Log.d(TAG, "onActivityResult: Azael si encontre informaci??n de articulos con las letras: " + inicialesDeEquipo);
                                nombreDeEquipo = catalogoArticulosList.get(0).getNombre();
                                guardarEquipo = true;

                                if (inicialesDeEquipo.equals("CB")) {
                                    guardar4Candelabros = true;
                                }
                            } else {
                                nombreDeEquipo = "DESCONOCIDO";
                                guardarEquipo = false;
                                guardar4Candelabros = false;
                            }

                            if (!nombreDeEquipo.equals("DESCONOCIDO") && !(inicialesDeEquipo.equals("CL") && nombreDeEquipo.equals("Candelabro"))) {
                                guardarEquipo = !DatabaseAssistant.yaExisteInformacionDelArticuloDeVelacionDeInstalacion("" + result.getContents(), "" + bitacora);
                            }

                            if (guardarEquipo) {
                                DatabaseAssistant.insertarEquipoInstalacion(
                                        "" + bitacora,
                                        "" + result.getContents(),
                                        "" + nombreDeEquipo,
                                        "" + (Preferences.getPreferenceIsbunker(BitacoraDetalle.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0"),
                                        "0"
                                );

                                if (guardar4Candelabros) {
                                    for (int i = 0; i <= 3; i++) {
                                        int random = (int) Math.floor(Math.random() * 247 + 1);
                                        String randomString = String.valueOf(random);
                                        String s = randomString.length() == 2 ? "CL00" : randomString.length() == 3 ? "CL0" : randomString.length() == 1 ? "CL000" : "CL000";
                                        String serie = s + random;
                                        DatabaseAssistant.insertarEquipoInstalacion(
                                                "" + bitacora,
                                                serie,
                                                "Candelabro",
                                                "" + (Preferences.getPreferenceIsbunker(BitacoraDetalle.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0"),
                                                "0"
                                        );
                                    }
                                } else
                                    Log.d(TAG, "onActivityResult: No se guardaran candelabros");

                                consultarEquiposDeInstalacion(bitacora);
                                Toast.makeText(this, "Equipo guardado correctamente", Toast.LENGTH_SHORT).show();
                                showAnimationFromSuccessRecord();
                            } else {
                                showErrorDialog("C??digo repetido o desconocido para articulos de instalaci??n, verifica nuevamente", "");
                            }


                        }
                        else
                            showErrorDialog("C??digo repetido o desconocido para articulos de instalaci??n, verifica nuevamente", "");
                    }
                    else {
                        showErrorDialog("El c??digo no corresponde a un art??culo de velaci??n, verifica nuevamente.", "");
                    }


                }else if(equipoDeCortejo){


                    //*********MODIFICACION *****************
                    if(result.getContents().length()<=6) {
                        boolean guardarEquipo = false, guardar4Candelabros = false;
                        String nombreDeEquipo = "DESCONOCIDO", inicialesDeEquipo = "";

                        if (!result.getContents().contains("AA")) {
                            inicialesDeEquipo = result.getContents().substring(0, 2);
                            List<CatalogoArticulos> catalogoArticulosList = CatalogoArticulos.findWithQuery(CatalogoArticulos.class, "SELECT * FROM catalogo_Articulos WHERE letras ='" + inicialesDeEquipo + "'");
                            if (catalogoArticulosList.size() > 0) {
                                Log.d(TAG, "onActivityResult: Azael si encontre informaci??n de articulos con las letras: " + inicialesDeEquipo);
                                nombreDeEquipo = catalogoArticulosList.get(0).getNombre();
                                guardarEquipo = true;

                                if (inicialesDeEquipo.equals("CB")) {
                                    guardar4Candelabros = true;
                                }
                            } else {
                                nombreDeEquipo = "DESCONOCIDO";
                                guardarEquipo = false;
                                guardar4Candelabros = false;
                            }

                            if (!nombreDeEquipo.equals("DESCONOCIDO") && !(result.getContents().contains("CL") && nombreDeEquipo.equals("Candelabro")))
                                guardarEquipo = !DatabaseAssistant.yaExisteInformacionDelArticuloDeVelacionDeCortejo("" + result.getContents(), "" + bitacora);


                            if (guardarEquipo) {
                                DatabaseAssistant.insertarEquipoCortejo(
                                        "" + bitacora,
                                        "" + result.getContents(),
                                        nombreDeEquipo,
                                        "" + (Preferences.getPreferenceIsbunker(BitacoraDetalle.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0")
                                );

                                if (guardar4Candelabros) {
                                    List<Equipoinstalacion> equipoinstalacionList = Equipoinstalacion.findWithQuery(Equipoinstalacion.class, "SELECT * FROM EQUIPOINSTALACION WHERE bitacora = '" + bitacora + "' ORDER BY fecha DESC");
                                    if (equipoinstalacionList.size() > 0) {
                                        Equipocortejo.executeQuery("DELETE FROM EQUIPOCORTEJO WHERE bitacora ='" + bitacora + "' and nombre ='Candelabro'");
                                        for (int i = 0; i <= equipoinstalacionList.size() - 1; i++) {
                                            if (equipoinstalacionList.get(i).getSerie().contains("CL")) {
                                                DatabaseAssistant.insertarEquipoCortejo(
                                                        "" + bitacora,
                                                        equipoinstalacionList.get(i).getSerie(),
                                                        equipoinstalacionList.get(i).getNombre(),
                                                        "" + (Preferences.getPreferenceIsbunker(BitacoraDetalle.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0")
                                                );
                                            } else
                                                Log.d(TAG, "onActivityResult: No se guarda informacion de candelabros de instalacion");
                                        }
                                    } else
                                        Log.d(TAG, "onActivityResult: Equipo de cortejo no contiene informaci??n");
                                } else {
                                    Log.d(TAG, "onActivityResult: No se guardaran candelabros");
                                }

                                consultarEquiposDeCortejo(bitacora);
                                Toast.makeText(this, "Equipo guardado correctamente", Toast.LENGTH_SHORT).show();
                                showAnimationFromSuccessRecord();
                            } else {
                                showErrorDialog("C??digo repetido o desconocido para articulos de Cortejo, verifica nuevamente", "");
                            }
                        } else
                            showErrorDialog("C??digo repetido o desconocido para articulos de Cortejo, verifica nuevamente", "");
                    }
                    else {
                        showErrorDialog("El c??digo no corresponde a un art??culo de velaci??n, verifica nuevamente.", "");
                    }
                    //*************************************

                    /*if(result.getContents().length()<=6) {

                        boolean guardarEquipo = false, guardar4Candelabros = false;
                        String nombreDeEquipo = "DESCONOCIDO";

                        {
                            if (result.getContents().contains("CF")) {
                                nombreDeEquipo = "Cafetera";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("CL")) {
                                nombreDeEquipo = "Candelabro";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("CB")) {
                                nombreDeEquipo = "Cristo y base";
                                guardarEquipo = true;
                                guardar4Candelabros = true;
                            } else if (result.getContents().contains("PD")) {
                                nombreDeEquipo = "Pedestal";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("BM")) {
                                nombreDeEquipo = "Biombo";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("AT")) {
                                nombreDeEquipo = "Ata??d de traslado";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("CP")) {
                                nombreDeEquipo = "Carrito pedestal";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("CI")) {
                                nombreDeEquipo = "Carrito infantil";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("CN")) {
                                nombreDeEquipo = "Candelero infantil";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("PI")) {
                                nombreDeEquipo = "Pedestal infantil";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            }else if (result.getContents().contains("KC")) {
                                nombreDeEquipo = "Kit de Cafeter??a";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            }else {
                                nombreDeEquipo = "DESCONOCIDO";
                                guardarEquipo = false;
                                guardar4Candelabros = false;
                            }

                        }


                        if(!nombreDeEquipo.equals("DESCONOCIDO") && !(result.getContents().contains("CL") && nombreDeEquipo.equals("Candelabro")))
                            guardarEquipo = !DatabaseAssistant.yaExisteInformacionDelArticuloDeVelacionDeCortejo("" + result.getContents(), "" + bitacora);


                        if (guardarEquipo) {
                            DatabaseAssistant.insertarEquipoCortejo(
                                    "" + bitacora,
                                    "" + result.getContents(),
                                    nombreDeEquipo,
                                    "" + (Preferences.getPreferenceIsbunker(BitacoraDetalle.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0")
                            );

                            if(guardar4Candelabros) {
                                List<Equipoinstalacion> equipoinstalacionList = Equipoinstalacion.findWithQuery(Equipoinstalacion.class, "SELECT * FROM EQUIPOINSTALACION WHERE bitacora = '"+ bitacora +"' ORDER BY fecha DESC");
                                if(equipoinstalacionList.size() > 0){
                                    Equipocortejo.executeQuery("DELETE FROM EQUIPOCORTEJO WHERE bitacora ='" + bitacora + "' and nombre ='Candelabro'");
                                    for(int i =0; i<=equipoinstalacionList.size()-1; i++){
                                        if(equipoinstalacionList.get(i).getSerie().contains("CL")){
                                            DatabaseAssistant.insertarEquipoCortejo(
                                                    "" + bitacora,
                                                    equipoinstalacionList.get(i).getSerie(),
                                                    equipoinstalacionList.get(i).getNombre(),
                                                    "" + (Preferences.getPreferenceIsbunker(BitacoraDetalle.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0")
                                            );
                                        }else
                                            Log.d(TAG, "onActivityResult: No se guarda informacion de candelabros de instalacion");
                                    }
                                }
                                else
                                    Log.d(TAG, "onActivityResult: Equipo de cortejo no contiene informaci??n");
                            }
                            else{
                                Log.d(TAG, "onActivityResult: No se guardaran candelabros");
                            }

                            consultarEquiposDeCortejo(bitacora);
                            Toast.makeText(this, "Equipo guardado correctamente", Toast.LENGTH_SHORT).show();
                            showAnimationFromSuccessRecord();
                        } else {
                            showErrorDialog("C??digo repetido o desconocido para articulos de Cortejo, verifica nuevamente", "");
                        }
                    }
                    else {
                        showErrorDialog("El c??digo no corresponde a un art??culo de velaci??n, verifica nuevamente.", "");
                    }*/

                } else if(equipoDeTraslado){

                    if(result.getContents().length()<=6) {


                        boolean guardarEquipo = false, guardar4Candelabros = false;
                        String nombreDeEquipo = "DESCONOCIDO", inicialesDeEquipo = "", entradaOsalida = "";

                        if (!result.getContents().contains("AA")) {
                            inicialesDeEquipo = result.getContents().substring(0, 2);
                            List<CatalogoArticulos> catalogoArticulosList = CatalogoArticulos.findWithQuery(CatalogoArticulos.class, "SELECT * FROM catalogo_Articulos WHERE letras ='" + inicialesDeEquipo + "'");
                            if (catalogoArticulosList.size() > 0) {
                                Log.d(TAG, "onActivityResult: Azael si encontre informaci??n de articulos con las letras: " + inicialesDeEquipo);
                                nombreDeEquipo = catalogoArticulosList.get(0).getNombre();
                                guardarEquipo = true;

                                if (inicialesDeEquipo.equals("CB")) {
                                    guardar4Candelabros = true;
                                }
                            } else {
                                nombreDeEquipo = "DESCONOCIDO";
                                guardarEquipo = false;
                                guardar4Candelabros = false;
                            }

                        /*boolean guardarEquipo = false, guardar4Candelabros = false;
                        String nombreDeEquipo = "DESCONOCIDO", entradaOsalida="";

                        {
                            if (result.getContents().contains("CF")) {
                                nombreDeEquipo = "Cafetera";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("CL")) {
                                nombreDeEquipo = "Candelabro";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("CB")) {
                                nombreDeEquipo = "Cristo y base";
                                guardarEquipo = true;
                                guardar4Candelabros = true;
                            } else if (result.getContents().contains("PD")) {
                                nombreDeEquipo = "Pedestal";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("BM")) {
                                nombreDeEquipo = "Biombo";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("AT")) {
                                nombreDeEquipo = "Ata??d de traslado";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("CP")) {
                                nombreDeEquipo = "Carrito pedestal";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("CI")) {
                                nombreDeEquipo = "Carrito infantil";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("CN")) {
                                nombreDeEquipo = "Candelero infantil";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("PI")) {
                                nombreDeEquipo = "Pedestal infantil";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            }else if (result.getContents().contains("KC")) {
                                nombreDeEquipo = "Kit de Cafeter??a";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            }else {
                                nombreDeEquipo = "DESCONOCIDO";
                                guardarEquipo = false;
                                guardar4Candelabros = false;
                            }

                        }*/

                            //if (!nombreDeEquipo.equals("DESCONOCIDO") && !((result.getContents().contains("CL") && nombreDeEquipo.equals("Candelabro")) || (result.getContents().contains("CB") && nombreDeEquipo.equals("Cristo y base"))))
                              //  guardarEquipo = !DatabaseAssistant.yaExisteInformacionDelArticuloDeVelacionDeTraslado("" + result.getContents(), "" + bitacora);

                            if (guardarEquipo) {
                                if (DatabaseAssistant.entradaSalidaTraslado("" + result.getContents(), "" + bitacora).equals("1")) {
                                    showErrorDialog("El c??digo ya tiene un registro de salida y entrada de inventario.", "");
                                } else {
                                    if (DatabaseAssistant.entradaSalidaTraslado("" + result.getContents(), "" + bitacora).equals("")) {
                                        entradaOsalida = "0";
                                    } else if (DatabaseAssistant.entradaSalidaTraslado("" + result.getContents(), "" + bitacora).equals("0")) {
                                        entradaOsalida = "1";
                                    }


                                    DatabaseAssistant.insertarEquipoTraslado(
                                            "" + bitacora,
                                            "" + result.getContents(),
                                            nombreDeEquipo,
                                            entradaOsalida,
                                            "" + (Preferences.getPreferenceIsbunker(BitacoraDetalle.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0"),
                                            "0"
                                    );
                                    if (guardar4Candelabros) {
                                        for (int i = 0; i <= 3; i++) {
                                            int random = (int) Math.floor(Math.random() * 247 + 1);
                                            String randomString = String.valueOf(random);
                                            String s = randomString.length() == 2 ? "CL00" : randomString.length() == 3 ? "CL0" : randomString.length() == 1 ? "CL000" : "CL000";
                                            String serie = s + random;
                                            DatabaseAssistant.insertarEquipoTraslado(
                                                    "" + bitacora,
                                                    serie,
                                                    "Candelabro",
                                                    entradaOsalida,
                                                    "" + (Preferences.getPreferenceIsbunker(BitacoraDetalle.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0"),
                                                    "0"
                                            );
                                        }
                                    } else
                                        Log.d(TAG, "onActivityResult: No se guardaran candelabros");

                                    consultarEquiposDeTraslado(bitacora);
                                    Toast.makeText(this, "Equipo guardado correctamente", Toast.LENGTH_SHORT).show();
                                    showAnimationFromSuccessRecord();
                                }
                            } else
                                showErrorDialog("C??digo repetido o desconocido para articulos de Traslado, verifica nuevamente", "");

                        } else
                            showErrorDialog("C??digo repetido o desconocido para articulos de Traslado, verifica nuevamente", "");
                    }
                    else {
                        showErrorDialog("El c??digo no corresponde a un art??culo de velaci??n, verifica nuevamente.", "");
                    }

                }
                else if(equipoRecoleccion){
                    if(result.getContents().length()<=6) {


                        boolean guardarEquipo = false, guardar4Candelabros = false;
                        String nombreDeEquipo = "DESCONOCIDO", inicialesDeEquipo="", entradaOsalida="";

                        inicialesDeEquipo = result.getContents().substring(0, 2);
                        List<CatalogoArticulos> catalogoArticulosList = CatalogoArticulos.findWithQuery(CatalogoArticulos.class, "SELECT * FROM catalogo_Articulos WHERE letras ='"+ inicialesDeEquipo + "'");
                        if(catalogoArticulosList.size() > 0 ){
                            Log.d(TAG, "onActivityResult: Azael si encontre informaci??n de articulos con las letras: " + inicialesDeEquipo);
                            nombreDeEquipo = catalogoArticulosList.get(0).getNombre();
                            guardarEquipo = true;

                            if(inicialesDeEquipo.equals("CB")){
                                guardar4Candelabros = true;
                            }
                        }
                        else{
                            nombreDeEquipo = "DESCONOCIDO";
                            guardarEquipo = false;
                            guardar4Candelabros = false;
                        }





                        /*boolean guardarEquipo = false, guardar4Candelabros = false;
                        String nombreDeEquipo = "DESCONOCIDO", entradaOsalida="";

                        {
                            if (result.getContents().contains("CF")) {
                                nombreDeEquipo = "Cafetera";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("CL")) {
                                nombreDeEquipo = "Candelabro";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("CB")) {
                                nombreDeEquipo = "Cristo y base";
                                guardarEquipo = true;
                                guardar4Candelabros = true;
                            } else if (result.getContents().contains("PD")) {
                                nombreDeEquipo = "Pedestal";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("BM")) {
                                nombreDeEquipo = "Biombo";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("AA")) {
                                nombreDeEquipo = "Ata??d de recolecci??n";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("CP")) {
                                nombreDeEquipo = "Carrito pedestal";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("CI")) {
                                nombreDeEquipo = "Carrito infantil";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("CN")) {
                                nombreDeEquipo = "Candelero infantil";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            } else if (result.getContents().contains("PI")) {
                                nombreDeEquipo = "Pedestal infantil";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            }else if (result.getContents().contains("KC")) {
                                nombreDeEquipo = "Kit de Cafeter??a";
                                guardarEquipo = true;
                                guardar4Candelabros = false;
                            }else {
                                nombreDeEquipo = "DESCONOCIDO";
                                guardarEquipo = false;
                                guardar4Candelabros = false;
                            }

                        }*/

                        /* Esta validaci??n sirve para no permitir agregar otro registro (entrada y salida) que no sea Candelabro o cristo base NO BORRAR*/
                        //NO BORRAR
                        //NO BORRAR
                        //NO BORRAR
                        //NO BORRAR
                        //if(!nombreDeEquipo.equals("DESCONOCIDO") && !(   ( result.getContents().contains("CL") && nombreDeEquipo.equals("Candelabro") ) || (   result.getContents().contains("CB") && nombreDeEquipo.equals("Cristo y base")   )      ))
                          //  guardarEquipo = !DatabaseAssistant.yaExisteInformacionDelArticuloDeVelacionDeRecoleccion("" + result.getContents(), "" + bitacora);


                        if (guardarEquipo) {
                            if (DatabaseAssistant.entradaSalidaRecoleccion("" + result.getContents(), "" + bitacora).equals("1")) {
                                Toast.makeText(this, "No se guarda ningun dato porque ya tiene un registro de entrada y salida", Toast.LENGTH_LONG).show();
                                showErrorDialog("El c??digo ya tiene un registro de salida y entrada de inventario.", "");
                            } else {
                                if (DatabaseAssistant.entradaSalidaRecoleccion("" + result.getContents(), "" + bitacora).equals("")) {
                                    entradaOsalida = "0";
                                } else if (DatabaseAssistant.entradaSalidaRecoleccion("" + result.getContents(), "" + bitacora).equals("0")) {
                                    entradaOsalida = "1";
                                }

                                DatabaseAssistant.insertarEquipoRecoleccion(
                                        "" + bitacora,
                                        "" + result.getContents(),
                                        nombreDeEquipo,
                                        entradaOsalida,
                                        "" + (Preferences.getPreferenceIsbunker(BitacoraDetalle.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0"),
                                        "0"
                                );

                                if (guardar4Candelabros) {
                                    for (int i = 0; i <= 3; i++) {
                                        int random = (int) Math.floor(Math.random() * 247 + 1);
                                        String randomString = String.valueOf(random);
                                        String s = randomString.length() == 2 ? "CL00" : randomString.length() == 3 ? "CL0" : randomString.length() == 1 ? "CL000" : "CL000";
                                        String serie = s + random;
                                        DatabaseAssistant.insertarEquipoRecoleccion(
                                                "" + bitacora,
                                                serie,
                                                "Candelabro",
                                                entradaOsalida,
                                                "" + (Preferences.getPreferenceIsbunker(BitacoraDetalle.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0"),
                                                "0"
                                        );
                                    }
                                } else
                                    Log.d(TAG, "onActivityResult: No se guardaran candelabros");

                                consultarEquiposDeRecoleccion(bitacora);
                                Toast.makeText(this, "Equipo guardado correctamente", Toast.LENGTH_SHORT).show();
                                showAnimationFromSuccessRecord();

                            }


                        } else
                            showErrorDialog("C??digo repetido o desconocido para articulos de recolecci??n, verifica nuevamente", "");

                    }
                    else{
                        showErrorDialog("El c??digo no corresponde a un art??culo de velaci??n, verifica nuevamente.", "");
                    }

                }else if(scannerAtaurna)
                {
                    if(ApplicationResourcesProvider.checkInternetConnection()) {

                        /**Hay que verificar por medio del WS si en la bitacora seleccioada, tiene el ataud o urna disponible para su uso**/
                        try {

                            if (result.getContents().contains("|"))
                            {

                                showLoadingDialog();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ApplicationResourcesProvider.createJsonForSync();
                                    }
                                });
                                TextView tvMensajeLoad =(TextView) findViewById(R.id.tvMensajeLoad);
                                tvMensajeLoad.setText("Cargando");
                                Button btCancelarPeticion =(Button) findViewById(R.id.btCancelarPeticion);
                                btCancelarPeticion.setVisibility(View.GONE);
                                JSONObject parametrosParaSaberSiElCodigoDeAtaurnaFunciona = new JSONObject();
                                parametrosParaSaberSiElCodigoDeAtaurnaFunciona.put("serie_ataurna", result.getContents());

                                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_CHECK_STATUS_ATAURNA, parametrosParaSaberSiElCodigoDeAtaurnaFunciona, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {
                                            if (response.has("error")) {
                                                Log.d(TAG, "onResponse: Ocurrio un error en la respuesta del WS de ATAURNAS CHECK: " + response.getString("error_message"));
                                                Toast.makeText(BitacoraDetalle.this, "Parece que el c??digo es incorrecto", Toast.LENGTH_SHORT).show();
                                                hideLoadingDialog();
                                            } else {
                                                if (response.has("result")) {

                                                    /*
                                                        result
                                                        1 = No disponible para escanear, esta guardada en Web con otra bitacora
                                                        0 = Disponible para escanear.
                                                     */

                                                    if (response.getString("result").equals("1")) {
                                                        showErrorDialog("Parece que el ata??d o urna no esta disponible, ya que se encuentra asignado(a) a otra bit??cora", "");
                                                        hideLoadingDialog();
                                                    } else if (response.getString("result").equals("0")) {
                                                        /**Declaramos la variable booleana para indiicar si se guarda el escaneo**/
                                                        boolean guardarCapturaDeAtaurnas = false;

                                                        /**Verificamos si ya tiene guardado el mismo codigo de ataud, en la bitacora indicada**/
                                                        if (DatabaseAssistant.hayDatosDeAtaurnasEnLaBitacora(bitacora)) {

                                                            /**Validar si tiene borrado 0 o 1 para insertar uno nuevo**/
                                                            if (DatabaseAssistant.getDatosDeAtaurnasEnLaBitacora(bitacora).equals("1")) {
                                                                guardarCapturaDeAtaurnas = true;
                                                            } else if (DatabaseAssistant.getDatosDeAtaurnasEnLaBitacora(bitacora).equals("0")) {
                                                                guardarCapturaDeAtaurnas = false;
                                                                showErrorDialog("Antes de guardar debes eliminar el Ata??d o la Urna que tienes registrada", "");
                                                            }

                                                        } else {
                                                            guardarCapturaDeAtaurnas = true;
                                                        }

                                                        /**Verificamos si fue verdadero para guardar, si es as??, procedemos a procesar y desocomponer la cadena de texto**/
                                                        if (guardarCapturaDeAtaurnas) {
                                                            try {
                                                                if (result.getContents().contains("|")) {
                                                                    String[] codeResult = result.getContents().replace("|", "X").split("X");

                                                                    DatabaseAssistant.insertarInventario(
                                                                            "" + codeResult[0],
                                                                            "" + codeResult[1],
                                                                            "" + codeResult[2],
                                                                            "" + codeResult[3],
                                                                            "" + codeResult[4],
                                                                            "" + bitacora,
                                                                            "0"
                                                                    );

                                                                    llenarCamposDeAtaurna(bitacora);
                                                                    hideLoadingDialog();
                                                                    Toast.makeText(getApplicationContext(), "Equipo guardado correctamente", Toast.LENGTH_SHORT).show();
                                                                    showAnimationFromSuccessRecord();

                                                                } else {
                                                                    showErrorDialog("El c??digo no corresponde a un Ata??d o Urna, verifica nuevamente", "");
                                                                    hideLoadingDialog();
                                                                }
                                                            } catch (Throwable e) {
                                                                Log.e(TAG, "onActivityResult: La cadena del escaneo de ataurna no tiene el formato correcto" + e.getMessage());
                                                                showErrorDialog("C??digo incorrecto, asegurate que sea un c??digo de Ata??d o de Urna. Verifica nuevamente", "");
                                                                hideLoadingDialog();
                                                            }
                                                        } else {
                                                            Log.d(TAG, "onActivityResult: No se guardaran datos de ataurnas");
                                                            hideLoadingDialog();
                                                        }
                                                    }
                                                }
                                                else{
                                                    //No tiene result
                                                    Toast.makeText(BitacoraDetalle.this, "Parece que el c??digo es incorrecto", Toast.LENGTH_SHORT).show();
                                                    hideLoadingDialog();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            Log.e("TIMER", "onErrorResponse: Error: " + e.getMessage());
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "Ocurrio un error en Ataurnas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            hideLoadingDialog();
                                        }
                                    }
                                },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                error.printStackTrace();
                                                status = -1;
                                                Log.e("TIMER", "onErrorResponse: Error: " + error.getMessage());
                                                hideLoadingDialog();
                                                Toast.makeText(getApplicationContext(), "Ocurrio un error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }) {

                                };
                                postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);

                            }else {
                                showErrorDialog("El c??digo no corresponde a un Ata??d o Urna, verifica nuevamente", "");
                            }

                        } catch (Exception e) {
                            Log.d("TIMER", "getStatusBinaccle: error en creacion de json para status de bitacora");
                            Toast.makeText(this, "Ocurrio un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }else
                        showErrorDialog("Necesitas conexi??n a internet para realizar el escaneo del Ata??d o Urna.", "");
                }
                else {
                    Log.d(TAG, "onActivityResult: No se guarda en ningun lugar");
                    Toast.makeText(this, "No se puede guardar el c??digo", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "No se puede escanear el c??digo.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void consultarEquiposDeInstalacion(String bitacora) {
        RecyclerView rvDocumentos;
        GridLayoutManager gridLayoutManager;
        rvDocumentos = (RecyclerView) findViewById(R.id.rvEquiposEscaneadosInstalacion);
        rvDocumentos.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rvDocumentos.setLayoutManager(gridLayoutManager);
        List<Equipoinstalacion> modelDocumentos = new ArrayList<>();
        AdapterEquiposInstalacion adapterDocumentos = null;

        List<Equipoinstalacion> documentosList = Equipoinstalacion.findWithQuery(Equipoinstalacion.class, "SELECT * FROM EQUIPOINSTALACION WHERE bitacora = '" + bitacora + "' ORDER BY id ASC");
        if (documentosList.size() > 0) {
            rvDocumentos.setVisibility(View.VISIBLE);
            //frameSinDatos.setVisibility(View.GONE);
            modelDocumentos.clear();
            for (int i = 0; i <= documentosList.size() - 1; i++) {
                Equipoinstalacion product = new Equipoinstalacion(
                        "" + documentosList.get(i).getBitacora(),
                        "" + documentosList.get(i).getSerie(),
                        "" + documentosList.get(i).getFecha(),
                        "" + documentosList.get(i).getSync(),
                        "" + documentosList.get(i).getNombre(),
                        "",
                        "",
                        "",
                        ""
                );
                modelDocumentos.add(product);
            }

            adapterDocumentos = new AdapterEquiposInstalacion(getApplicationContext(), modelDocumentos, BitacoraDetalle.this);
            rvDocumentos.setAdapter(adapterDocumentos);
        } else {
            modelDocumentos.clear();
            adapterDocumentos = null;
            rvDocumentos.setVisibility(View.GONE);
            //frameSinDatos.setVisibility(View.VISIBLE);
        }
    }

    private void consultarEquiposDeCortejo(String bitacora) {
        RecyclerView rvDocumentos;
        GridLayoutManager gridLayoutManager;
        rvDocumentos = (RecyclerView) findViewById(R.id.rvEquiposEscaneadosCortejo);
        rvDocumentos.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rvDocumentos.setLayoutManager(gridLayoutManager);
        List<Equipocortejo> modelDocumentos = new ArrayList<>();
        AdapterEquiposCortejo adapterDocumentos = null;

        List<Equipocortejo> documentosList = Equipocortejo.findWithQuery(Equipocortejo.class, "SELECT * FROM EQUIPOCORTEJO WHERE bitacora = '" + bitacora + "' ORDER BY id ASC");
        if (documentosList.size() > 0) {
            rvDocumentos.setVisibility(View.VISIBLE);
            //frameSinDatos.setVisibility(View.GONE);
            modelDocumentos.clear();
            for (int i = 0; i <= documentosList.size() - 1; i++) {
                Equipocortejo product = new Equipocortejo(
                        "" + documentosList.get(i).getBitacora(),
                        "" + documentosList.get(i).getSerie(),
                        "" + documentosList.get(i).getFecha(),
                        "" + documentosList.get(i).getSync(),
                        "" + documentosList.get(i).getNombre(),
                        "",
                        "",
                        "",""
                );
                modelDocumentos.add(product);
            }

            adapterDocumentos = new AdapterEquiposCortejo(getApplicationContext(), modelDocumentos, BitacoraDetalle.this);
            rvDocumentos.setAdapter(adapterDocumentos);
        } else {
            modelDocumentos.clear();
            adapterDocumentos = null;
            rvDocumentos.setVisibility(View.GONE);
            //frameSinDatos.setVisibility(View.VISIBLE);
        }


    }


    private void consultarEquiposDeTraslado(String bitacora) {
        RecyclerView rvDocumentos;
        GridLayoutManager gridLayoutManager;
        rvDocumentos = (RecyclerView) findViewById(R.id.rvEquiposEscaneadosTraslado);
        rvDocumentos.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rvDocumentos.setLayoutManager(gridLayoutManager);
        List<EquipoTraslado> modelDocumentos = new ArrayList<>();
        AdapterEquiposTraslado adapterDocumentos = null;

        List<EquipoTraslado> documentosList = EquipoTraslado.findWithQuery(EquipoTraslado.class, "SELECT * FROM EQUIPO_TRASLADO WHERE bitacora = '" + bitacora + "' ORDER BY id ASC");
        if (documentosList.size() > 0) {
            rvDocumentos.setVisibility(View.VISIBLE);
            //frameSinDatos.setVisibility(View.GONE);
            modelDocumentos.clear();
            for (int i = 0; i <= documentosList.size() - 1; i++) {
                EquipoTraslado product = new EquipoTraslado(
                        "" + documentosList.get(i).getBitacora(),
                        "" + documentosList.get(i).getSerie(),
                        "" + documentosList.get(i).getFecha(),
                        "" + documentosList.get(i).getSync(),
                        "" + documentosList.get(i).getNombre(),
                        "",
                        "",
                        "" + documentosList.get(i).getTipo(),
                        "", ""
                );
                modelDocumentos.add(product);
            }

            adapterDocumentos = new AdapterEquiposTraslado(getApplicationContext(), modelDocumentos, BitacoraDetalle.this);
            rvDocumentos.setAdapter(adapterDocumentos);
        } else {
            modelDocumentos.clear();
            adapterDocumentos = null;
            rvDocumentos.setVisibility(View.GONE);
            //frameSinDatos.setVisibility(View.VISIBLE);
        }


    }


    private void consultarEquiposDeRecoleccion(String bitacora) {
        RecyclerView rvDocumentos;
        GridLayoutManager gridLayoutManager;
        rvDocumentos = (RecyclerView) findViewById(R.id.rvEquiposEscaneadosRecoleccion);
        rvDocumentos.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rvDocumentos.setLayoutManager(gridLayoutManager);
        List<EquipoRecoleccion> modelDocumentos = new ArrayList<>();
        AdapterEquiposRecoleccion adapterDocumentos = null;

        List<EquipoRecoleccion> documentosList = EquipoRecoleccion.findWithQuery(EquipoRecoleccion.class, "SELECT * FROM EQUIPO_RECOLECCION WHERE bitacora = '" + bitacora + "' ORDER BY id ASC");
        if (documentosList.size() > 0) {
            rvDocumentos.setVisibility(View.VISIBLE);
            modelDocumentos.clear();
            for (int i = 0; i <= documentosList.size() - 1; i++) {
                EquipoRecoleccion product = new EquipoRecoleccion(
                        "" + documentosList.get(i).getBitacora(),
                        "" + documentosList.get(i).getSerie(),
                        "" + documentosList.get(i).getFecha(),
                        "" + documentosList.get(i).getSync(),
                        "" + documentosList.get(i).getNombre(),
                        "",
                        "",
                        documentosList.get(i).getTipo(),
                        "", ""
                );
                modelDocumentos.add(product);
            }
            adapterDocumentos = new AdapterEquiposRecoleccion(getApplicationContext(), modelDocumentos, BitacoraDetalle.this);
            rvDocumentos.setAdapter(adapterDocumentos);
        } else {
            modelDocumentos.clear();
            adapterDocumentos = null;
            rvDocumentos.setVisibility(View.GONE);
        }
    }

    private void llenarCamposDeAtaurna(String bitacora) {

        try {
            List<Inventario> inventarioList = Inventario.findWithQuery(Inventario.class, "SELECT * FROM INVENTARIO WHERE bitacora = '" + bitacora + "' and borrado = '0' ORDER BY id DESC LIMIT 1");
            if (inventarioList.size() > 0) {
                String codigo = inventarioList.get(0).getCodigo();

                String descripcion = "Descripci??n: " + inventarioList.get(0).getDescripcion();
                String serie = "No. Serie: " + inventarioList.get(0).getSerie();
                String fecha = "Fecha admisi??n: " + inventarioList.get(0).getFecha();
                String proveedor = "Proveedor: " + inventarioList.get(0).getProveedor();

                if (codigo.contains("UR")) {
                    LinearLayout layoutAtaud = (LinearLayout) findViewById(R.id.layoutAtaud);
                    layoutAtaud.setVisibility(View.GONE);

                    LinearLayout layoutUrna = (LinearLayout) findViewById(R.id.layoutUrna);
                    TextView tvCodigoUrna = (TextView) findViewById(R.id.tvCodigoUrna);
                    TextView tvDescripcionUrna = (TextView) findViewById(R.id.tvDescripcionUrna);
                    TextView tvSerieUrna = (TextView) findViewById(R.id.tvSerieUrna);
                    TextView tvFechaUrna = (TextView) findViewById(R.id.tvFechaUrna);
                    TextView tvProveedorUrna = (TextView) findViewById(R.id.tvProveedorUrna);

                    tvCodigoUrna.setText(inventarioList.get(0).getCodigo());
                    tvDescripcionUrna.setText(descripcion);
                    tvSerieUrna.setText(serie);
                    tvFechaUrna.setText(fecha);
                    tvProveedorUrna.setText(proveedor);

                    layoutUrna.setVisibility(View.VISIBLE);
                } else if (codigo.contains("AT")) {
                    LinearLayout layoutUrna = (LinearLayout) findViewById(R.id.layoutUrna);
                    layoutUrna.setVisibility(View.GONE);

                    LinearLayout layoutAtaud = (LinearLayout) findViewById(R.id.layoutAtaud);
                    TextView tvCodigoAtaud = (TextView) findViewById(R.id.tvCodigoAtaud);
                    TextView tvDescripcionAtaud = (TextView) findViewById(R.id.tvDescripcionAtaud);
                    TextView tvSerieAtaud = (TextView) findViewById(R.id.tvSerieAtaud);
                    TextView tvFechaAtaud = (TextView) findViewById(R.id.tvFechaAtaud);
                    TextView tvProveedorAtaud = (TextView) findViewById(R.id.tvProveedorAtaud);

                    tvCodigoAtaud.setText(inventarioList.get(0).getCodigo());
                    tvDescripcionAtaud.setText(descripcion);
                    tvSerieAtaud.setText(serie);
                    tvFechaAtaud.setText(fecha);
                    tvProveedorAtaud.setText(proveedor);

                    layoutAtaud.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "llenarCamposDeAtaurna: No hay datos de ataudes o urnas");
                    LinearLayout layoutAtaud = (LinearLayout) findViewById(R.id.layoutAtaud);
                    layoutAtaud.setVisibility(View.GONE);
                    LinearLayout layoutUrna = (LinearLayout) findViewById(R.id.layoutUrna);
                    layoutUrna.setVisibility(View.GONE);
                    //String vacio ="";
                    //Inventario.executeQuery("DELETE INVENTARIO WHERE codigo ='" + vacio + "'");
                }

            } else {
                Log.d(TAG, "llenarCamposDeAtaurna: No hay datos de ataudes o urnas");
                LinearLayout layoutAtaud = (LinearLayout) findViewById(R.id.layoutAtaud);
                layoutAtaud.setVisibility(View.GONE);
                LinearLayout layoutUrna = (LinearLayout) findViewById(R.id.layoutUrna);
                layoutUrna.setVisibility(View.GONE);
            }

        }catch (Throwable e){
            Log.e(TAG, "llenarCamposDeAtaurna: Erorr en cacheo de datos de ataruna" + e.getMessage());
            Log.d(TAG, "llenarCamposDeAtaurna: No hay datos de ataudes o urnas");
            LinearLayout layoutAtaud = (LinearLayout) findViewById(R.id.layoutAtaud);
            layoutAtaud.setVisibility(View.GONE);
            LinearLayout layoutUrna = (LinearLayout) findViewById(R.id.layoutUrna);
            layoutUrna.setVisibility(View.GONE);
            //String vacio ="";
            //Inventario.executeQuery("DELETE INVENTARIO WHERE codigo ='" + vacio + "'");
        }

    }

    private void consultarArticulosEscaneados(String bitacora) {
        RecyclerView rvDocumentos;
        GridLayoutManager gridLayoutManager;
        rvDocumentos = (RecyclerView) findViewById(R.id.rvArticulosEscaneados);
        rvDocumentos.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rvDocumentos.setLayoutManager(gridLayoutManager);
        List<ModelArticulosEscaneados> modelDocumentos = new ArrayList<>();
        AdapterArticulosEscaneados adapterDocumentos = null;

        List<Articuloscan> documentosList = Articuloscan.findWithQuery(Articuloscan.class, "SELECT * FROM ARTICULOSCAN WHERE bitacora ='" + bitacora + "' ORDER BY id DESC");
        if (documentosList.size() > 0) {
            rvDocumentos.setVisibility(View.VISIBLE);
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

            adapterDocumentos = new AdapterArticulosEscaneados(getApplicationContext(), modelDocumentos, BitacoraDetalle.this);
            rvDocumentos.setAdapter(adapterDocumentos);
        } else {
            modelDocumentos.clear();
            adapterDocumentos = null;
            rvDocumentos.setVisibility(View.GONE);
        }
    }



    @Override
    public void onClickCancelarArticulo(int position, String idArticulo) {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setCancelable(false);
        dialogo1.setTitle("Eliminaci??n...");
        dialogo1.setMessage("?? Seguro que des??as eliminar el art??culo ?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

                List<Articuloscan> listaArticulos = Articuloscan.findWithQuery(Articuloscan.class, "SELECT * FROM ARTICULOSCAN WHERE id = '" + idArticulo + "'");
                if (listaArticulos.size() > 0) {
                    DatabaseAssistant.insertarArticuloCancelado(
                            "" + listaArticulos.get(0).getNombre(),
                            "" + listaArticulos.get(0).getSerie(),
                            "" + listaArticulos.get(0).getFecha(),
                            "" + listaArticulos.get(0).getBitacora(),"", "",""
                    );
                    Articuloscan.executeQuery("DELETE FROM ARTICULOSCAN WHERE id ='" + idArticulo + "'");
                    consultarArticulosEscaneados(bitacora);

                }else{
                    Log.d(TAG, "onClick: No se puede cancelar el articulo");
                }


            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.dismiss();
            }
        });
        dialogo1.show();
    }

    @Override
    public void onClickCancelarArticuloInstalacion(int position, String serie, String fecha, String bitacora) {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setCancelable(false);
        dialogo1.setTitle("Eliminaci??n...");
        dialogo1.setMessage("?? Seguro que des??as eliminar el art??culo ?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

                List<Equipoinstalacion> listaArticulos = Equipoinstalacion.findWithQuery(Equipoinstalacion.class, "SELECT * FROM EQUIPOINSTALACION WHERE serie = '" + serie + "' and fecha ='" + fecha + "' and bitacora='" + bitacora + "'");
                if (listaArticulos.size() > 0) {
                    DatabaseAssistant.insertarArticuloCancelado(
                            "" + listaArticulos.get(0).getSerie(),
                            "" + listaArticulos.get(0).getNombre(),
                            "" + listaArticulos.get(0).getSerie(),
                            "" + listaArticulos.get(0).getFecha(),
                            "",
                            "" + listaArticulos.get(0).getBitacora(), ""
                    );

                    Equipoinstalacion.executeQuery("DELETE FROM EQUIPOINSTALACION WHERE serie = '" + serie + "' and fecha ='" + fecha + "' and bitacora='" + bitacora + "'");
                    consultarEquiposDeInstalacion(bitacora);

                }else{
                    Log.d(TAG, "onClick: No se puede cancelar el articulo");
                }


            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.dismiss();
            }
        });
        dialogo1.show();
    }

    public void showComentariosDialog(View view, String bitacora) {
        try {
            ImageView btEnviarComentario, btClose;
            RecyclerView rvComentarios;
            LinearLayout frameNoTenemosDescargas;
            EditText etComentario;
            GridLayoutManager gridLayoutManagerFromDownloads;
            TextView tvNombreUsuario, tvBitacora;
            String usuarioParaComentarios="";

            mBottomSheetDialogComentarios = new Dialog(BitacoraDetalle.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
            mBottomSheetDialogComentarios.setContentView(R.layout.bottom_dialog_comentarios);

            rvComentarios = (RecyclerView) mBottomSheetDialogComentarios.findViewById(R.id.rvComentarios);
            btEnviarComentario = (ImageView) mBottomSheetDialogComentarios.findViewById(R.id.btEnviarComentario);
            frameNoTenemosDescargas = (LinearLayout) mBottomSheetDialogComentarios.findViewById(R.id.frameNoTenemosDescargas);
            etComentario = (EditText) mBottomSheetDialogComentarios.findViewById(R.id.etComentario);
            tvNombreUsuario = (TextView) mBottomSheetDialogComentarios.findViewById(R.id.tvNombreUsuario);
            btClose = (ImageView) mBottomSheetDialogComentarios.findViewById(R.id.btClose);
            tvBitacora = (TextView) mBottomSheetDialogComentarios.findViewById(R.id.tvBitacora);
            tvBitacora.setText(bitacora);

            btClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomSheetDialogComentarios.dismiss();
                }
            });

            try {
                if (Preferences.getIsProveedor(BitacoraDetalle.this, Preferences.PREFERENCE_IS_PROVEEDOR)) {
                    List<Eventos> eventosList = Eventos.findWithQuery(Eventos.class, "SELECT * FROM EVENTOS where bitacora ='" + bitacora +"' ORDER BY id DESC LIMIT 1");
                    if(eventosList.size()>0) {
                        tvNombreUsuario.setText(DatabaseAssistant.getUserNameFromSesiones() + " | " + eventosList.get(0).getChofer());
                        usuarioParaComentarios = DatabaseAssistant.getUserNameFromSesiones() + " | " + eventosList.get(0).getChofer();
                    }
                }else {
                    tvNombreUsuario.setText(DatabaseAssistant.getUserNameFromSesiones());
                    usuarioParaComentarios = DatabaseAssistant.getUserNameFromSesiones();
                }
            }catch (Throwable e){
                Log.e(TAG, "onCreate: " + e.getMessage());
            }
            rvComentarios.setHasFixedSize(true);
            gridLayoutManagerFromDownloads = new GridLayoutManager(this, 1);
            rvComentarios.setLayoutManager(gridLayoutManagerFromDownloads);

            //****** ADAPTADOR Y LISTA
            List<ModelComentarios> modelComentarios = new ArrayList<>();
            AdapterComentarios adapterComentarios = null;
            //******

            String query = "SELECT * FROM COMENTARIOS WHERE bitacora = '" + bitacora + "' ORDER BY fecha ASC";
            List<Comentarios> comentariosList = Comentarios.findWithQuery(Comentarios.class, query);
            if (comentariosList.size() > 0) {
                modelComentarios.clear();
                for (int i = 0; i <= comentariosList.size() - 1; i++) {
                    ModelComentarios product = new ModelComentarios(
                            "" + comentariosList.get(i).getBitacora(),
                            "" + comentariosList.get(i).getComentario(),
                            "" + comentariosList.get(i).getUsuario(),
                            "" + comentariosList.get(i).getFecha(),
                            "" + comentariosList.get(i).getPormi()
                    );
                    modelComentarios.add(product);
                }
                Collections.reverse(modelComentarios);
                adapterComentarios = new AdapterComentarios(getApplicationContext(), modelComentarios);
                rvComentarios.setAdapter(adapterComentarios);
                rvComentarios.setVisibility(View.VISIBLE);
                frameNoTenemosDescargas.setVisibility(View.GONE);
            } else {
                rvComentarios.setVisibility(View.GONE);
                frameNoTenemosDescargas.setVisibility(View.VISIBLE);
            }

            String finalUsuarioParaComentarios = usuarioParaComentarios;
            btEnviarComentario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!etComentario.getText().toString().isEmpty()){
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        DatabaseAssistant.insertarComentarios(
                                "" + bitacora,
                                ""+ etComentario.getText().toString(),
                                "" + finalUsuarioParaComentarios,
                                "" + dateFormat.format(new Date()),
                                "0",
                                "1"
                        );
                        etComentario.setText("");
                        /*DatabaseAssistant.insertarComentarios(
                                "" + bitacora,
                                "Comentario de la otra persona",
                                "Bot Android",
                                "" + dateFormat.format(new Date()),
                                "0",
                                "0"
                        );*/
                        //****** ADAPTADOR Y LISTA
                        List<ModelComentarios> modelComentarios = new ArrayList<>();
                        AdapterComentarios adapterComentarios = null;
                        //******

                        String query = "SELECT * FROM COMENTARIOS WHERE bitacora = '" + bitacora + "' ORDER BY fecha ASC";
                        List<Comentarios> comentariosList = Comentarios.findWithQuery(Comentarios.class, query);
                        if (comentariosList.size() > 0) {
                            modelComentarios.clear();
                            for (int i = 0; i <= comentariosList.size() - 1; i++) {
                                ModelComentarios product = new ModelComentarios(
                                        "" + comentariosList.get(i).getBitacora(),
                                        "" + comentariosList.get(i).getComentario(),
                                        "" + comentariosList.get(i).getUsuario(),
                                        "" + comentariosList.get(i).getFecha(),
                                        "" + comentariosList.get(i).getPormi()
                                );
                                modelComentarios.add(product);
                            }
                            Collections.reverse(modelComentarios);
                            adapterComentarios = new AdapterComentarios(getApplicationContext(), modelComentarios);
                            rvComentarios.setAdapter(adapterComentarios);
                            rvComentarios.setVisibility(View.VISIBLE);
                            frameNoTenemosDescargas.setVisibility(View.GONE);
                        } else {
                            rvComentarios.setVisibility(View.GONE);
                            frameNoTenemosDescargas.setVisibility(View.VISIBLE);
                        }
                    }else
                        Toast.makeText(getApplicationContext(), "Escribe un comentario", Toast.LENGTH_SHORT).show();
                }
            });

            mBottomSheetDialogComentarios.show();
        } catch (Throwable e) {
            Log.e(TAG, "Error en showDownloadsPDF(): " + e.toString());
        }

    }

    private void consultarComentarios(String bitacora) {
        RecyclerView rvDocumentos;
        GridLayoutManager gridLayoutManager;
        rvDocumentos = (RecyclerView) mBottomSheetDialogComentarios.findViewById(R.id.rvEquiposEscaneadosCortejo);
        rvDocumentos.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rvDocumentos.setLayoutManager(gridLayoutManager);

        List<ModelComentarios> modelDocumentos = new ArrayList<>();
        AdapterComentarios adapterDocumentos = null;

        List<Comentarios> documentosList = Comentarios.findWithQuery(Comentarios.class, "SELECT * FROM COMENTARIOS WHERE bitacora = '" + bitacora + "' ORDER BY id DESC");
        if (documentosList.size() > 0) {
            rvDocumentos.setVisibility(View.VISIBLE);

            modelDocumentos.clear();
            for (int i = 0; i <= documentosList.size() - 1; i++) {
                ModelComentarios product = new ModelComentarios(
                        "" + documentosList.get(i).getBitacora(),
                        "" + documentosList.get(i).getComentario(),
                        "" + documentosList.get(i).getUsuario(),
                        "" + documentosList.get(i).getFecha(),
                        "" + documentosList.get(i).getPormi()
                );
                modelDocumentos.add(product);
            }

            Collections.reverse(modelDocumentos);
            adapterDocumentos = new AdapterComentarios(getApplicationContext(), modelDocumentos);
            rvDocumentos.setAdapter(adapterDocumentos);
        } else {
            modelDocumentos.clear();
            adapterDocumentos = null;
            rvDocumentos.setVisibility(View.GONE);
        }


    }


    private void showAnimationFromSuccessRecord() {
        imgSuccess.setAnimation("success_toast.json");
        imgSuccess.loop(false);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                BitacoraDetalle.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imgSuccess.setVisibility(View.VISIBLE);
                                        imgSuccess.playAnimation();
                                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_rigth);
                                        imgSuccess.startAnimation(animation);
                                    }
                                });
                            }
                        });

                    }
                    synchronized (this) {
                        wait(2000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imgSuccess.setVisibility(View.GONE);
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            ;
        };
        thread.start();
    }


    @Override
    public void onClickCancelarArticuloCortejo(int position, String serie, String fecha, String bitacora) {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setCancelable(false);
        dialogo1.setTitle("Eliminaci??n...");
        dialogo1.setMessage("?? Seguro que des??as eliminar el art??culo ?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

                List<Equipocortejo> listaArticulos = Equipocortejo.findWithQuery(Equipocortejo.class, "SELECT * FROM EQUIPOCORTEJO WHERE serie = '" + serie + "' and fecha ='" + fecha + "' and bitacora='" + bitacora + "'");
                if (listaArticulos.size() > 0) {
                    DatabaseAssistant.insertarArticuloCancelado(
                            "" + listaArticulos.get(0).getSerie(),
                            "" + listaArticulos.get(0).getNombre(),
                            "" + listaArticulos.get(0).getSerie(),
                            "" + listaArticulos.get(0).getFecha(),
                            "",
                            "" + listaArticulos.get(0).getBitacora(), ""
                    );

                    Equipocortejo.executeQuery("DELETE FROM EQUIPOCORTEJO WHERE serie = '" + serie + "' and fecha ='" + fecha + "' and bitacora='" + bitacora + "'");
                    consultarEquiposDeCortejo(bitacora);

                }else{
                    Log.d(TAG, "onClick: No se puede cancelar el articulo");
                }


            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.dismiss();
            }
        });
        dialogo1.show();
    }

    @Override
    public void onClickCancelarArticuloRecoleccion(int position, String serie, String fecha, String bitacora) {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setCancelable(false);
        dialogo1.setTitle("Eliminaci??n...");
        dialogo1.setMessage("?? Seguro que des??as eliminar el art??culo ?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

                List<EquipoRecoleccion> listaArticulos = EquipoRecoleccion.findWithQuery(EquipoRecoleccion.class, "SELECT * FROM EQUIPO_RECOLECCION WHERE serie = '" + serie + "' and fecha ='" + fecha + "' and bitacora='" + bitacora + "'");
                if (listaArticulos.size() > 0) {
                    DatabaseAssistant.insertarArticuloCancelado(
                            "" + listaArticulos.get(0).getSerie(),
                            "" + listaArticulos.get(0).getNombre(),
                            "" + listaArticulos.get(0).getSerie(),
                            "" + listaArticulos.get(0).getFecha(),
                            "",
                            "" + listaArticulos.get(0).getBitacora(), ""
                    );

                    EquipoRecoleccion.executeQuery("DELETE FROM EQUIPO_RECOLECCION WHERE serie = '" + serie + "' and fecha ='" + fecha + "' and bitacora='" + bitacora + "'");
                    consultarEquiposDeRecoleccion(bitacora);

                }else{
                    Log.d(TAG, "onClick: No se puede cancelar el articulo");
                }


            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.dismiss();
            }
        });
        dialogo1.show();
    }

    @Override
    public void onClickCancelarArticuloTraslado(int position, String serie, String fecha, String bitacora) {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setCancelable(false);
        dialogo1.setTitle("Eliminaci??n...");
        dialogo1.setMessage("?? Seguro que des??as eliminar el art??culo ?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

                List<EquipoTraslado> listaArticulos = EquipoTraslado.findWithQuery(EquipoTraslado.class, "SELECT * FROM EQUIPO_TRASLADO WHERE serie = '" + serie + "' and fecha ='" + fecha + "' and bitacora='" + bitacora + "'");
                if (listaArticulos.size() > 0) {
                    DatabaseAssistant.insertarArticuloCancelado(
                            "" + listaArticulos.get(0).getSerie(),
                            "" + listaArticulos.get(0).getNombre(),
                            "" + listaArticulos.get(0).getSerie(),
                            "" + listaArticulos.get(0).getFecha(),
                            "",
                            "" + listaArticulos.get(0).getBitacora(), ""
                    );

                    EquipoTraslado.executeQuery("DELETE FROM EQUIPO_TRASLADO WHERE serie = '" + serie + "' and fecha ='" + fecha + "' and bitacora='" + bitacora + "'");
                    consultarEquiposDeTraslado(bitacora);

                }else{
                    Log.d(TAG, "onClick: No se puede cancelar el articulo");
                }


            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.dismiss();
            }
        });
        dialogo1.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!Preferences.getPreferenceTutorialDetails(BitacoraDetalle.this, Preferences.PREFERENCE_TUTORIAL_DETALLES)) {
            startsTutorial();
        }

        //startsTutorial();
    }

    private void startsTutorial() {
        final Drawable droid = ContextCompat.getDrawable(   this, R.drawable.ic_comentarios);
        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(findViewById(R.id.btComentarios), "Centro de comentarios!", "Puedes a??adir comentarios de la bit??cora.")
                        .outerCircleColor(R.color.white_card_color)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.textos_general)   // Specify a color for the target circle
                        .titleTextSize(25)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.black)      // Specify the color of the title text
                        .descriptionTextSize(16)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.textos_general)  // Specify the color of the description text
                        .textColor(R.color.black)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        .icon(droid)                     // Specify a custom drawable to draw as the target
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        escaneoDeAtaudUrnaTutorial();
                    }
                });
    }

    private void escaneoDeAtaudUrnaTutorial() {
        final Drawable droid = ContextCompat.getDrawable(   this, R.drawable.ic_scan);
        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(findViewById(R.id.btScanner), "Escaneo de ATAUDES y URNAS!", "Aqu?? debes escanear UNICAMENTE el ATA??D o la URNA de la bit??cora.")
                        .outerCircleColor(R.color.purple_200)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .titleTextSize(28)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(20)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.textos_general)  // Specify the color of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        .icon(droid)                     // Specify a custom drawable to draw as the target
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional

                        final Display display = getWindowManager().getDefaultDisplay();
                        NestedScrollView nestedLayout = (NestedScrollView) findViewById(R.id.nestedLayout);
                        try {
                            nestedLayout.smoothScrollTo(0, display.getHeight() + 200);
                        }catch (Throwable e){
                            Log.e(TAG, "onTargetClick: " + e.getMessage() );
                            nestedLayout.smoothScrollTo(0, display.getHeight());
                        }
                        procedimientoTutorial();
                    }
                });
    }

    private void procedimientoTutorial() {
        //final Drawable droid = ContextCompat.getDrawable(this, R.drawable.ic_sync_tutorial);
        TapTargetView.showFor(this,
                TapTarget.forView(findViewById(R.id.layoutProcedimiento), "Embalsamado o Arreglo est??tico?", "Aqui puedes agregar el tipo de procedimiento.")
                        .outerCircleColor(R.color.purple_500)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle

                        .titleTextSize(25)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(16)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.white_card_color)  // Specify the color of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)

                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        laboratorioTutorial();
                    }

                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        laboratorioTutorial();
                    }
                });
    }


    private void laboratorioTutorial() {
        //final Drawable droid = ContextCompat.getDrawable(this, R.drawable.ic_sync_tutorial);
        TapTargetView.showFor(this,
                TapTarget.forView(findViewById(R.id.laboratorioLayout), "Laboratorio", "Aqui puedes seleccionar el laboratorio.")
                        .outerCircleColor(R.color.purple_500)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle

                        .titleTextSize(25)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(16)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.white_card_color)  // Specify the color of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)

                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        /*try {
                            NestedScrollView nestedLayout = (NestedScrollView) findViewById(R.id.nestedLayout);
                            final Display display = getWindowManager().getDefaultDisplay();
                            //display.getWidth() / 2, display.getHeight() / 2
                            nestedLayout.smoothScrollTo(display.getWidth(), display.getHeight() +2000);
                        }catch (Throwable e){
                            Log.e(TAG, "onTargetClick: " + e.getMessage() );
                        }
                        artuculosDeVelacionTutorial();*/

                        //Toast.makeText(BitacoraDetalle.this, "Tutorial finalizado", Toast.LENGTH_SHORT).show();
                        Preferences.setPreferenceTutorialDetails(BitacoraDetalle.this, true, Preferences.PREFERENCE_TUTORIAL_DETALLES);
                    }
                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        Preferences.setPreferenceTutorialDetails(BitacoraDetalle.this, true, Preferences.PREFERENCE_TUTORIAL_DETALLES);
                    }
                });
    }


    private void artuculosDeVelacionTutorial() {
        //final Drawable droid = ContextCompat.getDrawable(this, R.drawable.ic_sync_tutorial);
        TapTargetView.showFor(this,
                TapTarget.forView(findViewById(R.id.btAnadirEquipo), "ARTICULOS DE VELACI??N", "Tanto en Instalaci??n, Cortejo, Recolecci??n y Traslado tienes que escanear UNICAMENTE los articulos de velaci??n.")
                        .outerCircleColor(R.color.red_color)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .titleTextSize(25)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(16)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.white_card_color)  // Specify the color of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)

                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                    }
                });
    }


    private void getStatusBinaccle() {
        JSONObject json = new JSONObject();
        try {
            json.put("bitacora", bitacora);
            json.put("descripcion", descripcionPeticion);
            json.put("usuario_peticion", DatabaseAssistant.getUserNameFromSesiones());
            json.put("fecha_peticion", fechaEstatica);
            requestStatusBinaccle(json);
        } catch (Exception e) {
            Log.d("TIMER", "getStatusBinaccle: error en creacion de json para status de bitacora");
            hideLoadingDialog();
            Toast.makeText(this, "Ocurrio un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void requestStatusBinaccle(JSONObject jsonParams)
    {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_CHECK_STATUS_BINNACLE, jsonParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("TIMER", "onResponse: " + ConstantsBitacoras.WS_CHECK_STATUS_BINNACLE);
                manage_GetStatusBinnacle(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        status = -1;
                        Log.e("TIMER", "onErrorResponse: Error: " + error.getMessage());
                        hideLoadingDialog();
                        Toast.makeText(BitacoraDetalle.this, "Ocurrio un error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
    }


    public void manage_GetStatusBinnacle(JSONObject json)
    {
        if (status != -1 && !requesterCanceled)
        {
            try
            {
                status = 0;
                status = json.getInt("status");

                if (status == 0) {
                    //continue;
                    Log.d("TIMER", "manage_GetStatusBinnacle: Continua con el ciclo de bitacora.");
                }
                
                //accepted
                else if (status == 1) {
                    Toast.makeText(ApplicationResourcesProvider.getContext(), "Bit??cora aceptada.", Toast.LENGTH_LONG).show();
                    registrarBitacoraTerminada(json.getString("bitacora"));
                    hideLoadingDialog();
                }

                //rechazado
                else if (status == 2) {
                    Toast.makeText(ApplicationResourcesProvider.getContext(), "Bit??cora rechazada.", Toast.LENGTH_LONG).show();
                    hideLoadingDialog();
                }
                else {
                    status = -1;
                    hideLoadingDialog();
                    Toast.makeText(this, "La bit??cora no fue aceptada.", Toast.LENGTH_SHORT).show();
                    requesterCanceled = true;
                }

            } catch (JSONException e) {
                status = 0;
                e.printStackTrace();
                Log.e("TIMER", "manage_GetStatusBinnacle: Error en obtener el dato de estatus: " + e.getMessage());
                hideLoadingDialog();
                Toast.makeText(this, "Ocurrio un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else
            Log.d("TIMER", "manage_GetStatusBinnacle: el estatus es -1");

    }

}