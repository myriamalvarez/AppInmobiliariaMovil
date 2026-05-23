package com.myrsoft.appinmobiliariamovil.ui.inmuebles;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myrsoft.appinmobiliariamovil.R;
import com.myrsoft.appinmobiliariamovil.modelo.Inmueble;
import com.myrsoft.appinmobiliariamovil.request.ApiClient;

import java.util.List;

public class InmuebleAdapter extends RecyclerView.Adapter<InmuebleAdapter.ViewHolderInmueble>{
    private List<Inmueble> inmuebles;
    private Context context;
    private LayoutInflater inflater;

    public InmuebleAdapter(List<Inmueble> inmuebles, Context context, LayoutInflater inflater) {
        this.inmuebles = inmuebles;
        this.context = context;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public ViewHolderInmueble onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_inmueble, parent, false);
        return new ViewHolderInmueble(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderInmueble holder, int position) {
        Inmueble inmueble = inmuebles.get(position);
        holder.direccion.setText(inmueble.getDireccion());
        holder.precio.setText("$" + inmueble.getValor());
        holder.ambientes.setText(inmueble.getAmbientes()+" ambientes");
        holder.tipo.setText(inmueble.getTipo());
        if (inmueble.isDisponible()) {
            holder.disponible.setText("☑️ Disponible");
            holder.disponible.setTextColor(context.getColor(R.color.disponible));
            holder.disponible.setBackgroundResource(0);
        } else {
            holder.disponible.setText("❌ No disponible");
            holder.disponible.setTextColor(context.getColor(R.color.no_disponible));
            holder.disponible.setBackgroundResource(0);
        }
            Glide.with(holder.itemView.getContext())
                    .load(ApiClient.BASE_URL + inmueble.getImagen())
                    .placeholder(R.drawable.casa)
                    .into(holder.foto);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Bundle bundle = new Bundle();
               bundle.putSerializable("inmueble", inmueble);
               Navigation.findNavController((Activity) v.getContext(), R.id.nav_host_fragment_content_menu).navigate(R.id.detalleInmuebleFragment, bundle);
            }
        });
    }

    public int getItemCount(){
        return inmuebles.size();
    }

    public class ViewHolderInmueble extends RecyclerView.ViewHolder{
        private TextView direccion, precio, ambientes, tipo, disponible;
        private ImageView foto;
        public ViewHolderInmueble(@NonNull View itemView) {
            super(itemView);
            direccion= itemView.findViewById(R.id.tvDireccionItem);
            precio= itemView.findViewById(R.id.tvPrecioItem);
            ambientes= itemView.findViewById(R.id.tvAmbientesItem);
            tipo= itemView.findViewById(R.id.tvTipoItem);
            disponible= itemView.findViewById(R.id.tvEstadoItem);
            foto= itemView.findViewById(R.id.ivInmueble);

        }
    }
}
