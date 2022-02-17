package com.example.book_demo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import android.os.Bundle;
import android.widget.Adapter;

import com.example.book_demo.Adapter.AdapterViewPager;
import com.example.book_demo.Model.ModelViewPager;

import java.util.ArrayList;

public class ViewPager extends AppCompatActivity {

    //Actionbar
    private ActionBar actionBar;

    //UI Views
    private androidx.viewpager.widget.ViewPager viewPager;

    private ArrayList<ModelViewPager> modelArrayList;
    private Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        //init actionbar
        actionBar = getSupportActionBar();
        //init UI Views
        viewPager = findViewById(R.id.viewPager);
        loadCards();




    }

    private void loadCards(){
        //Init list
        modelArrayList = new ArrayList<>();

        //Add items to list
        modelArrayList.add(new ModelViewPager("La Bella y la Bestia",
                "Había una vez un mercader muy rico que tenía seis hijos, tres varones y tres mujeres.",
                "Jeanne-Marie Leprince de Beaumont",
                R.drawable.img));
        modelArrayList.add(new ModelViewPager("El patito feo",
                "Al fin los huevos se abrieron uno tras otro. «¡Pip, pip!», decían los patitos conforme iban asomando sus cabezas a través delcascarón.",
                "Hans Christian Andersen",
                R.drawable.img_1));
        modelArrayList.add(new ModelViewPager("Hansel y Gretel",
                "Junto a un bosque muy grande vivía un pobre leñador con su mujer y dos hijos; el niño se llamaba Hänsel, y la niña, Gretel.",
                "Los Hermanos Grimm",
                R.drawable.img_2));
        modelArrayList.add(new ModelViewPager("Caperucita Roja",
                "Aquí vemos que la adolescencia, en especial las señoritas, bien hechas, amables y bonitas no deben a cualquiera oír con complacencia, y no resulta causa de extrañeza ver que muchas del lobo son la presa.",
                "Charles Perrault",
                R.drawable.img_3));

        //Setup adapter
        adapter = (Adapter) new AdapterViewPager(this,modelArrayList);
        //Set adapter to view pager
        viewPager.setAdapter((PagerAdapter) adapter);
        //Set defaultpadding from left/right
        viewPager.setPadding(100,0,100,0);

    }



}