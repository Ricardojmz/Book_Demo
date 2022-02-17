package com.example.book_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.example.book_demo.Adapter.AdapterCategory;
import com.example.book_demo.Model.ModelCategory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    EditText EtSearch;
    RecyclerView RvCategories;
    FloatingActionButton addPDF;
    //array to store category
    private ArrayList<ModelCategory> categoryArrayList;
    //Adapter
    private AdapterCategory adapterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        RvCategories = findViewById(R.id.RvCategories);
        EtSearch = findViewById(R.id.EtSearch);
        //addPDF = findViewById(R.id.addPDF);

        loadCategories();
        //Search
        EtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Called as and when user type each letter
                try {
                    adapterCategory.getFilter().filter(s);
                }
                catch (Exception e){

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //handle clic, star PDF add screen
        /*addPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, Cargar_PDF.class));
            }
        });*/
    }

    private void loadCategories() {
        //init arraylist
        categoryArrayList = new ArrayList<>();

        //get categories from firebase < categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear arraylist before adding data into it
                categoryArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    ModelCategory model = ds.getValue(ModelCategory.class);

                    //add array list
                    categoryArrayList.add(model);
                }
                //setup adapter
                adapterCategory = new AdapterCategory(Dashboard.this, categoryArrayList);
                //set Adapter to recyclerview
                RvCategories.setAdapter(adapterCategory);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}