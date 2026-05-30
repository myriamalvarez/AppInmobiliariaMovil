package com.myrsoft.appinmobiliariamovil.ui.pagos;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.myrsoft.appinmobiliariamovil.R;
import com.myrsoft.appinmobiliariamovil.modelo.Pago;
import java.util.List;

public class PagoAdapter extends RecyclerView.Adapter<PagoAdapter.PagoViewHolder> {

    private List<Pago> listaPagos;
    private Context context;
    private LayoutInflater inflater;

    public PagoAdapter(List<Pago> listaPagos, Context context, LayoutInflater inflater) {
        this.listaPagos = listaPagos;
        this.context = context;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public PagoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_pago, parent, false);
        return new PagoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PagoViewHolder holder, int position) {
        Pago pago = listaPagos.get(position);

        holder.tvNumeroPago.setText("#" + pago.getIdPago());
        holder.tvFechaPago.setText(pago.getFechaPago());
        holder.tvDetalle.setText(pago.getDetalle());
        holder.tvMonto.setText("$ " + String.format("%,.0f", pago.getMonto()));

        // Cambiar color del monto según si el pago está saldado o no
        if (pago.isEstado()) { // Asumiendo que 'estado' es 'true' para pago saldado
            holder.tvMonto.setTextColor(context.getResources().getColor(R.color.primary_purple_dark));
        } else { // Pago pendiente
            holder.tvMonto.setTextColor(Color.parseColor("#FF5722")); // color de alerta
        }
    }

    @Override
    public int getItemCount() {
        return listaPagos != null ? listaPagos.size() : 0;
    }

    public static class PagoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumeroPago;
        TextView tvFechaPago;
        TextView tvDetalle;
        TextView tvMonto;

        public PagoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroPago = itemView.findViewById(R.id.tvNumeroPago);
            tvFechaPago = itemView.findViewById(R.id.tvFechaPago);
            tvDetalle = itemView.findViewById(R.id.tvDetalle);
            tvMonto = itemView.findViewById(R.id.tvMonto);
        }
    }
}
