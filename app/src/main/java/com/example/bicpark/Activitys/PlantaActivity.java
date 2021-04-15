package com.example.bicpark.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bicpark.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

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
        cargarimagen();
        comprobacion();

        imagenplanta = findViewById(R.id.plant_image);
        photoViewAttacher = new PhotoViewAttacher(imagenplanta);
        planta = findViewById(R.id.plant_nombre);
        planta.setText(key);


    }

    private void comprobacion(){
        if(key == null){
            Toast.makeText(getApplicationContext(), "No puedes acceder a esta pagina", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.planta_menu, menu);
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menupl_camara :
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
                break;
            case R.id.menupl_volver:
                Intent volver = new Intent(PlantaActivity.this, EdificioActivity.class);
                startActivity(volver);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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