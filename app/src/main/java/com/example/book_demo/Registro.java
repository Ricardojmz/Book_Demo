package com.example.book_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {
    //Creamos variables para los objetos
    TextView registro, regresar, logo;
    TextInputLayout campo_nombre, campo_Apaterno, campo_email, campo_password, campo_ConfPass;
    MaterialButton registrar_usu;
    TextInputEditText EmailEditText, PasswordEditText, ConfPassEditText, Campo_Nombre, Campo_Apaterno;
    //Crear una animacion de carga para ver los datos si en realidad hace algo//
    ProgressDialog progressDialog;

    //Crear un objeto para Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        //Instanciamos los objetos con la vista
        registro = (TextView) findViewById(R.id.registro);
        logo = (TextView) findViewById(R.id.logo);
        campo_nombre = (TextInputLayout) findViewById(R.id.campo_nombre);
        campo_Apaterno = (TextInputLayout) findViewById(R.id.campo_Apaterno);
        campo_email = (TextInputLayout) findViewById(R.id.campo_email);
        campo_password = (TextInputLayout) findViewById(R.id.campo_password);
        campo_ConfPass = (TextInputLayout) findViewById(R.id.campo_ConfPass);
        regresar = (TextView) findViewById(R.id.regresar);
        registrar_usu = (MaterialButton) findViewById(R.id.registrar_usu);
        Campo_Nombre = (TextInputEditText) findViewById(R.id.Campo_Nombre);
        Campo_Apaterno = (TextInputEditText) findViewById(R.id.Campo_Apaterno);
        EmailEditText = (TextInputEditText) findViewById(R.id.EmailEditText);
        PasswordEditText = (TextInputEditText) findViewById(R.id.PasswordEdittext);
        ConfPassEditText = (TextInputEditText) findViewById(R.id.ConfPassEditText);
        //Acción que realizara el boton
        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        //Acción que realizara el boton
        registrar_usu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Método para regresar a Login
                //Método para validar datos
                validar();
            }
        });
        //Clase para llamar los métodos y llamar las credenciales
        mAuth = FirebaseAuth.getInstance();

    }
    public void validar(){
        String campo_nombre = Campo_Nombre.getText().toString().trim();
        String campo_Apaterno = Campo_Apaterno.getText().toString().trim();
        String email = EmailEditText.getText().toString().trim();
        String password = PasswordEditText.getText().toString().trim();
        String confpassword = ConfPassEditText.getText().toString().trim();
        //String email = EmailEditText.getText().toString().trim();
        //Validar los campos
        if (campo_nombre.isEmpty() || campo_nombre.length()>20){
            Campo_Nombre.setError("Introduce tu Nombre (s)");
            return;
        } else {
            Campo_Nombre.setError(null);
        }
        if (campo_Apaterno.isEmpty() || campo_Apaterno.length()>15){
            Campo_Apaterno.setError("Introduce tu Apellido Paterno");
            return;
        } else {
            Campo_Apaterno.setError(null);
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            EmailEditText.setError("Correo Invalido");
            return;
        } else {
            EmailEditText.setError(null);
        }
        if (password.isEmpty() || password.length() <8){
            PasswordEditText.setError("Tu contraseña debe ser de 8 caracteres");
            return;
        } else if (!Pattern.compile("^[a-zA-Z 0-9 #.,áéíóú]+$").matcher(password).find()){
            PasswordEditText.setError("Al menos un número debe contener");

            return;
        }  else { PasswordEditText.setError(null);

        }
        if (!confpassword.equals(password)){
            ConfPassEditText.setError("La contraseña no coincide");
            return;
        }else {
            registrar(email,password);
        }

    }
    //Crear un Método para registrar los datos en Firebase
    public void registrar(String email, String password){
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    //Si los datos son correctos
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent menu = new Intent(Registro.this,Inicio.class);
                            startActivity(menu);
                            finish();
                        }else{//En caso de algun error o no se haya creado el usuario
                            Toast.makeText(Registro.this,"Error al registrarse",Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    //Regresar a la pantalla anterior despues de registrarse con exito//
    public void login(){

        Intent login = new Intent(Registro.this,Login.class);
        //Arreglo para las transiciones de los objetos del login referenciando ambos
        Pair[] pairs = new Pair[9];
        pairs [0] = new Pair<View, String>(logo, "LogoImageTrans");
        pairs [1] = new Pair<View, String>(registro, "TextUsuario");
        pairs [2] = new Pair<View, String>(campo_nombre, "TextUsuario1");
        pairs [3] = new Pair<View, String>(campo_Apaterno, "TextName");
        pairs [4] = new Pair<View, String>(campo_email, "EmailInputTextTrans");
        pairs [5] = new Pair<View, String>(campo_password, "PaswordInputTextTrans");
        pairs [6] = new Pair<View, String>(campo_ConfPass, "NewUserTrans1");
        pairs [7] = new Pair<View, String>(registrar_usu, "ButtonSingInTrans");
        pairs [8] = new Pair<View, String>(regresar, "NewUserTrans");

        //Verificar si es una versión optima para la transición
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Registro.this, pairs);
        startActivity(login, options.toBundle());

    }

}