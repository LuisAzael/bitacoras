package com.example.bitacoras2020.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bitacoras2020.Callbacks.CancelarArticuloInstalacion;
import com.example.bitacoras2020.Database.Documentos;
import com.example.bitacoras2020.Database.Equipoinstalacion;
import com.example.bitacoras2020.R;
import com.example.bitacoras2020.Utils.ApplicationResourcesProvider;

import java.util.List;

public class AdapterEquiposInstalacion extends RecyclerView.Adapter<AdapterEquiposInstalacion.ProductViewHolder> {
    private static final String TAG = "AdapterDocumentos";
    private Context mCtx;
    private List<Equipoinstalacion> productList;
    private CancelarArticuloInstalacion cancelarArticuloInstalacion;


    public AdapterEquiposInstalacion(Context mCtx, List<Equipoinstalacion> productList, CancelarArticuloInstalacion cancelarArticuloInstalacion) {
        this.mCtx = mCtx;
        this.productList = productList;
        this.cancelarArticuloInstalacion = cancelarArticuloInstalacion;
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
        final Equipoinstalacion product= productList.get(position);

        /*if(position % 2 == 0) {
            holder.viewLateral.setBackgroundColor(Color.parseColor("#dcb467"));
        }
        else {
            holder.viewLateral.setBackgroundColor(Color.parseColor("#a63fff"));
        }*/

        holder.tvNombre.setTypeface(ApplicationResourcesProvider.bold);
        holder.tvSerie.setTypeface(ApplicationResourcesProvider.regular);

        holder.tvNumeroDeDocumento.setText(String.valueOf(position + 1));
        holder.tvNombre.setText(product.getNombre());
        holder.tvSerie.setText(product.getSerie());

        holder.btBorrarArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarArticuloInstalacion.onClickCancelarArticuloInstalacion(position, "" + product.getSerie(), "" + product.getFecha(), product.getBitacora());
            }
        });

        holder.layoutGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarArticuloInstalacion.onClickCancelarArticuloInstalacion(position, "" + product.getSerie(), "" + product.getFecha(), product.getBitacora());
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
            btBorrarArticulo = itemView.findViewById(R.id.btBorrarArticulo);
            layoutGeneral = itemView.findViewById(R.id.layoutGeneral);
        }
    }


}
