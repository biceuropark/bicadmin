package com.example.bicpark.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.bicpark.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class EdificioActivity extends AppCompatActivity {

    private FirebaseAuth fbAuth;

    private CardView parking, planta0, planta1, planta2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edificio);

        fbAuth = FirebaseAuth.getInstance();

        parking = findViewById(R.id.edi_parking);
        planta0 = findViewById(R.id.edi_planta0);
        planta1 = findViewById(R.id.edi_planta1);
        planta2 = findViewById(R.id.edi_planta2);

        parking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviardatos("Parking");
                finish();
            }
        });
        planta0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviardatos("Oficina 0");
                finish();
            }
        });
        planta1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviardatos("Oficina 1");
                finish();
            }
        });
        planta2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviardatos("Oficina 2");
                finish();
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
                Intent empresa = new Intent(EdificioActivity.this, EmpresaActivity.class);
                startActivity(empresa);
                finish();
                break;
            case R.id.item_aparca:
                Intent aparca = new Intent(EdificioActivity.this, PlazaActivity.class);
                startActivity(aparca);
                finish();
                break;
            case R.id.item_edi:
                Toast.makeText(EdificioActivity.this, "No puedes ir al lugar donde te encuentras", Toast.LENGTH_LONG).show();
                break;
            case R.id.item_des:
                AlertDisconect();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void AlertDisconect(){
        MaterialAlertDialogBuilder Dialog = new MaterialAlertDialogBuilder(EdificioActivity.this);
        Dialog.setTitle("Desconectando...");
        Dialog.setMessage("¿Quieres salir de la app?");
        Dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fbAuth.signOut();
                Intent logout = new Intent(EdificioActivity.this, LoginActivity.class);
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

    private void enviardatos(String key){
        Intent intent = new Intent(EdificioActivity.this, PlantaActivity.class);
        intent.putExtra("key", key);
        setResult(RESULT_OK, intent);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EdificioActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}