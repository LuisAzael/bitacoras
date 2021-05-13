package com.example.bitacoras2020.Adapters;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bitacoras2020.Activities.BitacoraDetalle;
import com.example.bitacoras2020.Activities.SplashScreen;
import com.example.bitacoras2020.Callbacks.RegistrarSalidaCallback;
import com.example.bitacoras2020.Callbacks.RequerirEventoDeSalidaCallback;
import com.example.bitacoras2020.Callbacks.ShowDetailsBitacoraCallback;
import com.example.bitacoras2020.Callbacks.TerminarBitacoraCallback;
import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.example.bitacoras2020.Models.ModelBitacorasActivas;
import com.example.bitacoras2020.R;
import com.example.bitacoras2020.Utils.ApplicationResourcesProvider;
import com.example.bitacoras2020.Utils.Preferences;

import org.json.JSONArray;

import java.util.List;

import soup.neumorphism.NeumorphButton;

public class AdapterBitacorasActivas extends RecyclerView.Adapter<AdapterBitacorasActivas.ProductViewHolder> {
    private static final String TAG = "AdapterBitacorasActivas";
    private Context mCtx;
    private List<ModelBitacorasActivas> productList;
    private TerminarBitacoraCallback terminarBitacoraCallback;
    private ShowDetailsBitacoraCallback showDetailsBitacoraCallback;
    private RequerirEventoDeSalidaCallback requerirEventoDeSalidaCallback;
    private RegistrarSalidaCallback registrarSalidaCallback;

