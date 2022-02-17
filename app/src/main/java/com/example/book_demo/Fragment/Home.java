package com.example.book_demo.Fragment;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.book_demo.Adapter.AdapterViewPager;
import com.example.book_demo.Model.ModelPdf;
import com.example.book_demo.Model.ModelViewPager;
import com.example.book_demo.R;

import java.util.ArrayList;


public class Home extends Fragment  {
    View vista;
    //Actionbar
    private ActionBar actionBar;

    //UI Views
    private ViewPager viewPager;

    private ArrayList<ModelViewPager> modelArrayList;
    private AdapterViewPager adapter;

    public Home() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_home, container, false);
        //init actionbar
        //actionBar = getSupportActionBar();
        //init UI Views
        viewPager = vista.findViewById(R.id.viewPager);
        loadCards();

        return vista;
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
        adapter = new AdapterViewPager(getContext(),modelArrayList);
        //Set adapter to view pager
        viewPager.setAdapter(adapter);
        //Set defaultpadding from left/right
        viewPager.setPadding(100,0,100,0);

    }


}


