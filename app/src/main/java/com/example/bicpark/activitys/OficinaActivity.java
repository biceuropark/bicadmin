package com.example.bicpark.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.bicpark.R;
import com.example.bicpark.model.Empresa;
import com.example.bicpark.model.Oficina;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class OficinaActivity extends AppCompatActivity {

    private TextInputEditText ofinumero;
    private Spinner ofiestado, ofiiden;
    private SearchableSpinner ofiempresa;
    private Button ofiguardar, oficancelar, ofiborrar;
    private FirebaseDatabase fDatabase;
    private DatabaseReference databaseReference;

    private String key;
    private Oficina takeofi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oficina);

        fDatabase = FirebaseDatabase.getInstance();
        databaseReference = fDatabase.getReference();
        obtenerintencion();
        cargarEmpresas();

        ofinumero = findViewById(R.id.ofi_numero);
        ofiestado = findViewById(R.id.ofi_estado);
        ofiempresa = findViewById(R.id.ofi_empresa);
        ofiguardar = findViewById(R.id.ofi_save);
        oficancelar = findViewById(R.id.ofi_cancel);
        ofiborrar = findViewById(R.id.ofi_borrar);
        ofiiden = findViewById(R.id.ofi_iden);

        if (takeofi == null) {
            ofiborrar.setEnabled(false);
        } else if (takeofi != null) {
            ofinumero.setText(String.valueOf(takeofi.getNumero()));
            ofiestado.setSelection(obtenerPosicionItem(ofiestado, takeofi.getEstado()));
            ofiempresa.setSelection(obtenerPosicionItem(ofiempresa, takeofi.getEmpresa()));
            ofiborrar.setEnabled(true);
        }

        ofiborrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertaBorrar(view);
            }
        });


        ofiguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardaroficina(view);
            }
        });

        oficancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarkey();
                finish();
            }
        });
    }

    private void guardaroficina(View view) {
        String num = ofinumero.getText().toString().trim();
        String emp = ofiempresa.getSelectedItem().toString();
        String estado = ofiestado.getSelectedItem().toString();
        String identificador = ofiiden.getSelectedItem().toString();
        if (num.isEmpty()) {
            Snackbar.make(view, "Rellena los campos", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (takeofi == null) {
            Oficina oficina = new Oficina();
            oficina.setUid(UUID.randomUUID().toString());
            oficina.setEmpresa(emp);
            oficina.setNumero(Integer.parseInt(num));
            oficina.setEstado(estado);
            oficina.setIdentificador(identificador);
            databaseReference.child("Edificio").child(key).child(oficina.getUid()).setValue(oficina);
        } else if (takeofi != null) {
            Oficina oficina = new Oficina();
            oficina.setUid(takeofi.getUid());
            oficina.setEmpresa(emp);
            oficina.setNumero(Integer.parseInt(num));
            oficina.setEstado(estado);
            oficina.setIdentificador(identificador);
            databaseReference.child("Edificio").child(key).child(oficina.getUid()).setValue(oficina);

        }
        Snackbar.make(view, "La oficina ha sido creada", Snackbar.LENGTH_LONG).show();
        takeofi = null;
        limpiaredit();
    }

    @Override
    public void onBackPressed() {
        takeofi = null;
        enviarkey();
        finish();
    }

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

                //Le asigno el adapter a la lista de las oficinas
                ArrayAdapter<Empresa> arrayAdapter = new ArrayAdapter<>(OficinaActivity.this, android.R.layout.simple_spinner_item, empresas);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Empresa empresa1 = new Empresa();
                empresa1.setNombre("Vacío");
                empresas.add(0, empresa1);
                ofiempresa.setAdapter(arrayAdapter);
                ofiempresa.setTitle("Empresas");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void obtenerintencion() {
        Intent getkey = getIntent();
        Bundle bundlekey = getkey.getExtras();
        key = bundlekey.getString("key");

        Intent getofi = getIntent();
        Bundle bundleofi = getofi.getExtras();
        takeofi = (Oficina) bundleofi.getSerializable("exofi");
    }

    private void enviarkey() {
        Intent intent = new Intent(OficinaActivity.this, PlantaActivity.class);
        intent.putExtra("key", key);
        setResult(RESULT_OK, intent);
        startActivity(intent);
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

    private void limpiaredit() {
        ofinumero.setText("");
    }


    public void AlertaBorrar(View view) {
        MaterialAlertDialogBuilder Dialog = new MaterialAlertDialogBuilder(OficinaActivity.this);
        Dialog.setTitle("Borrar oficina");
        Dialog.setMessage("¿Quieres borrar la oficina?");
        Dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.child("Edificio").child(key).child(takeofi.getUid()).removeValue();
                limpiaredit();
                Snackbar.make(view, "La oficina ha sido borrada", Snackbar.LENGTH_LONG).show();
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