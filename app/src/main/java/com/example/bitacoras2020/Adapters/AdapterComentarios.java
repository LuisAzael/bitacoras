package com.example.bitacoras2020.Adapters;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bitacoras2020.Models.ModelComentarios;
import com.example.bitacoras2020.Models.ModelNotificaciones;
import com.example.bitacoras2020.R;

import java.util.List;

public class AdapterComentarios extends RecyclerView.Adapter<AdapterComentarios.ProductViewHolder> {
    private static final String TAG = "AdapterComentarios";
    private Context mCtx;
    private List<ModelComentarios> productList;


    public AdapterComentarios(Context mCtx, List<ModelComentarios> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_comentario, null);
        ProductViewHolder holder = new ProductViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position)
    {
        final ModelComentarios product= productList.get(position);

        if(product.getComentarioCreadoPorMi().equals("1")) {
            holder.viewLateral.setBackgroundColor(Color.parseColor("#dcb467"));
            CardView.LayoutParams params = new CardView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.START;
            params.setMargins(5,5,5,5);
            holder.cardContainer.setLayoutParams(params);
            holder.cardContainer.setRotation(0f);
            holder.layoutTextos.setRotation(0f);
        }
        else {
            holder.viewLateral.setBackgroundColor(Color.parseColor("#a63fff"));

            CardView.LayoutParams params = new CardView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END;
            params.setMargins(5,5,5,5);
            holder.cardContainer.setLayoutParams(params);
            holder.cardContainer.setRotation(180f);
            holder.layoutTextos.setRotation(180f);
        }

        holder.tvComentario.setText(product.getComentario());
        holder.tvUsuario.setText(product.getUsuario());
        holder.tvFecha.setText(product.getFecha());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvComentario, tvUsuario, tvFecha;
        View viewLateral;
        CardView cardContainer;
        LinearLayout layoutTextos;

        public ProductViewHolder(View itemView)
        {
            super(itemView);
            viewLateral= itemView.findViewById(R.id.viewLateral);
            tvComentario = itemView.findViewById(R.id.tvComentario);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            cardContainer = itemView.findViewById(R.id.cardContainer);
            layoutTextos = itemView.findViewById(R.id.layoutTextos);
        }
    }


}
