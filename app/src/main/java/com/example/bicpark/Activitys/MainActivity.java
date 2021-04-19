package com.example.bicpark.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bicpark.R;
import com.example.bicpark.filtros.Filtros;
import com.example.bicpark.model.Empresa;
import com.example.bicpark.model.Plaza;
import com.example.bicpark.recycler.AdapterPlaza;
import com.example.bicpark.recycler.OnPlazaClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth fbAuth;
    private FirebaseDatabase fDatabase;
    private DatabaseReference databaseReference;

    private EditText SearchNumber;
    private Spinner SearchEstado;
    private SearchableSpinner SearchEmpresa;
    private TextView contador;
    private String listener = "";

    private ArrayList<Plaza> plazaArrayList;
    private Filtros filtros;
    private String empresa_f;
    private String estado_f;

    private RecyclerView recyclerView;
    private AdapterPlaza adapterPlaza;
    private List<Plaza> plazas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseInits();
        ViewInits();

        recyclerView = findViewById(R.id.main_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        plazas = new ArrayList<>();
        construirspinner();
        cargardatos();
        cargarEmpresas();

        estado_f = SearchEstado.getSelectedItem().toString();
        filtros = new Filtros();


        //Editext con observable para buscar el numero

        SearchNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                listener = String.valueOf(editable);
                filtrado();
            }
        });

        //Spinner para buscar el estado

        SearchEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                estado_f = SearchEstado.getSelectedItem().toString();
                filtrado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        SearchEmpresa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                empresa_f = SearchEmpresa.getSelectedItem().toString();
                filtrado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Construcción del adaptador

        adapterPlaza = new AdapterPlaza(plazas, new OnPlazaClickListener() {
            @Override
            public void OnClick(Plaza p) {
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                intent.putExtra("explaza", p);
                setResult(RESULT_OK, intent);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapterPlaza);
    }

    //Cargar las plazas de la base de datos

    private void cargardatos() {
        databaseReference.child("Plazas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                plazas.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Plaza plaza = ds.getValue(Plaza.class);
                    plazas.add(plaza);
                }
                adapterPlaza.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //método de filtrado, llamada a los métodos de la clase Filtrado

    private void filtrado() {
        if (listener == "" && estado_f == "Todos" && empresa_f == "Todas") {
            adapterPlaza.filtrar((ArrayList<Plaza>) plazas);
        } else if (listener != "" && estado_f == "Todos" && empresa_f == "Todas") {
            filtros.filtrarnumero(listener, plazas, adapterPlaza);
        } else if (listener == "" && estado_f != "Todos" && empresa_f == "Todas") {
            filtros.filtrarestado(estado_f, plazas, adapterPlaza);
        } else if (listener == "" && estado_f == "Todos" && empresa_f != "Todas") {
            filtros.filtrarempresa(empresa_f, plazas, adapterPlaza);
        } else if (listener != "" && estado_f != "Todos" && empresa_f == "Todas") {
            filtros.filtrarnumeroestado(listener, estado_f, plazas, adapterPlaza);
        } else if (listener != "" && estado_f == "Todos" && empresa_f != "Todas") {
            filtros.filtrarnumeroempresa(listener, empresa_f, plazas, adapterPlaza);
        } else if (listener == "" && estado_f != "Todos" && empresa_f != "Todas") {
            filtros.filtrarestadoempresa(estado_f, empresa_f, plazas, adapterPlaza);
        } else if (listener != "" && estado_f != "Todos" && empresa_f != "Todas") {
            filtros.filtrartodo(estado_f, empresa_f, listener, plazas, adapterPlaza);
        }
        contador.setText("Plazas según el criterio de búsqueda: " + adapterPlaza.getItemCount());
        ordenar();

    }

    //Ordenar plazas por numero

    private void ordenar() {
        Collections.sort(plazas, new Comparator<Plaza>() {
            @Override
            public int compare(Plaza plaza, Plaza t1) {
                return Integer.compare(plaza.getNumero(), t1.getNumero());
            }
        });
    }

    //inicialización de objetos

    private void FirebaseInits() {

        fbAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        databaseReference = fDatabase.getReference();
    }

    private void ViewInits() {
        SearchNumber = findViewById(R.id.main_searchnumber);
        SearchEstado = findViewById(R.id.main_searchestado);
        SearchEmpresa = findViewById(R.id.main_searchempresa);
        contador = findViewById(R.id.main_contador);

    }

    //Construcción spinner personalizado

    private void construirspinner() {
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Todos");
        spinnerArray.add("Libre");
        spinnerArray.add("Alquilada");
        spinnerArray.add("Nula");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        SearchEstado.setAdapter(spinnerAdapter);
    }

    //Carga de empresas de la base de datos

    private void cargarEmpresas() {
        List<Empresa> empresas = new ArrayList<>();
        databaseReference.child("Empresas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String nombre = ds.child("nombre").getValue().toString();
                    Empresa emp = new Empresa();
                    emp.setNombre(nombre);
                    empresas.add(emp);
                }
                Empresa empresa1 = new Empresa();
                empresa1.setNombre("Todas");
                empresas.add(0, empresa1);
                ArrayAdapter<Empresa> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, empresas);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SearchEmpresa.setAdapter(arrayAdapter);
                SearchEmpresa.setTitle("Empresas");
                empresa_f = SearchEmpresa.getSelectedItem().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //Métodos para construir menu

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_empresa:
                Intent empresa = new Intent(MainActivity.this, EmpresaActivity.class);
                startActivity(empresa);
                break;
            case R.id.item_aparca:
                Intent aparca = new Intent(MainActivity.this, PlazaActivity.class);
                startActivity(aparca);
                break;
            case R.id.item_edi:
                Intent edi = new Intent(MainActivity.this, EdificioActivity.class);
                startActivity(edi);
                break;
            case R.id.item_des:
                AlertDisconect();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //LLamada al método BackPressed cuando se pulsa el botón atrás

    @Override
    public void onBackPressed() {
        AlertDisconect();
    }

    //Alerta de desconexión

    public void AlertDisconect() {
        MaterialAlertDialogBuilder Dialog = new MaterialAlertDialogBuilder(MainActivity.this);
        Dialog.setTitle("Desconectando...");
        Dialog.setMessage("¿Quieres salir de la app?");
        Dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fbAuth.signOut();
                Intent logout = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(logout);
                finish();
            }
        });
        Dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).show();
    }
}