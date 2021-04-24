package com.example.bitacoras2020.Adapters;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bitacoras2020.Database.Documentos;
import com.example.bitacoras2020.Models.ModelNotificaciones;
import com.example.bitacoras2020.R;

import java.util.List;

public class AdapterDocumentos extends RecyclerView.Adapter<AdapterDocumentos.ProductViewHolder> {
    private static final String TAG = "AdapterDocumentos";
    private Context mCtx;
    private List<Documentos> productList;


    public AdapterDocumentos(Context mCtx, List<Documentos> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
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
        final Documentos product= productList.get(position);

        /*if(position % 2 == 0) {
            holder.viewLateral.setBackgroundColor(Color.parseColor("#dcb467"));
        }
        else {
            holder.viewLateral.setBackgroundColor(Color.parseColor("#a63fff"));
        }*/

        holder.tvNumeroDeDocumento.setText(String.valueOf(position + 1));
        holder.tvNombreDocumento.setText(product.getDocumento());
        holder.tvSerie.setText(product.getFecha());
        //holder.tvNombreDocumento.setVisibility(View.GONE);
    }





    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvNumeroDeDocumento, tvNombreDocumento, tvSerie;

        public ProductViewHolder(View itemView)
        {
            super(itemView);
            tvNumeroDeDocumento= itemView.findViewById(R.id.tvNumeroDeDocumento);
            tvNombreDocumento = itemView.findViewById(R.id.tvNombre);
            tvSerie = itemView.findViewById(R.id.tvSerie);
        }
    }


}
