package com.example.bicpark.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.bicpark.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth fbAuth;

    private TextInputEditText editusu, editpass;
    private MaterialButton btn_login, btn_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Ocultar ActionBar
        getSupportActionBar().hide();
        //Inits
        ViewInits();

        editusu.setText("admin@bic.es");
        editpass.setText("informatica2021");

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usuario = editusu.getText().toString().trim();
                String pass = editpass.getText().toString().trim();

                //Comprobamos si los campos estan vacíos
                if(usuario.isEmpty() || pass.isEmpty()){
                    Snackbar.make(view, "Rellena los campos", Snackbar.LENGTH_LONG).show();
                    return;
                }
                //Método Login firebase, le paso los datos de los edittext
                fbAuth.signInWithEmailAndPassword(usuario, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Si la respuesta es exitosa
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            //Si la respuesta es fallida
                            Snackbar.make(view, "Error en el acceso, revisa los campos", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        //Salir de la app
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void ViewInits(){
        //Firebase Authentication instancia
        fbAuth = FirebaseAuth.getInstance();
        //Vistas
        editusu = findViewById(R.id.lg_usu);
        editpass = findViewById(R.id.lg_pass);
        btn_login = findViewById(R.id.lg_login);
        btn_exit = findViewById(R.id.lg_salir);
    }
}