package com.example.book_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    MaterialButton inicio_sesion;
    TextView btn_registrar, encabezado, continuar, olvidaste_pass;
    TextInputLayout Email, Password;
    ImageView librogif, cubo;
    TextInputEditText EmailEditText, PasswordEditText;

    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    public static final int SIGN_IN = 0;
    //Crear un objeto para Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        encabezado = (TextView) findViewById(R.id.encabezado);
        inicio_sesion = (MaterialButton) findViewById(R.id.inicio_sesion);
        continuar = (TextView) findViewById(R.id.continuar);
        olvidaste_pass = (TextView) findViewById(R.id.olvidaste_pass);
        btn_registrar = (TextView) findViewById(R.id.btn_registrar);
        Email = (TextInputLayout) findViewById(R.id.Email);
        librogif = (ImageView) findViewById(R.id.librogif);
        cubo = (ImageView) findViewById(R.id.cubo);
        Password = (TextInputLayout) findViewById(R.id.Password);
        EmailEditText = (TextInputEditText) findViewById(R.id.EmailEditText);
        PasswordEditText = (TextInputEditText) findViewById(R.id.PasswordEdittext);

        mAuth = FirebaseAuth.getInstance();
        //instanciar una imagen GIF
        imagen();
        //Acción que realizara el boton
        inicio_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Crear un método para validar datos ingresados
                validarlogin();
            }
        });
        //Acción que realizara el boton
        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Crear un método para registrar un usuario
                registrar_usuario();
            }
        });
        //Instanciar el objeto del botón con la vista
        signInButton = findViewById(R.id.LoginGoogle);
        //Acción que realizará el botón
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Crear un método para iniciar sesión con Google
                LoginGoogle();
            }
        });
        olvidaste_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent recuperar = new Intent(Login.this,Recuperar_Pass.class);
                startActivity(recuperar);
                finish();
            }
        });
        //Configuraciones
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        //Llamar a la variable mGoogleSignInClient
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

    }
    //Crear la clase para el método LoginGoogle
    private void LoginGoogle (){
        //Cuadro de dialogo de google
        Intent signIngoogle = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIngoogle,SIGN_IN);
    }
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            //Evaluar si hay un error
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Método para Firebase que da un token
                firebaseAuthWithGoogle(account.getIdToken());
                //En caso de algun error
            }catch (ApiException e){
                Toast.makeText(Login.this,"No hay respuesta de Google",Toast.LENGTH_SHORT).show();
            }
        }
    }
    //Crear clase para el método firebaseAuthWidthGoogle
    private void firebaseAuthWithGoogle (String idtoken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idtoken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Validar un error de autenticación
                        if (task.isSuccessful()){
                            Intent intent = new Intent(Login.this, Inicio.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(Login.this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Metodo para cargar el GIF
    public void imagen(){
        Glide.with(this).load(R.drawable.cubo).into(cubo);
        Glide.with(this).load(R.drawable.librogif).into(librogif);
    }

    public void validarlogin (){
        String email = EmailEditText.getText().toString().trim();
        String password = PasswordEditText.getText().toString().trim();
        //Validar los campos
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
        InicioSesion(email,password);
    }

    public void InicioSesion(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent iniciar_sesion = new Intent(Login.this, Inicio.class);
                            startActivity(iniciar_sesion);
                            Toast.makeText(getApplicationContext(),"Acceso Correcto",Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Datos incorrectos intenta nuevamente", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Enlazamos al Activity Registro
    public void registrar_usuario(){
        Intent registrar_u = new Intent(Login.this,Registro.class);
        //Arreglo para las transiciones de los objetos del login referenciando ambos
        Pair[] pairs = new Pair[9];
        pairs [0] = new Pair<View, String>(librogif, "LogoImageTrans");
        pairs [1] = new Pair<View, String>(encabezado, "TextUsuario");
        pairs [2] = new Pair<View, String>(continuar, "TextUsuario1");
        pairs [3] = new Pair<View, String>(Email, "EmailInputTextTrans");
        pairs [4] = new Pair<View, String>(Password, "PaswordInputTextTrans");
        pairs [5] = new Pair<View, String>(olvidaste_pass, "TextName");
        pairs [6] = new Pair<View, String>(inicio_sesion, "ButtonSingInTrans");
        pairs [7] = new Pair<View, String>(btn_registrar, "NewUserTrans1");
        pairs [8] = new Pair<View, String>(cubo, "NewUserTrans|");

        //Verificar si es una versión optima para la transición
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
            startActivity(registrar_u, options.toBundle());
        }else {
            startActivity(registrar_u);
            finish();
        }

    }

}