package com.myrsoft.appinmobiliariamovil.ui.contratos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ContratoAdapter extends RecyclerView.Adapter<ContratoAdapter.ViewHolder> {
    private List<Inmueble> inmuebles;
    private Context context;
    private LayoutInflater inflater;

    public ContratoAdapter(List<Inmueble> inmuebles, Context context, LayoutInflater inflater) {
        this.inmuebles = inmuebles;
        this.context = context;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_contrato, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inmueble inmueble = inmuebles.get(position);
        
        // Datos del inmueble (lo que viene en la lista)
        holder.tvDireccion.setText(inmueble.getDireccion());
        holder.tvTipoUso.setText(inmueble.getTipo() + " - " + inmueble.getUso());
        holder.tvAmbientes.setText(inmueble.getAmbientes() + " ambientes");
        holder.tvMonto.setText("$ " + String.format("%,.0f", inmueble.getValor()));
        holder.txFechas.setText(inmueble.isTieneContratoVigente() ? "Contrato vigente" : "Sin contrato vigente");
        
        if (inmueble.getImagen() != null && !inmueble.getImagen().isEmpty()) {
            Glide.with(context)
                    .load(ApiClient.BASE_URL + inmueble.getImagen())
                    .placeholder(R.drawable.casa)
                    .into(holder.ivInmueble);
        }
        
        holder.btnVerDetalle.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("idInmueble", inmueble.getIdInmueble());
            Navigation.findNavController(v).navigate(R.id.detalleContratoFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return inmuebles != null ? inmuebles.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDireccion, tvTipoUso, tvAmbientes, tvMonto, txFechas;
        ImageView ivInmueble;
        Button btnVerDetalle;
        
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvTipoUso = itemView.findViewById(R.id.tvTipoUso);
            tvAmbientes = itemView.findViewById(R.id.tvAmbientes);
            tvMonto = itemView.findViewById(R.id.tvMonto);
            txFechas = itemView.findViewById(R.id.txFechas); // Corregido el ID
            ivInmueble = itemView.findViewById(R.id.ivInmueble);
            btnVerDetalle = itemView.findViewById(R.id.btnVerDetalle);
        }
    }
}