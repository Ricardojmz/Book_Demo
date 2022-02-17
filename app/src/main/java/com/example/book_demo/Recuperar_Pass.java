package com.example.book_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Recuperar_Pass extends AppCompatActivity {
    //Creamos variables para los objetos
    TextInputEditText EmailEditText;
    MaterialButton RecuperarPass;
    ImageView cuadrado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_pass);
        //Instaciamos los objetos con la vista
        EmailEditText = (TextInputEditText) findViewById(R.id.EmailEditText);
        RecuperarPass = (MaterialButton) findViewById(R.id.RecuperarPass);
        cuadrado = (ImageView) findViewById(R.id.cuadrado);
        //Acción que realizara el bóton
        RecuperarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recuperar();//Creamos un método
            }
        });
        //Clase para instanciar una imagen GIF
        imagen();

    }
    //Creamos la clase para el método recuperar
    public void recuperar(){
        String email = EmailEditText.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            EmailEditText.setError("Correo Invalido");
            return;
        }
        sendEmail(email);
    }
    //Validar para regreso al Login
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Recuperar_Pass.this,Login.class);
        startActivity(intent);
        finish();
    }
    public void sendEmail(String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //Declarar variable
        String emailAddress = email;
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Si el correo fuen enviado
                        if (task.isSuccessful()){
                            Toast.makeText(Recuperar_Pass.this,"Correo enviado", Toast.LENGTH_SHORT).show();
                            Intent envio_correo = new Intent(Recuperar_Pass.this,Login.class);
                            startActivity(envio_correo);
                            finish();
                        }else{//En caso de correo invalido o no existe el correo
                            Toast.makeText(Recuperar_Pass.this,"Correo invalido",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    //Cargamos el GIF
    public void imagen() {
        Glide.with(this).load(R.drawable.cuadrado).into(cuadrado);
    }

}