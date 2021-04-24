package com.example.bitacoras2020.Adapters;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bitacoras2020.Callbacks.RegistrarSalidaCallback;
import com.example.bitacoras2020.Callbacks.RequerirEventoDeSalidaCallback;
import com.example.bitacoras2020.Callbacks.ShowDetailsBitacoraCallback;
import com.example.bitacoras2020.Callbacks.TerminarBitacoraCallback;
import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.example.bitacoras2020.Models.ModelBitacorasActivas;
import com.example.bitacoras2020.Models.ModelNotificaciones;
import com.example.bitacoras2020.R;

import java.util.List;

public class AdapterNotificaciones extends RecyclerView.Adapter<AdapterNotificaciones.ProductViewHolder> {
    private static final String TAG = "AdapterNotificaciones";
    private Context mCtx;
    private List<ModelNotificaciones> productList;


    public AdapterNotificaciones(Context mCtx, List<ModelNotificaciones> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_notificacion, null);
        ProductViewHolder holder = new ProductViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position)
    {
        final ModelNotificaciones product= productList.get(position);

        if(product.getBitacora().equals("")) {
            holder.viewLateral.setBackgroundColor(Color.parseColor("#dcb467"));
            holder.tvBitacora.setVisibility(View.GONE);
        }
        else {
            holder.viewLateral.setBackgroundColor(Color.parseColor("#a63fff"));
            holder.tvBitacora.setVisibility(View.VISIBLE);
            holder.tvBitacora.setText(product.getBitacora());
        }

        holder.tvTitulo.setText(product.getTitulo());
        holder.tvBody.setText(product.getBody());
        holder.tvFecha.setText(product.getFecha());
    }





    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvTitulo, tvBody, tvFecha, tvBitacora;
        View viewLateral;


        public ProductViewHolder(View itemView)
        {
            super(itemView);
            viewLateral= itemView.findViewById(R.id.viewLateral);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvBitacora = itemView.findViewById(R.id.tvBitacora);
        }
    }


}
