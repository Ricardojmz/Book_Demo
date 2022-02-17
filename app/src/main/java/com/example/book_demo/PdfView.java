package com.example.book_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.book_demo.databinding.ActivityPdfViewBinding;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PdfView extends AppCompatActivity {

    private ActivityPdfViewBinding binding;
    private String bookId;
    private static final String TAG = "PDF_VIEW_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get bookId from intent that we passed in intent
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        Log.d(TAG,"onCreate: BookId"+bookId);

        loadBookDetails();
        binding.BtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadBookDetails() {
        Log.d(TAG,"LoadBookDetails: Get PDF Url from db");
        //Database reference to get book details e.g get book url
        //Step 1 get book url using book id
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Bokks");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get book url
                        String pdfUrl = ""+ snapshot.child("url").getValue();
                        Log.d(TAG,"onDataChange: PDF URL"+ pdfUrl);

                        //Step 2 load pdf using that url from firebase storage
                        loadFromUrl(pdfUrl);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadFromUrl(String pdfUrl) {
        Log.d(TAG,"LoadBookFromUrl: Get PDF from Storage");
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        reference.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        //load pdf usin Bytes
                        binding.pdfview2.fromBytes(bytes)
                                .swipeHorizontal(true)//set false to scrooll vertical, set true to swipe horizontal
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        //set current and total pages in toolbar subtutle
                                        int currentPage = (page + 1);//do +1 because page status from 0
                                        binding.ToolbarSub.setText(currentPage+ "/"+pageCount);
                                        Log.d(TAG,"onPageChanged: " + currentPage + "/"+ pageCount);
                                    }
                                })
                                      .onError(new OnErrorListener() {
                                          @Override
                                          public void onError(Throwable t) {
                                              Log.d(TAG,"onError: "+t.getMessage());
                                              Toast.makeText(PdfView.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                          }
                                      })
                                            .onPageError(new OnPageErrorListener() {
                                                @Override
                                                public void onPageError(int page, Throwable t) {
                                                    Log.d(TAG,"onPageError: "+t.getMessage());
                                                    Toast.makeText(PdfView.this, "Error de página"+page+ " "+t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                               .load();

                        binding.progressBar.setVisibility(View.GONE);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"onFailure: "+e.getMessage());
                //falled to load
                binding.progressBar.setVisibility(View.GONE);

            }
        });
    }
}