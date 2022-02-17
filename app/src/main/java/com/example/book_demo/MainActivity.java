package com.example.book_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    TextView tv_title,tv_message;


    private final int DURACION_SPLASH =2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_message = findViewById(R.id.tv_message);
        tv_title = findViewById(R.id.tv_title);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
        thread.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Si un usuario ya esta registrado no vuelve a iniciar sesión
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //Si el usuario ha o no iniciado sesión
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
                if (user != null && account != null ){
                    Intent volver = new Intent(MainActivity.this, Inicio.class);
                    startActivity(volver);
                    finish();
                } else {//Si no se ha registrado o no ha iniciado sesión
                    Intent intent = new Intent(MainActivity.this,Login.class);
                    //Arreglo para las transiciones de los objetos del login referenciando ambos
                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(tv_title,"LogoImageTrans");
                    pairs[1] = new Pair<View, String>(tv_message,"TextUsuario1");

                    //Verificar si es una versión optima para la transición
                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                        startActivity(intent, options.toBundle());

                    }else {
                        startActivity(intent);
                        finish();
                    }

                }
            }
        },DURACION_SPLASH);
        //Clase para instanciar una imagen GIF

    }


}