    public AdapterBitacorasActivas(Context mCtx, List<ModelBitacorasActivas> productList,
                                   TerminarBitacoraCallback terminarBitacoraCallback,
                                   ShowDetailsBitacoraCallback showDetailsBitacoraCallback,
                                   RequerirEventoDeSalidaCallback requerirEventoDeSalidaCallback,
                                   RegistrarSalidaCallback registrarSalidaCallback) {
        this.mCtx = mCtx;
        this.productList = productList;
        this.terminarBitacoraCallback = terminarBitacoraCallback;
        this.showDetailsBitacoraCallback = showDetailsBitacoraCallback;
        this.requerirEventoDeSalidaCallback = requerirEventoDeSalidaCallback;
        this.registrarSalidaCallback = registrarSalidaCallback;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.multiple_item_bitacoras_layout, null);
        ProductViewHolder holder = new ProductViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position)
    {
        final ModelBitacorasActivas product= productList.get(position);
        if(position % 2 == 0) {

            if(productList.size() == 1)
                holder.viewBajo.setVisibility(View.GONE);
            else
                holder.viewBajo.setVisibility(View.VISIBLE);

            holder.layoutGeneral.setBackgroundColor(Color.parseColor("#efefef"));
        }
        else {
            holder.layoutGeneral.setBackgroundColor(Color.parseColor("#efefef"));//#ffffff
            holder.viewBajo.setVisibility(View.GONE);
        }


        holder.tvNumeroBitacora.setTypeface(ApplicationResourcesProvider.bold);
        holder.tvSecondName.setTypeface(ApplicationResourcesProvider.light);
        holder.tvRutasGoogle.setTypeface(ApplicationResourcesProvider.regular);
        holder.tvOrigen.setTypeface(ApplicationResourcesProvider.bold);
        holder.tvDestino.setTypeface(ApplicationResourcesProvider.bold);
        holder.tvFechaUltimoEventoRegistrado.setTypeface(ApplicationResourcesProvider.regular);
        holder.btRegistrarSalida.setTypeface(ApplicationResourcesProvider.regular);
        holder.btRegistrarllegada.setTypeface(ApplicationResourcesProvider.regular);
        holder.tvTipoBitacora.setTypeface(ApplicationResourcesProvider.bold);

        holder.tvNumeroBitacora.setText(product.getBitacora());
        holder.tvSecondName.setText(DatabaseAssistant.getNameBitacora(product.getBitacora()));
        holder.tvFechaUltimoEventoRegistrado.setText(product.getFecha());



        holder.tvTipoBitacora.setText(DatabaseAssistant.getTipoDeBitacora(product.getBitacora()));
        TextPaint paint = holder.tvTipoBitacora.getPaint();
        float width = paint.measureText(DatabaseAssistant.getTipoDeBitacora(product.getBitacora()));
        Shader textShader = new LinearGradient(0, 0, width, holder.tvTipoBitacora.getTextSize(),
                new int[]{
                        Color.parseColor("#a63fff"),
                        Color.parseColor("#d40aff"),
                }, null, Shader.TileMode.CLAMP);
        holder.tvTipoBitacora.getPaint().setShader(textShader);




        try {
            if(!product.getDestino().equals("")) {
                holder.tvOrigen.setText("Salida:   " +product.getOrigen());
                holder.tvDestino.setText("Destino:   " + product.getDestino());
                holder.vistaSeparador.setVisibility(View.VISIBLE);
                holder.indicadorDestino.setVisibility(View.VISIBLE);
                holder.tvDestino.setVisibility(View.VISIBLE);
                holder.viewLateral.setBackgroundColor(Color.parseColor("#a63fff"));
            }else{
                holder.tvOrigen.setText("Llegada:    " + product.getOrigen());
                holder.vistaSeparador.setVisibility(View.GONE);
                holder.indicadorDestino.setVisibility(View.GONE);
                holder.tvDestino.setVisibility(View.GONE);
                holder.viewLateral.setBackgroundColor(Color.parseColor("#9a0007"));
                holder.layoutGeneral.setBackgroundColor(Color.parseColor("#ffdee2"));
            }

            if(product.getDestino().equals("") && product.getTipo().equals("Salida") && Preferences.getSalidaAutomatica(mCtx, Preferences.PREFERENCE_AUTOMATICA)) {
                holder.layoutGeneral.setBackgroundColor(Color.parseColor("#ffdee2"));
                requerirEventoDeSalidaCallback.onRequeriedEventoSalida(position, product.getBitacora());
                holder.btRegistrarSalida.setVisibility(View.VISIBLE);
                holder.btRegistrarllegada.setVisibility(View.VISIBLE);
            }


            //******
            String[] evento = DatabaseAssistant.getUltimoEvento(product.getBitacora());
            String destino = evento[1];

            if(!destino.equals("") || !destino.isEmpty()) {
                holder.btRegistrarSalida.setAlpha(0.3f);
                holder.btRegistrarllegada.setAlpha(1.0f);
            }
            else{

                holder.btRegistrarSalida.setAlpha(1.0f);
                holder.btRegistrarllegada.setAlpha(0.3f);
            }

            //**********

            holder.tvOrigen.setVisibility(View.VISIBLE);

        } catch (Throwable e) {
            Log.e(TAG, e.getMessage());
        }


        holder.btRegistrarSalida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] evento = DatabaseAssistant.getUltimoEvento(product.getBitacora());
                String destino = evento[1];

                if(!destino.equals("") || !destino.isEmpty()) {
                    Toast.makeText(mCtx, "Debes registrar una llegada.", Toast.LENGTH_LONG).show();
                }
                else{
                    registrarSalidaCallback.onClickRegistrarSalida(position, product.getBitacora(), "");
                }




            }
        });

        holder.btRegistrarllegada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String[] evento = DatabaseAssistant.getUltimoEvento(product.getBitacora());
                String destino = evento[1];

                if(!destino.equals("") || !destino.isEmpty()) {
                    registrarSalidaCallback.onClickRegistrarSalida(position, product.getBitacora(),  destino);
                }
                else{
                    Toast.makeText(mCtx, "Debes registrar una salida.", Toast.LENGTH_LONG).show();
                }

                Preferences.setSalidaAutomatica(mCtx, false, Preferences.PREFERENCE_AUTOMATICA);

            }
        });




        if(product.getDomicilioRuta().equals("") || product.getDomicilioRuta().length()<3) {
            holder.tvRutasGoogle.setText("Ruta no disponible por el momento.");
            holder.tvRutasGoogle.setEnabled(false);
        }
        else {
            holder.tvRutasGoogle.setText(product.getDomicilioRuta());
            holder.tvRutasGoogle.setEnabled(true);
        }


        holder.imgTerminarBitacora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminarBitacoraCallback.onClickTerminarBitacora(position, product.getBitacora());

                /*holder.tvNumeroBitacora.setTransitionName("power_bitacora");
                Intent intent = new Intent(mCtx, BitacoraDetalle.class);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mCtx,  holder.imgTerminarBitacora, holder.imgTerminarBitacora.getTransitionName());
                mCtx.startActivity(intent, optionsCompat.toBundle());*/
            }
        });

        holder.imgShowDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailsBitacoraCallback.onClickShowDetails(position, product.getBitacora());

                //holder.tvNumeroBitacora.setTransitionName("bitacora_animation");
                //Intent intent = new Intent(mCtx, BitacoraDetalle.class);
                //ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mCtx,  holder.tvNumeroBitacora, holder.tvNumeroBitacora.getTransitionName());
                //mCtx.startActivity(intent, optionsCompat.toBundle());
                //final ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mCtx, Pair.create(v, v.getTransitionName()));
                //mCtx.startActivity(new Intent(mCtx, BitacoraDetalle.class).putExtra("shared_element_transition_name", v.getTransitionName()), options.toBundle());


            }
        });

        holder.layoutGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar aqui los datos completos de la bitacora en Fragment
                // hacerlo con un Callback para mandar el contexto de la actividad hacia el fragment
                showDetailsBitacoraCallback.onClickShowDetails(position, product.getBitacora());
            }
        });

    }





    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvNumeroBitacora, tvSecondName, tvOrigen, tvDestino, tvRutasGoogle, tvFechaUltimoEventoRegistrado, tvTipoBitacora;
        View vistaSeparador, indicadorDestino, viewLateral;
        ImageView imgTerminarBitacora, imgShowDetails;
        LinearLayout layoutGeneral;
        NeumorphButton btRegistrarSalida, btRegistrarllegada;
        View viewBajo;

        public ProductViewHolder(View itemView)
        {
            super(itemView);
            tvNumeroBitacora= itemView.findViewById(R.id.tvNumeroBitacora);
            tvSecondName = itemView.findViewById(R.id.tvSecondName);
            tvOrigen = itemView.findViewById(R.id.tvOrigen);
            tvDestino = itemView.findViewById(R.id.tvDestino);
            tvRutasGoogle = itemView.findViewById(R.id.tvRutasGoogle);

            vistaSeparador = itemView.findViewById(R.id.view2);
            indicadorDestino = itemView.findViewById(R.id.viwwew);
            viewLateral = itemView.findViewById(R.id.viewLateral);

            imgTerminarBitacora = itemView.findViewById(R.id.imgTerminarBitacora);
            imgShowDetails = itemView.findViewById(R.id.imgShowDetails);

            layoutGeneral=itemView.findViewById(R.id.layoutGeneral);
            btRegistrarSalida=itemView.findViewById(R.id.btRegistrarSalida);
            btRegistrarllegada=itemView.findViewById(R.id.btRegistrarllegada);
            tvFechaUltimoEventoRegistrado=itemView.findViewById(R.id.tvFechaUltimoEventoRegistrado);
            tvTipoBitacora=itemView.findViewById(R.id.tvTipoBitacora);
            viewBajo=itemView.findViewById(R.id.viewBajo);
        }
    }


}
