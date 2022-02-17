package com.example.book_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;

public class Categorias extends AppCompatActivity {

    EditText etCategory;
    Button btnEnviar;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        etCategory = findViewById(R.id.etCategory);
        btnEnviar = findViewById(R.id.btnEnviar);

        //init Firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //configure progressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere");
        progressDialog.setCanceledOnTouchOutside(false);

        //Click upload category
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }
    private String category ="";

    private void validateData() {
        //Before adding validate data
        //get data
        category = etCategory.getText().toString().trim();
        //validate if not empty
        if (TextUtils.isEmpty(category)){
            Toast.makeText(this,"Favor de agregar una categoria",Toast.LENGTH_LONG).show();
        }
        else {
            addCategoryFireBase();
        }
    }

    private void addCategoryFireBase() {
        //Show Progress
        progressDialog.setMessage("Agregando Categoria..");
        progressDialog.show();

        //get timestamp
        long timestamp = System.currentTimeMillis();

        //Setup info to add in farebase DB
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id",""+timestamp);
        hashMap.put("category",""+category);
        hashMap.put("timestamp",timestamp);
        hashMap.put("vid","" + firebaseAuth.getUid());

        //add to firebase DB... Database Root > Categories >categoryId > category info
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.child("" + timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //category and succes
                        progressDialog.dismiss();
                        Toast.makeText(Categorias.this,"Categoria agregada Satisfactoriamente",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //category and faiked
                        progressDialog.dismiss();
                        Toast.makeText(Categorias.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }
}