package com.myrsoft.appinmobiliariamovil.ui.inquilinos;

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

public class InquilinoAdapter extends RecyclerView.Adapter<InquilinoAdapter.ViewHolder> {
    private List<Inmueble> inmuebles;
    private Context context;
    private LayoutInflater inflater;

    public InquilinoAdapter(List<Inmueble> inmuebles, Context context, LayoutInflater inflater) {
        this.inmuebles = inmuebles;
        this.context = context;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_inquilino, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inmueble inmueble = inmuebles.get(position);
        
        // Mostrar datos del inmueble
        holder.tvDireccion.setText(inmueble.getDireccion());
        holder.tvTipoUso.setText(inmueble.getTipo() + " - " + inmueble.getUso());
        holder.tvAmbientes.setText(inmueble.getAmbientes() + " ambientes");
        
        // Cargar imagen del inmueble
        if (inmueble.getImagen() != null && !inmueble.getImagen().isEmpty()) {
            Glide.with(context)
                    .load(ApiClient.BASE_URL + inmueble.getImagen())
                    .placeholder(R.drawable.casa)
                    .into(holder.ivInmueble);
        }
        
        // Al hacer click, pasamos el inmueble completo usando Serializable
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("inmueble", inmueble);
            Navigation.findNavController(v).navigate(R.id.detalleInquilinoFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return inmuebles != null ? inmuebles.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDireccion, tvTipoUso, tvAmbientes;
        ImageView ivInmueble, ivArrow;
        
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvTipoUso = itemView.findViewById(R.id.tvTipoUso);
            tvAmbientes = itemView.findViewById(R.id.tvAmbientes);
            ivInmueble = itemView.findViewById(R.id.ivInmueble);
            ivArrow = itemView.findViewById(R.id.ivArrow);
        }
    }
}