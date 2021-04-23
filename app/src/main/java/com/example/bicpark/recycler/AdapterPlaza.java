package com.example.bicpark.recycler;



import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bicpark.R;
import com.example.bicpark.model.Plaza;

import java.util.ArrayList;
import java.util.List;



public class AdapterPlaza extends RecyclerView.Adapter<AdapterPlaza.AdapterViewHolder>{

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

    public void filtrar(ArrayList<Plaza> filtroplazas){
        this.plazas = filtroplazas;
        notifyDataSetChanged();
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
            switch (pl.getEstado()){
                case "Alquilada" :
                    estado.setTextColor(Color.rgb(119, 119,227));
                    estado.setText(pl.getEstado());
                    break;
                case "Libre" :
                    estado.setTextColor(Color.rgb(0, 200,0));
                    estado.setText(pl.getEstado());
                    break;
                case "Nula" :
                    estado.setTextColor(Color.rgb(200, 0,0));
                    estado.setText(pl.getEstado());
                    break;
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
