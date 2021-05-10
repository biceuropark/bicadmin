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

    public void filtrartipo(String tipo, List<Plaza> plazas, AdapterPlaza adapterPlaza){
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();
        for (Plaza pl : plazas){
            if(pl.getTipo().contains(tipo)){
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
    public void filtrartiponumero(String numero, String tipo, List<Plaza> plazas, AdapterPlaza adapterPlaza){
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();
        for (Plaza pl : plazas){
            if(String.valueOf(pl.getNumero()).contains(numero) && pl.getTipo().contains(tipo)){
                plazaArrayList.add(pl);
            }
        }
        adapterPlaza.filtrar(plazaArrayList);
    }
    public void filtrartipoempresa(String tipo, String empresa, List<Plaza> plazas, AdapterPlaza adapterPlaza){
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();
        for (Plaza pl : plazas){
            if(pl.getTipo().contains(tipo) && pl.getEmpresa().contains(empresa)){
                plazaArrayList.add(pl);
            }
        }
        adapterPlaza.filtrar(plazaArrayList);
    }
    public void filtrartipoestado(String tipo, String estado, List<Plaza> plazas, AdapterPlaza adapterPlaza){
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();
        for (Plaza pl : plazas){
            if(pl.getTipo().contains(tipo) && pl.getEstado().contains(estado)){
                plazaArrayList.add(pl);
            }
        }
        adapterPlaza.filtrar(plazaArrayList);
    }

    public void filtrarnumerotipoempresa(String numero, String tipo, String empresa, List<Plaza> plazas, AdapterPlaza adapterPlaza){
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();
        for (Plaza pl : plazas){
            if(pl.getEmpresa().contains(empresa) && pl.getTipo().contains(tipo) && String.valueOf(pl.getNumero()).contains(numero)){
                plazaArrayList.add(pl);
            }
        }
        adapterPlaza.filtrar(plazaArrayList);
    }

    public void filtrarnumerotipoestado(String numero, String tipo, String estado, List<Plaza> plazas, AdapterPlaza adapterPlaza){
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();
        for (Plaza pl : plazas){
            if(pl.getTipo().contains(tipo) && pl.getEstado().contains(estado) && String.valueOf(pl.getNumero()).contains(numero)){
                plazaArrayList.add(pl);
            }
        }
        adapterPlaza.filtrar(plazaArrayList);
    }

    public void filtrartipoempresaestado(String tipo, String empresa, String estado,  List<Plaza> plazas, AdapterPlaza adapterPlaza){
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();
        for (Plaza pl : plazas){
            if(pl.getTipo().contains(tipo) && pl.getEstado().contains(estado) && pl.getEmpresa().contains(empresa)){
                plazaArrayList.add(pl);
            }
        }
        adapterPlaza.filtrar(plazaArrayList);
    }

    public void filtrarnumeroempresaestado(String numero, String estado, String empresa, List<Plaza> plazas, AdapterPlaza adapterPlaza){
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();
        for (Plaza pl : plazas){
            if(pl.getEmpresa().contains(empresa) && pl.getEstado().contains(estado) && String.valueOf(pl.getNumero()).contains(numero)){
                plazaArrayList.add(pl);
            }
        }
        adapterPlaza.filtrar(plazaArrayList);
    }

    public void filtrartodo(String estado, String empresa, String numero, String tipo, List<Plaza> plazas, AdapterPlaza adapterPlaza){
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();
        for (Plaza pl : plazas){
            if(pl.getEmpresa().contains(empresa) && pl.getEstado().contains(estado) && String.valueOf(pl.getNumero()).contains(numero) && pl.getTipo().contains(tipo)){
                plazaArrayList.add(pl);
            }
        }
        adapterPlaza.filtrar(plazaArrayList);
    }

}
