package com.example.bicpark.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bicpark.R;
import com.example.bicpark.model.Empresa;

import java.util.List;

public class AdapterEmpresa extends RecyclerView.Adapter<AdapterEmpresa.AdapterViewHolder> {

    List<Empresa> empresas;
    public static OnEmpresaClickListener listener;

    public AdapterEmpresa(List<Empresa> empresas, OnEmpresaClickListener listener) {
        this.empresas = empresas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_empresas, parent, false);
        AdapterViewHolder holder = new AdapterViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        holder.bind(empresas.get(position));
    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }

    TextView nombre;

    public class AdapterViewHolder extends RecyclerView.ViewHolder {
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.rve_nombre);
        }
        public void bind(Empresa item){
            nombre.setText(item.getNombre());
            nombre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(item);
                }
            });
        }
    }
}
