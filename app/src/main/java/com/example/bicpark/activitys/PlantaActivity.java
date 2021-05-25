package com.example.bicpark.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bicpark.R;
import com.example.bicpark.model.Oficina;
import com.example.bicpark.recycler.AdapterEdificio;
import com.example.bicpark.recycler.OnOficicinaClickListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class PlantaActivity extends AppCompatActivity {

    private FirebaseAuth fbAuth;
    private FirebaseDatabase fDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage fStorage;
    private StorageReference storageReference;

    private String key;
    private ImageView imagenplanta;
    private PhotoViewAttacher photoViewAttacher;
    private TextView planta;

    private RecyclerView recyclerView;
    private AdapterEdificio adapter;
    private List<Oficina> oficinas;


    private final int PICK_IMAGE = 256;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planta);

        fDatabase = FirebaseDatabase.getInstance();
        databaseReference = fDatabase.getReference();
        fStorage = FirebaseStorage.getInstance();
        storageReference = fStorage.getReference();
        fbAuth = FirebaseAuth.getInstance();


        Intent getkey = getIntent();
        Bundle bundlekey = getkey.getExtras();
        key = bundlekey.getString("key");


        imagenplanta = findViewById(R.id.plant_image);
        Glide.with(getApplicationContext()).load(R.drawable.gifdefinitivo).into(imagenplanta);
        photoViewAttacher = new PhotoViewAttacher(imagenplanta);
        planta = findViewById(R.id.plant_nombre);
        planta.setText(key);



        recyclerView = findViewById(R.id.plant_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        oficinas = new ArrayList<>();

        adapter = new AdapterEdificio(oficinas, new OnOficicinaClickListener() {
            @Override
            public void onClick(Oficina ofi) {
                Intent intent = new Intent(PlantaActivity.this, OficinaActivity.class);
                intent.putExtra("exofi", ofi);
                intent.putExtra("key", key);
                setResult(RESULT_OK, intent);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        cargarimagen();
        comprobacion();
        cargardb();


    }

    private void cargardb(){
        databaseReference.child("Edificio").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                oficinas.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Oficina ofi = ds.getValue(Oficina.class);
                    oficinas.add(ofi);
                }
                ordenar(oficinas);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static void ordenar(List<Oficina> persons) {

        Collections.sort(persons, new Comparator() {

            public int compare(Object o1, Object o2) {

                String x1 = ((Oficina) o1).getIdentificador();
                String x2 = ((Oficina) o2).getIdentificador();
                int sComp = x1.compareTo(x2);

                if (sComp != 0) {
                    return sComp;
                }

                Integer x3 = ((Oficina) o1).getNumero();
                Integer x4 = ((Oficina) o2).getNumero();
                return x3.compareTo(x4);
            }});
    }

    private void comprobacion(){
        if(key == null){
            Toast.makeText(getApplicationContext(), "No puedes acceder a esta pagina", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            StorageReference filePath = storageReference.child("Plantas").child(key);

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(PlantaActivity.this, "Subido con éxito, cargando...", Toast.LENGTH_LONG).show();
                    cargarimagen();
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void enviarkey(){
        Intent oficina = new Intent(PlantaActivity.this, OficinaActivity.class);
        oficina.putExtra("key", key);
        setResult(RESULT_OK, oficina);
        startActivity(oficina);
    }

    private void cargarimagen(){
        storageReference.child("Plantas").child(key).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imagenplanta);
                photoViewAttacher.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PlantaActivity.this, EdificioActivity.class);
        startActivity(intent);
        finish();
    }
}