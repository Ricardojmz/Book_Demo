package com.example.book_demo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.book_demo.databinding.ActivityPdfDetailBinding;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PdfDetail extends AppCompatActivity {
    //
    private ActivityPdfDetailBinding binding;



    String bookId, bookTitle, bookUrl;

    private static final String TAG_DOWNLOAD = "DOWNLOAD_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");

        //at satart hide download button, because we need book url that we will load letter in function loadBookDetails()
        binding.btnDow.setVisibility(View.GONE);

        loadBookDetails();

        //progresBar = (SpinKitView) binding.progressBar;


        //increment book view
        MyApplication.incrementBookViewCount(bookId);

        //handle click
        binding.Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //handle click open pdf
        binding.btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(PdfDetail.this, PdfView.class);
                intent1.putExtra("bookId",bookId);
                startActivity(intent1);
            }
        });

        //Handle click, download pdf
        binding.btnDow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG_DOWNLOAD,"onClick: Checking permission");
                if (ContextCompat.checkSelfPermission(PdfDetail.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG_DOWNLOAD,"onClick: Permission already granted, can download book");
                    MyApplication.dowloadBook(PdfDetail.this,""+bookId,""+bookTitle,
                            ""+bookUrl);
                }
                else {
                    Log.d(TAG_DOWNLOAD,"onClick: Permission was not granted, request permission...");
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });
    }

    //request storage permission
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),isGranted ->{
                if (isGranted){
                    Log.d(TAG_DOWNLOAD,"Permission Granted");
                    MyApplication.dowloadBook(this,""+bookId,""+bookTitle,""+bookUrl);
                }
                else{
                    Log.d(TAG_DOWNLOAD,"Permission was denied...:");
                    Toast.makeText(this, "Permission was denied...", Toast.LENGTH_SHORT).show();
                }
            });


    private void loadBookDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Bokks");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        bookTitle = ""+ snapshot.child("title").getValue();
                        String description = ""+ snapshot.child("description").getValue();
                        String categoryId = ""+ snapshot.child("categoryId").getValue();
                        String viewsCount = ""+ snapshot.child("viewsCount").getValue();
                        String downloadsCount = ""+ snapshot.child("downloadsCount").getValue();
                        bookUrl = ""+ snapshot.child("url").getValue();
                        String timestamp = ""+ snapshot.child("timestamp").getValue();

                        //required data is loaded, show download button
                        binding.btnDow.setVisibility(View.VISIBLE);

                        //format date
                        String date = MyApplication.formatTimesTamp(Long.parseLong(timestamp));

                        MyApplication.loadCategory(
                                ""+categoryId,
                                binding.TvCategory);

                        MyApplication.loadPdfFromUrlSinglePage(
                                ""+bookUrl,
                                ""+bookTitle,
                                binding.PdfView,
                                 (SpinKitView) binding.progressBar);

                        MyApplication.loadPdfSize(
                                ""+bookUrl,
                                ""+bookTitle,
                                binding.TvSize);
                        //set data
                        binding.TvTitle.setText(bookTitle);
                        binding.TvDescripcion.setText(description);
                        binding.TvVistas.setText(viewsCount.replace("null","N/A"));
                        binding.TvDescargas.setText(downloadsCount.replace("null","N/A"));
                        binding.TvDate.setText(date);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}