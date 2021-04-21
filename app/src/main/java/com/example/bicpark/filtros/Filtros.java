package com.example.bicpark.filtros;

import android.widget.Adapter;

import com.example.bicpark.model.Plaza;
import com.example.bicpark.recycler.AdapterPlaza;

import java.util.ArrayList;
import java.util.List;

public class  Filtros {

    public void filtrarnumero(String numero, List<Plaza> plazas, AdapterPlaza adapterPlaza) {
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();
        for (Plaza pl : plazas) {
            if (String.valueOf(pl.getNumero()).contains(numero)) {
                plazaArrayList.add(pl);
            }
        }
        adapterPlaza.filtrar(plazaArrayList);
    }


    public void filtrarempresa(String empresa, List<Plaza> plazas, AdapterPlaza adapterPlaza){
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();

        for (Plaza pl : plazas){
            if(pl.getEmpresa().contains(empresa)){
                plazaArrayList.add(pl);
            }
        }
        adapterPlaza.filtrar(plazaArrayList);
    }

    public void filtrarestado(String estado, List<Plaza> plazas, AdapterPlaza adapterPlaza){
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();
        for (Plaza pl : plazas){
            if(pl.getEstado().contains(estado)){
                plazaArrayList.add(pl);
            }
        }
        adapterPlaza.filtrar(plazaArrayList);
    }


    public void filtrarnumeroestado(String numero, String estado, List<Plaza> plazas, AdapterPlaza adapterPlaza){
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();
        for (Plaza pl : plazas){
            if(String.valueOf(pl.getNumero()).contains(numero) && pl.getEstado().contains(estado)){
                plazaArrayList.add(pl);
            }
        }
        adapterPlaza.filtrar(plazaArrayList);
    }

    public void filtrarnumeroempresa(String numero, String empresa, List<Plaza> plazas, AdapterPlaza adapterPlaza){
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();
        for (Plaza pl : plazas){
            if(String.valueOf(pl.getNumero()).contains(numero) && pl.getEmpresa().contains(empresa)){
                plazaArrayList.add(pl);
            }
        }
        adapterPlaza.filtrar(plazaArrayList);
    }

    public void filtrarestadoempresa(String estado, String empresa, List<Plaza> plazas, AdapterPlaza adapterPlaza){
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();
        for (Plaza pl : plazas){
            if(pl.getEmpresa().contains(empresa) && pl.getEstado().contains(estado)){
                plazaArrayList.add(pl);
            }
        }
        adapterPlaza.filtrar(plazaArrayList);
    }

    public void filtrartodo(String estado, String empresa, String numero, List<Plaza> plazas, AdapterPlaza adapterPlaza){
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();
        for (Plaza pl : plazas){
            if(pl.getEmpresa().contains(empresa) && pl.getEstado().contains(estado) && String.valueOf(pl.getNumero()).contains(numero)){
                plazaArrayList.add(pl);
            }
        }
        adapterPlaza.filtrar(plazaArrayList);
    }

}
