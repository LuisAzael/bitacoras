package com.example.bitacoras2020.Adapters;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bitacoras2020.Callbacks.CancelarArticuloEscaneado;
import com.example.bitacoras2020.Callbacks.RegistrarSalidaCallback;
import com.example.bitacoras2020.Database.Articuloscan;
import com.example.bitacoras2020.Database.Equipoinstalacion;
import com.example.bitacoras2020.Models.ModelArticulosEscaneados;
import com.example.bitacoras2020.R;

import java.util.List;

public class AdapterArticulosEscaneados extends RecyclerView.Adapter<AdapterArticulosEscaneados.ProductViewHolder> {
    private static final String TAG = "AdapterDocumentos";
    private Context mCtx;
    private List<ModelArticulosEscaneados> productList;
    private CancelarArticuloEscaneado cancelarArticuloEscaneado;


    public AdapterArticulosEscaneados(Context mCtx, List<ModelArticulosEscaneados> productList, CancelarArticuloEscaneado cancelarArticuloEscaneado) {
        this.mCtx = mCtx;
        this.productList = productList;
        this.cancelarArticuloEscaneado = cancelarArticuloEscaneado;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_articulos, null);
        ProductViewHolder holder = new ProductViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position)
    {
        final ModelArticulosEscaneados product= productList.get(position);

        /*try {
            if (position == 0)
                holder.viewLateral.setBackgroundColor(Color.parseColor("#dcb467"));
            else
                holder.viewLateral.setBackgroundColor(Color.parseColor("#a63fff"));
        }catch (Throwable e){
            Log.e(TAG, "onBindViewHolder: " + e.getMessage());
        }*/

        holder.tvNumeroDeDocumento.setText(String.valueOf(position + 1));
        holder.tvNombreArticulo.setText(product.getNombre());
        holder.tvSerie.setText(product.getSerie());
        holder.tvFecha.setText(product.getFecha());

        holder.btBorrarArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarArticuloEscaneado.onClickCancelarArticulo(position, product.getId());
            }
        });
    }





    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvNumeroDeDocumento, tvNombreArticulo, tvSerie, tvFecha;
        ImageView btBorrarArticulo;
        public ProductViewHolder(View itemView)
        {
            super(itemView);
            tvNumeroDeDocumento= itemView.findViewById(R.id.tvNumeroDeDocumento);
            btBorrarArticulo= itemView.findViewById(R.id.btBorrarArticulo);
            tvNombreArticulo = itemView.findViewById(R.id.tvNombreArticulo);
            tvSerie = itemView.findViewById(R.id.tvSerie);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }


}
