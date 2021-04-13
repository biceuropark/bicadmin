package com.example.bicpark.recycler;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bicpark.R;
import com.example.bicpark.model.Plaza;

import java.util.List;


public class AdapterPlaza extends RecyclerView.Adapter<AdapterPlaza.AdapterViewHolder> {

    private List<Plaza> plazas;
    public static OnPlazaClickListener listener;

    public AdapterPlaza(List<Plaza> plazas, OnPlazaClickListener listener) {
        this.plazas = plazas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_main, parent, false);
        AdapterViewHolder holder = new AdapterViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        holder.bind(plazas.get(position));
    }

    @Override
    public int getItemCount() {
        return plazas.size();
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder {

        TextView numero, estado, empresa;
        ImageButton btneditar;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            numero = itemView.findViewById(R.id.rvm_numero);
            empresa = itemView.findViewById(R.id.rvm_empresa);
            estado = itemView.findViewById(R.id.rvm_estado);
            btneditar = itemView.findViewById(R.id.rvm_inf);
        }

        private void bind(Plaza pl){
            numero.setText(String.valueOf(pl.getNumero()));
            empresa.setText(pl.getEmpresa());
            estado.setText(pl.getEstado());

            if(pl.getEstado() == "Libre"){
                estado.setTextColor(Color.rgb(0, 255, 0));
            }else if(pl.getEstado() == "Nula"){
                estado.setTextColor(Color.rgb(255, 0, 0));
            }else if(pl.getEstado() == "Alquilada"){
                estado.setTextColor(Color.rgb(255, 255, 0));
            }
            btneditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnClick(pl);
                }
            });
        }
    }
}
