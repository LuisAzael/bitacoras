package com.example.bitacoras2020.Adapters;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bitacoras2020.Callbacks.CancelarArticuloCortejo;
import com.example.bitacoras2020.Database.Equipocortejo;
import com.example.bitacoras2020.Database.Equipoinstalacion;
import com.example.bitacoras2020.R;
import com.example.bitacoras2020.Utils.ApplicationResourcesProvider;

import java.util.List;

public class AdapterEquiposCortejo extends RecyclerView.Adapter<AdapterEquiposCortejo.ProductViewHolder> {
    private static final String TAG = "AdapterDocumentos";
    private Context mCtx;
    private List<Equipocortejo> productList;
    private CancelarArticuloCortejo cancelarArticuloCortejo;


    public AdapterEquiposCortejo(Context mCtx, List<Equipocortejo> productList, CancelarArticuloCortejo cancelarArticuloCortejo) {
        this.mCtx = mCtx;
        this.productList = productList;
        this.cancelarArticuloCortejo = cancelarArticuloCortejo;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_documento_extra, null);
        ProductViewHolder holder = new ProductViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position)
    {
        final Equipocortejo product= productList.get(position);

        /*if(position % 2 == 0) {
            holder.layoutItem.setBackgroundColor(Color.parseColor("#eeeeee"));
        }
        else {
            holder.layoutItem.setBackgroundColor(Color.parseColor("#ffffff"));
        }*/

        holder.tvNombre.setTypeface(ApplicationResourcesProvider.bold);
        holder.tvSerie.setTypeface(ApplicationResourcesProvider.regular);

        holder.tvNumeroDeDocumento.setText(String.valueOf(position + 1));
        holder.tvNombre.setText(product.getNombre());
        holder.tvSerie.setText(product.getSerie());
        holder.btBorrarArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarArticuloCortejo.onClickCancelarArticuloCortejo(position, "" + product.getSerie(), "" + product.getFecha(), product.getBitacora());
            }
        });
        holder.layoutGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarArticuloCortejo.onClickCancelarArticuloCortejo(position, "" + product.getSerie(), "" + product.getFecha(), product.getBitacora());
            }
        });
    }





    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvNumeroDeDocumento, tvNombre, tvSerie;
        ImageButton btBorrarArticulo;
        LinearLayout layoutGeneral;
        public ProductViewHolder(View itemView)
        {
            super(itemView);
            tvNumeroDeDocumento= itemView.findViewById(R.id.tvNumeroDeDocumento);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvSerie = itemView.findViewById(R.id.tvSerie);
            //layoutItem = itemView.findViewById(R.id.layoutItem);
            btBorrarArticulo = itemView.findViewById(R.id.btBorrarArticulo);
            layoutGeneral = itemView.findViewById(R.id.layoutGeneral);
        }
    }


}
