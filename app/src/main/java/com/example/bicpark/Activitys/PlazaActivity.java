package com.example.bicpark.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.bicpark.R;
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
import java.util.UUID;

public class PlazaActivity extends AppCompatActivity {

    private FirebaseAuth fbAuth;
    private FirebaseDatabase fDatabase;
    private DatabaseReference databaseReference;

    private TextInputEditText edit_persona, edit_numero;
    private Spinner sp_estado, sp_tipo, sp_empresa;
    private Button btn_save, btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plaza);
        getSupportActionBar().hide();

        fbAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        databaseReference = fDatabase.getReference();

        edit_numero = findViewById(R.id.pl_numero);
        edit_persona = findViewById(R.id.pl_persona);
        sp_estado = findViewById(R.id.pl_estado);
        sp_tipo = findViewById(R.id.pl_tipo);
        sp_empresa = findViewById(R.id.pl_empresa);
        btn_save = findViewById(R.id.pl_save);
        btn_cancel = findViewById(R.id.pl_cancel);
        cargarEmpresas();



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String numero = edit_numero.getText().toString().trim();
                String persona = edit_persona.getText().toString().trim();
                String estado = sp_estado.getSelectedItem().toString().trim();
                String tipo = sp_tipo.getSelectedItem().toString().trim();
                String empresa = sp_empresa.getSelectedItem().toString().trim();
                String uid = UUID.randomUUID().toString();

                if (numero.isEmpty()) {
                    Snackbar.make(view, "Completa los campos", Snackbar.LENGTH_LONG).show();
                    return;
                }

                Plaza plaza = new Plaza();
                plaza.setNumero(Integer.parseInt(numero));
                if (!persona.isEmpty()) {
                    plaza.setPersona(persona);
                }
                plaza.setEstado(estado);
                plaza.setTipo(tipo);
                plaza.setEmpresa(empresa);
                plaza.setUid(uid);

                MaterialAlertDialogBuilder Dialog = new MaterialAlertDialogBuilder(PlazaActivity.this);
                Dialog.setTitle("Plazas");
                Dialog.setMessage("¿Estas seguro que quieres crear la plaza " + edit_numero.getText().toString() + "?");
                Dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseReference.child("Plazas").child(plaza.getUid()).setValue(plaza);
                    }
                });
                Dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
                }

        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlazaActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void cargarEmpresas(){
        List<Empresa> empresas = new ArrayList<>();
        databaseReference.child("Empresas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    String nombre = ds.child("nombre").getValue().toString();
                    Empresa emp = new Empresa();
                    emp.setNombre(nombre);
                    empresas.add(emp);
                }
                ArrayAdapter<Empresa> arrayAdapter = new ArrayAdapter<>(PlazaActivity.this, R.layout.support_simple_spinner_dropdown_item, empresas);
                sp_empresa.setAdapter(arrayAdapter);
                if (sp_empresa.getSelectedItem() == null) {
                    edit_numero.setEnabled(false);
                    edit_persona.setEnabled(false);
                    sp_estado.setEnabled(false);
                    sp_empresa.setEnabled(false);
                    sp_tipo.setEnabled(false);
                    btn_save.setEnabled(false);
                }else{
                    edit_numero.setEnabled(true);
                    edit_persona.setEnabled(true);
                    sp_estado.setEnabled(true);
                    sp_empresa.setEnabled(true);
                    sp_tipo.setEnabled(true);
                    btn_save.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}