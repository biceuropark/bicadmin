package com.example.bicpark.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bicpark.R;
import com.example.bicpark.Servicios.Descarga;
import com.example.bicpark.Servicios.Filtros;
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

    private static final int PERMISO_STORAGE = 356;
    private FirebaseAuth fbAuth;
    private FirebaseDatabase fDatabase;
    private DatabaseReference databaseReference;

    private EditText SearchNumber;
    private Spinner SearchEstado, SearchTipo;
    private SearchableSpinner SearchEmpresa;
    private TextView contador;
    private String listener = "";
    private ImageView bicgif;

    private Filtros filtros;
    private Descarga descarga;
    private String empresa_f;
    private String estado_f;
    private String tipo_f;

    private RecyclerView recyclerView;
    private AdapterPlaza adapterPlaza;
    private List<Plaza> plazas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //////////////
        FirebaseInits();
        ViewInits();
        //////////////
        //Recycler
        recyclerView = findViewById(R.id.main_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        plazas = new ArrayList<>();
        //Gif de carga
        Glide.with(getApplicationContext()).load(R.drawable.gifdefinitivo).into(bicgif);

        //Cargar datos de la base de datos
        construirspinnerestados();
        construirspinnertipo();
        cargardatos();
        cargarEmpresas();

        //Guardar selección del spinner de estados
        estado_f = SearchEstado.getSelectedItem().toString();
        tipo_f = SearchTipo.getSelectedItem().toString();
        //Llamada a filtros
        filtros = new Filtros();
        //Llamada Descarga
        descarga = new Descarga();

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
                //Guardar los datos del edittext cada vez que cambie
                listener = String.valueOf(editable);
                //Ejecutar filtrado
                filtrado();
            }
        });

        SearchTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tipo_f = SearchTipo.getSelectedItem().toString();
                filtrado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Spinner para buscar el estado
        SearchEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Guardar la selección del estado cada vez que cambie
                estado_f = SearchEstado.getSelectedItem().toString();
                //Ejecutar filtrado
                filtrado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        SearchEmpresa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Guardar la selección del estado cada vez que cambie
                empresa_f = SearchEmpresa.getSelectedItem().toString();
                //Ejecutar filtrado
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
                //Creo la intención
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                //Guardamos los datos de la plaza en la intención
                intent.putExtra("explaza", p);
                setResult(RESULT_OK, intent);
                //Voy a la actividad
                startActivity(intent);
            }
        });
        //Asigno el adapter al recyclerview
        recyclerView.setAdapter(adapterPlaza);
    }

    //Cargar las plazas de la base de datos
    private void cargardatos() {
        //Directorio de los datos
        databaseReference.child("Plazas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Limpio la lista antes de cargar los datos
                plazas.clear();
                //Bucle para ir cargando los datos
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //Obtengo el objeto de la base de datos y lo agrego a la lista
                    Plaza plaza = ds.getValue(Plaza.class);
                    plazas.add(plaza);
                }
                bicgif.setVisibility(View.GONE);
                //Notifico cambios al adaptador
                adapterPlaza.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { /*Nothing*/}
        });
    }

    //método de filtrado, llamada a los métodos de la clase Filtrado
    private void filtrado() {

        //Filtrar TOdOS
        if (listener == "" && estado_f == "Todos" && empresa_f == "Todas" && tipo_f == "Todos") {
            adapterPlaza.filtrar((ArrayList<Plaza>) plazas);
        }
        //Filtrar SOlO NUMERO
        else if (listener != "" && estado_f == "Todos" && empresa_f == "Todas" && tipo_f == "Todos") {
            filtros.filtrarnumero(listener, plazas, adapterPlaza);
        }
        //Filtrar SOLO ESTADO
        else if (listener == "" && estado_f != "Todos" && empresa_f == "Todas" && tipo_f == "Todos") {
            filtros.filtrarestado(estado_f, plazas, adapterPlaza);
        }
        //Filtrar SOlO EMPrESA
        else if (listener == "" && estado_f == "Todos" && empresa_f != "Todas" && tipo_f == "Todos") {
            filtros.filtrarempresa(empresa_f, plazas, adapterPlaza);
        //Filtrar SOLO TIPO
        } else if(listener == "" && estado_f == "Todos" && empresa_f == "Todas" && tipo_f != "Todos"){
            filtros.filtrartipo(tipo_f, plazas, adapterPlaza);

        }
        //FILTRAR NUMERO Y ESTADO
        else if (listener != "" && estado_f != "Todos" && empresa_f == "Todas" && tipo_f == "Todos") {
            filtros.filtrarnumeroestado(listener, estado_f, plazas, adapterPlaza);
        }
        //Filtrar NUMERO Y EMPRESA
        else if (listener != "" && estado_f == "Todos" && empresa_f != "Todas" && tipo_f == "Todos") {
            filtros.filtrarnumeroempresa(listener, empresa_f, plazas, adapterPlaza);
        }
        //Filtrar EMPRESA Y ESTADO
        else if (listener == "" && estado_f != "Todos" && empresa_f != "Todas" && tipo_f == "Todos") {
            filtros.filtrarestadoempresa(estado_f, empresa_f, plazas, adapterPlaza);
        }
        //Filtrar TIPO Y NUMERO
        else if(listener != "" && estado_f == "Todos" && empresa_f == "Todas" && tipo_f != "Todos"){
            filtros.filtrartiponumero(listener, tipo_f, plazas, adapterPlaza);
        }
        //Filtrar TIPO Y ESTADO
        else if(listener == "" && estado_f != "Todos" && empresa_f == "Todas" && tipo_f != "Todos"){
            filtros.filtrartipoestado(tipo_f, estado_f, plazas, adapterPlaza);
        }
        //FILTRAR TIPO Y EMPRESA
        else if(listener == "" && estado_f == "Todos" && empresa_f != "Todas" && tipo_f != "Todos"){
            filtros.filtrartipoempresa(tipo_f, empresa_f, plazas, adapterPlaza);
        }

        //Filtrar NUMERO TIPO Y EMPrESA
        else if(listener != "" && estado_f == "Todos" && empresa_f != "Todas" && tipo_f != "Todos"){
            filtros.filtrarnumerotipoempresa(listener, tipo_f, empresa_f, plazas, adapterPlaza);
        }
        //Filtrar TIPO emPresa y ESTADO
        else if(listener == "" && estado_f != "Todos" && empresa_f != "Todas" && tipo_f != "Todos"){
            filtros.filtrartipoempresaestado(tipo_f, empresa_f, estado_f, plazas, adapterPlaza);
        }
        //Filtrar ESTADO tiPO Y NUMERO
        else if(listener != "" && estado_f != "Todos" && empresa_f == "Todas" && tipo_f != "Todos"){
            filtros.filtrarnumerotipoestado(listener, tipo_f, estado_f, plazas, adapterPlaza);
        }
        //Filtrar NUMERO, EMPRESA Y ESTADO
        else if(listener != "" && estado_f != "Todos" && empresa_f != "Todas" && tipo_f == "Todos"){
            filtros.filtrarnumeroempresaestado(listener, estado_f, empresa_f, plazas, adapterPlaza);
        }
        //filtrar ALL
        else if (listener != "" && estado_f != "Todos" && empresa_f != "Todas" && estado_f != "Todos") {
            filtros.filtrartodo(estado_f, empresa_f, tipo_f, listener, plazas, adapterPlaza);
        }

        //Doy un valor a la clase contador para contar los resultados de la búsqueda
        contador.setText("Plazas según el criterio de búsqueda: " + adapterPlaza.getItemCount());
        //Método para ordenar los datos según el número de la plaza
        ordenar();

    }

    //Ordenar plazas por numero
    private void ordenar() {
        Collections.sort(plazas, new Comparator<Plaza>() {
            @Override
            public int compare(Plaza plaza, Plaza t1) {
                //Comparamos el numero de cada plaza y lo ordenamos
                return Integer.compare(plaza.getNumero(), t1.getNumero());
            }
        });
    }


    //Enlazar objetos de firebase
    private void FirebaseInits() {

        fbAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        databaseReference = fDatabase.getReference();
    }
    //Enlazar objetos de la interfaz
    private void ViewInits() {
        SearchNumber = findViewById(R.id.main_searchnumber);
        SearchEstado = findViewById(R.id.main_searchestado);
        SearchEmpresa = findViewById(R.id.main_searchempresa);
        SearchTipo = findViewById(R.id.main_searchtipo);
        contador = findViewById(R.id.main_contador);
        bicgif = findViewById(R.id.gif);

    }

    //Construcción spinner personalizado de estados
    private void construirspinnerestados() {
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Todos");
        spinnerArray.add("Libre");
        spinnerArray.add("Alquilada");
        spinnerArray.add("Nula");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        SearchEstado.setAdapter(spinnerAdapter);
    }
    //Construcción spinner personalizado tipo
    private void construirspinnertipo(){
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Todos");
        spinnerArray.add("Techada");
        spinnerArray.add("Exterior");
        spinnerArray.add("Almacen");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        SearchTipo.setAdapter(spinnerAdapter);
    }

    //Carga de empresas de la base de datos
    private void cargarEmpresas() {
        //Creo la lista donde lo voy a guardar
        List<Empresa> empresas = new ArrayList<>();
        //Llamo al directorio de la base de datos
        databaseReference.child("Empresas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Obtengo los datos con un bucle
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String nombre = ds.child("nombre").getValue().toString();
                    Empresa emp = new Empresa();
                    emp.setNombre(nombre);
                    empresas.add(emp);
                }
                //Creo una empresa donde agrupar todas para el filtrado
                Empresa empresa1 = new Empresa();
                empresa1.setNombre("Todas");
                empresas.add(0, empresa1);
                //Le asigno el adapter a la lista de las empresas
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

    //Método para construir el menu
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
    //Método para darle funciones al menu y darle navegación a la aplicación
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_empresa:
                Intent empresa = new Intent(MainActivity.this, EmpresaActivity.class);
                startActivity(empresa);
                finish();
                break;
            case R.id.item_aparca:
                Intent aparca = new Intent(MainActivity.this, PlazaActivity.class);
                startActivity(aparca);
                finish();
                break;
            case R.id.item_edi:
                Intent edi = new Intent(MainActivity.this, EdificioActivity.class);
                startActivity(edi);
                finish();
                break;
            case R.id.item_pdf:
                checkearpermisos();
                break;
            case R.id.item_des:
                AlertDisconect();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkearpermisos(){
        //Si el SO es marshmallow o superior, manejamos el permiso
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                //Permiso denegado, solicitamos
                String[] permisos = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //mostramos pop-up para otorgar el permiso
                requestPermissions(permisos, PERMISO_STORAGE);
            }else{
                //Permiso otorgado, comenzamos descarga
                descarga.descargdoc(MainActivity.this);
            }
        }
        else {
            //Si el SO es menor que marshmallow, llevamos a cabo la descarga
            descarga.descargdoc(MainActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISO_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permiso otorgado desde el pop-up
                    descarga.descargdoc(MainActivity.this);
                }
                else {
                    //Permiso denegado desde el pop-up
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /*private void descarga(){
        //Url del archivo
        String url = "https://firebasestorage.googleapis.com/v0/b/biceuropark-2a103.appspot.com/o/PDF%2Fsolicitud.pdf?alt=media&token=c1c6908c-dc2e-4e25-99c2-8ab22704af40";
        //Creamos una petición de descarga
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //Permitimos los tipos de conexión para descargar archivos
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        //Titulo de la notificación de descarga
        request.setTitle(""+System.currentTimeMillis()+".pdf");
        //Descripción de la notificación de descarga
        request.setDescription("Descargando archivo...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, ""+System.currentTimeMillis()+".pdf");//Guardado del archivo en carpeta descargas y nombre con el tiempo del dispositivo
        //Obtenemos el servicio de descarga y el archivo en cola
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }*/

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