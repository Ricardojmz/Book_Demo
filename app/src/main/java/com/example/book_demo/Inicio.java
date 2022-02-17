package com.example.book_demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.book_demo.Fragment.Categorias;
import com.example.book_demo.Fragment.Home;
import com.example.book_demo.Fragment.Video;
import com.example.book_demo.Fragment.contactanos;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Inicio extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    //Implementar icono de menú
    ActionBarDrawerToggle toggle;
    //private MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        //Método requerimientos del navigationDrawer
        navigation();
        //Clase para llamar los métodos y llamar las credenciales
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //Clase para recibir datos de usuario
        updateNavHeader();

        getSupportFragmentManager().beginTransaction().add(R.id.content, new Home()).commit();
        setTitle("HOME");

        //icono hamburguesa llamarlo
        mDrawerLayout.addDrawerListener(toggle);

        navigationView.setNavigationItemSelectedListener(this);
    }
    private ActionBarDrawerToggle setUpDrawerToggle() {
        ActionBarDrawerToggle actionBarDrawerToggle;
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, toolbar,
                R.string.open,
                R.string.close);
        return actionBarDrawerToggle;
    }





    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectItemNav(item);
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        toggle.syncState();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    private void selectItemNav(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch (item.getItemId()) {
            case R.id.inicio:
                ft.replace(R.id.content, new Home()).commit();
                break;
            case R.id.Categorias:
                ft.replace(R.id.content, new Categorias()).commit();
                break;
            case R.id.Videos:
                ft.replace(R.id.content, new Video()).commit();
                break;
            case R.id.Contactanos:
                ft.replace(R.id.content, new contactanos()).commit();
                break;
            case R.id.Comparte:
                share();
                break;
            case R.id.CerrarSesión://Cerrar sesión
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>(){
                            @Override//Cierra sesión y vuelve a preguntar con que cuenta quiere entrar
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                                Toast.makeText(Inicio.this, "Cerrando Sesión", Toast.LENGTH_SHORT).show();
                                regresoLogin();//Nos regresa al Login
                            }
                        });break;
            case R.id.Salir:
                //Se agrega un mensaje de alerta si es que quiere salir de la App
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("BOOK DEMO");
                builder.setMessage("Quieres salir de BOOK DEMO");
                //Si presiona Aceptar sale de la App
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //moveTaskToBack(true);
                        finishAffinity();
                        //android.os.Process.killProcess(android.os.Process.myPid());
                        //System.exit(7);
                    }
                });
                //Si presiona Cancelar se queda en la App
                builder.setNegativeButton("Cancelar",null);
                AlertDialog dialog = builder.create();
                dialog.show();
        }
        setTitle(item.getTitle());
        mDrawerLayout.closeDrawers();
    }

    public void share(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Check out this cool Aplication");
        intent.putExtra(Intent.EXTRA_TEXT,"Abre el enlace para compartir BOOK DEMO ");
        startActivity(Intent.createChooser(intent,"Compartir via"));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Método que direcciona al Login
    public void regresoLogin(){
        Intent cerrar = new Intent(Inicio.this, Login.class);
        cerrar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(cerrar);
        finish();
    }
    //Método para el navigationDrawer
    public void navigation(){
        // drawer es el layout del activity que contiene el Navigation Drawer
        // y los activities con los que está vinculado
        mDrawerLayout = findViewById(R.id.drawer_layout);
        // navigationView es el menú Navigation Drawer
        navigationView = findViewById(R.id.nav_view);
        //Barra de navegación
        toolbar = findViewById(R.id.toolbar);
        //Quitar el color a los iconos para hacerlos visibles
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        //Setup toolbar
        setSupportActionBar(toolbar);
        //icono de menu
        toggle = setUpDrawerToggle();

    }

    //Método para actualizar la imagen, nombre y email del usuario
    public void updateNavHeader(){

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nameView = headerView.findViewById(R.id.nameView);
        TextView emailView = headerView.findViewById(R.id.emailView);
        ImageView imgView = headerView.findViewById(R.id.imageView);
        //Mostrar los valores
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nameView.setText(currentUser.getDisplayName());
        emailView.setText(currentUser.getEmail());
        Glide.with(this).load(currentUser.getPhotoUrl()).into(imgView);
    }

}