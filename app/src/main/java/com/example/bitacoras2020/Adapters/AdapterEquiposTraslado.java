package com.example.bitacoras2020.Adapters;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bitacoras2020.Callbacks.CancelarArticuloTraslado;
import com.example.bitacoras2020.Database.EquipoRecoleccion;
import com.example.bitacoras2020.Database.EquipoTraslado;
import com.example.bitacoras2020.R;
import com.example.bitacoras2020.Utils.ApplicationResourcesProvider;

import java.util.List;

public class AdapterEquiposTraslado extends RecyclerView.Adapter<AdapterEquiposTraslado.ProductViewHolder> {
    private static final String TAG = "AdapterEquiposTraslado";
    private Context mCtx;
    private List<EquipoTraslado> productList;
    private CancelarArticuloTraslado cancelarArticuloTraslado;


    public AdapterEquiposTraslado(Context mCtx, List<EquipoTraslado> productList, CancelarArticuloTraslado cancelarArticuloTraslado) {
        this.mCtx = mCtx;
        this.productList = productList;
        this.cancelarArticuloTraslado = cancelarArticuloTraslado;
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
        final EquipoTraslado product= productList.get(position);

        /*if(position % 2 == 0) {
            holder.viewLateral.setBackgroundColor(Color.parseColor("#dcb467"));
        }
        else {
            holder.viewLateral.setBackgroundColor(Color.parseColor("#a63fff"));
        }*/

        holder.tvNombre.setTypeface(ApplicationResourcesProvider.bold);
        holder.tvSerie.setTypeface(ApplicationResourcesProvider.regular);
        holder.tvEntradaSalida.setTypeface(ApplicationResourcesProvider.light);

        holder.tvNumeroDeDocumento.setText(String.valueOf(position + 1));
        holder.tvNombre.setText(product.getNombre());
        holder.tvSerie.setText(product.getSerie());
        holder.btBorrarArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarArticuloTraslado.onClickCancelarArticuloTraslado(position, "" + product.getSerie(), "" + product.getFecha(), product.getBitacora());
            }
        });

        if(product.getSerie().contains("CL") ||product.getSerie().contains("CB") )
            holder.tvEntradaSalida.setVisibility(View.VISIBLE);
        else
            holder.tvEntradaSalida.setVisibility(View.GONE);

        if (product.getTipo().equals("0")){
            holder.tvEntradaSalida.setTextColor(Color.parseColor("#9a0007"));
            holder.tvEntradaSalida.setText("Salida de inventario");}
        else if(product.getTipo().equals("1")) {
            holder.tvEntradaSalida.setText("Entrada de inventario");
            holder.tvEntradaSalida.setTextColor(Color.parseColor("#ff669900"));
        }
    }





    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvNumeroDeDocumento, tvNombre, tvSerie, tvEntradaSalida;
        ImageButton btBorrarArticulo;
        public ProductViewHolder(View itemView)
        {
            super(itemView);
            tvNumeroDeDocumento= itemView.findViewById(R.id.tvNumeroDeDocumento);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvSerie = itemView.findViewById(R.id.tvSerie);
            btBorrarArticulo = itemView.findViewById(R.id.btBorrarArticulo);
            tvEntradaSalida = itemView.findViewById(R.id.tvEntradaSalida);
        }
    }


}
