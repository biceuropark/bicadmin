package com.example.bicpark.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.bicpark.R;
import com.example.bicpark.Servicios.Descarga;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class EdificioActivity extends AppCompatActivity {

    private FirebaseAuth fbAuth;

    private CardView parking, planta0, planta1, planta2;
    private Descarga descarga;
    private static final int PERMISO_STORAGE = 356;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edificio);

        fbAuth = FirebaseAuth.getInstance();

        parking = findViewById(R.id.edi_parking);
        planta0 = findViewById(R.id.edi_planta0);
        planta1 = findViewById(R.id.edi_planta1);
        planta2 = findViewById(R.id.edi_planta2);

        descarga = new Descarga();

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
                enviardatos("Planta 0");
                finish();
            }
        });
        planta1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviardatos("Planta 1");
                finish();
            }
        });
        planta2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviardatos("Planta 2");
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
            case R.id.item_pdf:
                checkearpermisos();
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
                descarga.descargdoc(EdificioActivity.this);
            }
        }
        else {
            //Si el SO es menor que marshmallow, llevamos a cabo la descarga
            descarga.descargdoc(EdificioActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISO_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permiso otorgado desde el pop-up
                    descarga.descargdoc(EdificioActivity.this);
                }
                else {
                    //Permiso denegado desde el pop-up
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
                }
            }
        }
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