package com.myrsoft.appinmobiliariamovil.ui.inmuebles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myrsoft.appinmobiliariamovil.R;
import com.myrsoft.appinmobiliariamovil.modelo.Inmueble;

import java.util.List;

public class InmuebleAdapter extends RecyclerView.Adapter<InmuebleAdapter.ViewHolderInmueble>{
    private List<Inmueble> inmuebles;
    private LayoutInflater li;

    public InmuebleAdapter(List<Inmueble> inmuebles, LayoutInflater li) {
        this.inmuebles = inmuebles;
        this.li = li;
    }

    @NonNull
    @Override
    public ViewHolderInmueble onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = li.inflate(R.layout.item_inmueble, parent, false);
        return new ViewHolderInmueble(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderInmueble holder, int position) {
        Inmueble inmueble = inmuebles.get(position);
        holder.direccion.setText(inmueble.getDireccion());
        holder.precio.setText(inmueble.getValor()+"");
        holder.ambientes.setText(inmueble.getAmbientes());
        holder.tipo.setText(inmueble.getTipo());
        holder.uso.setText(inmueble.getUso());
            Glide.with(holder.itemView.getContext())
                    .load("https://capacitacion.alwaysdata.net" + inmueble.getImagen())
                    .placeholder(null)
                    .into(holder.foto);
    }

    public int getItemCount(){
        return inmuebles.size();
    }

    public class ViewHolderInmueble extends RecyclerView.ViewHolder{
        private TextView direccion, precio, ambientes, tipo, uso;
        private ImageView foto;
        public ViewHolderInmueble(@NonNull View itemView) {
            super(itemView);
            direccion= itemView.findViewById(R.id.tvDireccionItem);
            precio= itemView.findViewById(R.id.tvPrecioItem);
            ambientes= itemView.findViewById(R.id.tvAmbientesItem);
            tipo= itemView.findViewById(R.id.tvTipoItem);
            uso= itemView.findViewById(R.id.tvUsoItem);
            foto= itemView.findViewById(R.id.ivInmueble);

        }
    }
}
