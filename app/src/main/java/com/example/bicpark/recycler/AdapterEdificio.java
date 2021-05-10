package com.example.bicpark.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.bicpark.R;

import com.example.bicpark.model.Oficina;

import com.example.bicpark.model.Plaza;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdapterEdificio extends RecyclerView.Adapter<AdapterEdificio.AdapterViewHolder> {

    private List<Oficina> oficinas;
    public static OnOficicinaClickListener listener;


    public AdapterEdificio(List<Oficina> oficinas, OnOficicinaClickListener listener) {
        this.oficinas = oficinas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_planta, parent, false);
        AdapterViewHolder holder = new AdapterViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        holder.bind(oficinas.get(position));
    }

    @Override
    public int getItemCount() {
        return oficinas.size();

    }


    public class AdapterViewHolder extends RecyclerView.ViewHolder {


        TextView numero, empresa, iden;
        ImageButton editar;


        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            empresa = itemView.findViewById(R.id.rvpl_empresa);
            numero = itemView.findViewById(R.id.rvpl_numero);
            editar = itemView.findViewById(R.id.rvpl_editar);
            iden = itemView.findViewById(R.id.rvpl_identificador);
        }


        private void bind(Oficina oficina) {

            numero.setText(String.valueOf(oficina.getNumero()));
            empresa.setText(oficina.getEmpresa());
            iden.setText(oficina.getIdentificador());
            editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(oficina);
                }
            });
        }


    }
}
