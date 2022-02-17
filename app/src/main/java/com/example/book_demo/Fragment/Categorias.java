package com.example.book_demo.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.book_demo.Adapter.AdapterCategory;
import com.example.book_demo.Dashboard;
import com.example.book_demo.Model.ModelCategory;
import com.example.book_demo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Categorias extends Fragment {
    View vista;
    EditText EtSearch;
    RecyclerView RvCategories;
    FloatingActionButton addPDF;
    //array to store category
    private ArrayList<ModelCategory> categoryArrayList;
    //Adapter
    private AdapterCategory adapterCategory;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista=inflater.inflate(R.layout.fragment_categorias, container, false);
        RvCategories = vista.findViewById(R.id.RvCategories);
        EtSearch = vista.findViewById(R.id.EtSearch);
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


        return vista;
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
                adapterCategory = new AdapterCategory(getContext(), categoryArrayList);
                //set Adapter to recyclerview
                RvCategories.setAdapter(adapterCategory);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}