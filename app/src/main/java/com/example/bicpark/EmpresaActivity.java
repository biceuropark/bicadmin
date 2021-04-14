package com.example.bicpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.bicpark.model.Empresa;
import com.example.bicpark.recycler.AdapterEmpresa;
import com.example.bicpark.recycler.OnEmpresaClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth fbAuth;
    private FirebaseDatabase fDatabase;
    private DatabaseReference empresasDatabase;

    private TextInputEditText editempresa;
    private TextInputLayout ly_editempresa;
    private RecyclerView recyclerView;
    private AdapterEmpresa adapterEmpresa;
    private List<Empresa> empresaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        fbAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        empresasDatabase = fDatabase.getReference();
        editempresa = findViewById(R.id.emp_nombre);
        ly_editempresa = findViewById(R.id.emp_lynombre);


        recyclerView = findViewById(R.id.emp_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        empresaList = new ArrayList<>();
        datos();

        adapterEmpresa = new AdapterEmpresa(empresaList, new OnEmpresaClickListener() {
            @Override
            public void onClick(Empresa emp) {
                MaterialAlertDialogBuilder Dialog = new MaterialAlertDialogBuilder(EmpresaActivity.this);
                Dialog.setTitle("Borrar empresa");
                Dialog.setMessage("¿Quieres borrar la empresa "+emp.getNombre()+"?");
                Dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        empresaList.remove(emp);
                        empresasDatabase.child("Empresas").child(emp.getUid()).removeValue();
                        adapterEmpresa.notifyDataSetChanged();
                    }
                });
                Dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
            }});
        recyclerView.setAdapter(adapterEmpresa);

        ly_editempresa.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = editempresa.getText().toString().trim();
                String uid = UUID.randomUUID().toString();
                if(nombre.isEmpty()){
                    Snackbar.make(view, "Escribe el nombre de la empresa", Snackbar.LENGTH_LONG).show();
                    return;
                }
                Empresa m = new Empresa();
                m.setNombre(nombre);
                m.setUid(uid);
                MaterialAlertDialogBuilder Dialog = new MaterialAlertDialogBuilder(EmpresaActivity.this);
                Dialog.setTitle("Crear Empresa");
                Dialog.setMessage("¿Quieres crear la empresa "+m.getNombre()+"?");
                Dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        empresaList.add(m);
                        empresasDatabase.child("Empresas").child(m.getUid()).setValue(m);
                        adapterEmpresa.notifyDataSetChanged();
                        Toast.makeText(EmpresaActivity.this, "Empresa "+m.getNombre()+" creada", Toast.LENGTH_LONG).show();
                        limpiar();
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

    }


    private void datos() {
        empresasDatabase.child("Empresas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresaList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Empresa empresa = ds.getValue(Empresa.class);
                    empresaList.add(empresa);
                }
                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void limpiar(){
        editempresa.setText("");
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}