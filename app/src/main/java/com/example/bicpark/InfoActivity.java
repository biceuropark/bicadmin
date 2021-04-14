package com.example.bicpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bicpark.model.Empresa;
import com.example.bicpark.model.Plaza;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    private FirebaseAuth fbAuth;
    private FirebaseDatabase fDatabase;
    private DatabaseReference databaseReference;

    private TextInputEditText edit_numero, edit_persona;
    private Spinner sp_estado, sp_tipo, sp_empresa;
    private Plaza explaza;
    private Button btn_save, btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        fbAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        databaseReference = fDatabase.getReference();

        explaza = (Plaza) getIntent().getSerializableExtra("explaza");

        edit_numero = findViewById(R.id.info_numero);
        edit_persona = findViewById(R.id.info_Persona);
        sp_empresa = findViewById(R.id.info_empresa);
        sp_estado = findViewById(R.id.info_estado);
        sp_tipo = findViewById(R.id.info_tipo);
        btn_save = findViewById(R.id.info_save);
        btn_cancel = findViewById(R.id.info_cancel);

        cargardatos();
        cargarEmpresas();
        nopermito();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numero = edit_numero.getText().toString().trim();
                String persona = edit_persona.getText().toString().trim();
                String estado = sp_estado.getSelectedItem().toString().trim();
                String tipo = sp_tipo.getSelectedItem().toString().trim();
                String empresa = sp_empresa.getSelectedItem().toString().trim();
                if(numero.isEmpty()){
                    Snackbar.make(view, "Rellena los campos", Snackbar.LENGTH_LONG).show();
                    return;
                }

                explaza.setNumero(Integer.parseInt(numero));
                explaza.setEmpresa(empresa);
                explaza.setTipo(tipo);
                explaza.setEstado(estado);
                if(!persona.isEmpty()){
                    explaza.setPersona(persona);
                }
                databaseReference.child("Plazas").child(explaza.getUid()).setValue(explaza);
                Snackbar.make(view, "Plaza "+explaza.getNumero()+" editada", Snackbar.LENGTH_LONG).show();
                nopermito();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nopermito();
                alertasalir();
            }
        });



    }
    public static int obtenerPosicionItem(Spinner spinner, String string) {
        int posicion = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(string)) {
                posicion = i;
            }
        }
        return posicion;
    }

    private void nopermito(){
        edit_numero.setEnabled(false);
        edit_persona.setEnabled(false);
        sp_empresa.setEnabled(false);
        sp_tipo.setEnabled(false);
        sp_estado.setEnabled(false);
        btn_save.setEnabled(false);
    }
    private void sipermito(){
        edit_numero.setEnabled(true);
        edit_persona.setEnabled(true);
        sp_empresa.setEnabled(true);
        sp_tipo.setEnabled(true);
        sp_estado.setEnabled(true);
        btn_save.setEnabled(true);
    }

    private void cargardatos(){
        edit_numero.setText(String.valueOf(explaza.getNumero()));
        edit_persona.setText(explaza.getPersona());
        sp_estado.setSelection(obtenerPosicionItem(sp_estado, explaza.getEstado()));
        sp_tipo.setSelection(obtenerPosicionItem(sp_tipo, explaza.getTipo()));
    }

    private void alertasalir(){
        MaterialAlertDialogBuilder Dialog = new MaterialAlertDialogBuilder(InfoActivity.this);
        Dialog.setTitle("Volver atrás");
        Dialog.setMessage("¿Quieres volver atrás?");
        Dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(InfoActivity.this, MainActivity.class);
                startActivity(intent);
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

    private void cargarEmpresas(){
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
                ArrayAdapter<Empresa> arrayAdapter = new ArrayAdapter<>(InfoActivity.this, R.layout.support_simple_spinner_dropdown_item, empresas);
                sp_empresa.setAdapter(arrayAdapter);
                sp_empresa.setSelection(obtenerPosicionItem(sp_empresa, explaza.getEmpresa()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.empresa_menu, menu);
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_editar :
                sipermito();
                break;
            case R.id.item_borrar:
                databaseReference.child("Plazas").child(explaza.getUid()).removeValue();
                Toast.makeText(this, "Plaza "+explaza.getNumero()+" eliminada", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(InfoActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}