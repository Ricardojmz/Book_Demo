package com.example.book_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.book_demo.Adapter.AdapterPDF;
import com.example.book_demo.Model.ModelPdf;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PdfList extends AppCompatActivity {
    EditText EtSearchBook;
    TextView TVBooks, TvSubBook;
    ImageButton Back;
    RecyclerView Recycle;

    private ArrayList<ModelPdf> pdfArrayList;

    private AdapterPDF adapterPDF;

    private String categoryId, categoryTitle;



    private  static final String TAG= "PDF_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_list);
        EtSearchBook = findViewById(R.id.EtSearchBook);
        TVBooks = findViewById(R.id.TVBooks);
        TvSubBook = findViewById(R.id.TvSubBook);
        Back = findViewById(R.id.Back);
        Recycle = findViewById(R.id.Recycle);


        //get data from inte
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        categoryTitle= intent.getStringExtra("categoryTitle");

       //set pdf category
        TvSubBook.setText(categoryTitle);


        loadPdfList();

        //search
        EtSearchBook.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //search as and when user type any letter
                try {
                    adapterPDF.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d(TAG,"onTextChanged: "+ e.getMessage());

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //andhle click go to

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void loadPdfList() {
        pdfArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Bokks");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            //get data
                            ModelPdf model = ds.getValue(ModelPdf.class);
                            //add to list
                            pdfArrayList.add(model);

                            Log.d(TAG,"onDateChange: "+model.getId()+" "+model.getTitle());
                        }
                        //setup adapter
                        adapterPDF = new AdapterPDF(PdfList.this,pdfArrayList);
                        Recycle.setAdapter(adapterPDF);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}