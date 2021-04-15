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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.bicpark.R;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth fbAuth;
    private FirebaseDatabase fDatabase;
    private DatabaseReference databaseReference;

    private EditText SearchNumber;

    private RecyclerView recyclerView;
    private AdapterPlaza adapterPlaza;
    private List<Plaza> plazas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fbAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        databaseReference = fDatabase.getReference();

        SearchNumber = findViewById(R.id.main_searchnumber);

        recyclerView = findViewById(R.id.main_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        plazas = new ArrayList<>();
        cargardatos();

        SearchNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                filtrarnumero(editable.toString());
            }
        });



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

    private void cargardatos(){
        databaseReference.child("Plazas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                plazas.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
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



    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_empresa :
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

    @Override
    public void onBackPressed() {
        AlertDisconect();
    }

    public void AlertDisconect(){
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

    private void filtrarnumero(String numero){
        ArrayList<Plaza> plazaArrayList = new ArrayList<>();
        for(Plaza pl : plazas){
            if (String.valueOf(pl.getNumero()).contains(numero)){
                plazaArrayList.add(pl);
            }
        }
        adapterPlaza.filtrar(plazaArrayList);
    }
}