package com.example.book_demo;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.icu.text.LocaleDisplayNames;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import static com.example.book_demo.Constants.MAX_BYTES_PDF;

public class MyApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
    }
    //create a static method
    private static final String TAG_DOWNLOAD = "PDF_DOUWNLOAD";
    public static final String formatTimesTamp(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        //format timestamp to dd/MM/yyyy
        String date = DateFormat.format("dd/MM/yyyy",cal).toString();
        return date;
    }

    public static void loadPdfSize(String pdfUrl, String pdfTitle, TextView sizeTV) {
        String TAG ="PDF_SIZE_TAG";
        //using url we can get file and its medatada from firebase storage


        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        //get size in bytes
                        double bytes = storageMetadata.getSizeBytes();
                        Log.d(TAG,"onSuccess: "+pdfTitle+" "+bytes);


                        //Convert bytes to Kb, Mb
                        double kb = bytes/1024;
                        double mb = kb/1024;

                        if (mb >= 1){
                            sizeTV.setText(String.format("%.2f", mb)+" MB");
                        }
                        else if (kb >= 1) {
                            sizeTV.setText(String.format("%.2f", kb) + " KB");
                        }
                        else {
                            sizeTV.setText(String.format("%.2f", bytes) + " bytes");
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed getting metadata
                        Log.d(TAG,"onFailure: "+e.getMessage());

                    }
                });
    }

    public static void loadPdfFromUrlSinglePage(String pdfUrl, String pdfTitle, PDFView pdfView, SpinKitView RlProgressBar) {
        //using url we can get file and its metadata from firebase storage
        String TAG ="PDF_LOAD_SINGLE_TAG";

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG,"onSuccess: "+pdfTitle+"successfylly got the file");

                        //set to pdfView
                        pdfView.fromBytes(bytes)
                                .pages(0)//Show only first page
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        //hide progress
                                        RlProgressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG,"onPageError: "+t.getMessage());

                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        RlProgressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG,"onPageError: "+ t.getMessage());
                                    }
                                })
                                .onLoad(new OnLoadCompleteListener() {
                                    @Override
                                    public void loadComplete(int nbPages) {
                                        //hide progress

                                        RlProgressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG,"loadComplete: pdf loader");
                                    }
                                })
                                .load();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //hide progress
                        RlProgressBar.setVisibility(View.INVISIBLE);
                        Log.d(TAG,"onFailure: failed gatting file from url due to"+ e.getMessage());
                    }
                });
    }

    public  static void  loadCategory(String categoryId, TextView categoryTv) {
        //get Category using

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category
                        String category = ""+snapshot.child("category").getValue();
                        //set to category text view
                        categoryTv.setText(category);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public static void incrementBookViewCount(String bookId){
        //Get book views count
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Bokks");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get views count
                        String viewsCount = ""+ snapshot.child("viewsCount").getValue();
                        //in case of null replace whith 0
                        if (viewsCount.equals("") || viewsCount.equals("null"));
                        viewsCount = "0";

                        //increment views count
                        long newViewsCount = Long.parseLong(viewsCount) + 1;
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("viewsCount",newViewsCount);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Bokks");
                        reference.child(bookId)
                                .updateChildren(hashMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void dowloadBook(Context context, String bookId, String bookTitle, String bookUrl){
        Log.d(TAG_DOWNLOAD,"downloadBook: downloading book: ");

        String nameWhitExtension = bookTitle + ".pdf";
        Log.d(TAG_DOWNLOAD,"downloadBoook: NAME: "+ nameWhitExtension);

        //ProgresDialog
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("Descargando "+ nameWhitExtension+".....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //Douwnload from firebase storage

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        storageReference.getBytes(MAX_BYTES_PDF)
        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d(TAG_DOWNLOAD,"onSuccess: BookDownloader");
                Log.d(TAG_DOWNLOAD,"onSuccess: SavingBook");
                SaveDownloadedbook(context, progressDialog, bytes, nameWhitExtension, bookId);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG_DOWNLOAD,"onFailure: Failed to download due to"+e.getMessage());
                progressDialog.dismiss();
                Toast.makeText(context, "Fallo al descargar"+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private static void SaveDownloadedbook(Context context, ProgressDialog progressDialog, byte[] bytes, String nameWhitExtension, String bookId) {
        Log.d(TAG_DOWNLOAD,"saveDownloadeBook: Saving downloaded Book");
        try {
            File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            downloadsFolder.mkdirs();

            String filePath = downloadsFolder.getPath() + "/" + nameWhitExtension;

            FileOutputStream out = new FileOutputStream(filePath);
            out.write(bytes);
            out.close();

            Toast.makeText(context, "Saved to Download folder", Toast.LENGTH_SHORT).show();
            Log.d(TAG_DOWNLOAD,"SaveDownloadedBook: Save to Download Folder");
            progressDialog.dismiss();

            incrementedBookDownloadCount(bookId);

        }
        catch (Exception e) {
            Log.d(TAG_DOWNLOAD,"saveDownloadedBook: Failed saving to Download Folder due to "+e.getMessage());
            Toast.makeText(context, "Failed Saving to Download Folder dou to "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static void incrementedBookDownloadCount(String bookId) {
        Log.d(TAG_DOWNLOAD,"incrementDownloadCount: Incrementing Book Download Count" );
        //Step 1 get previous download count
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Bokks");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String downloadsCount = ""+snapshot.child("downloadsCount").getValue();
                        Log.d(TAG_DOWNLOAD,"onDataChange: Downloads Count: "+ downloadsCount);

                        if (downloadsCount.equals("") || downloadsCount.equals("null")){
                            downloadsCount = "0";
                        }

                        //convert to long and increment 1
                        long newDownloadscount = Long.parseLong(downloadsCount) + 1;
                        Log.d(TAG_DOWNLOAD,"onDataChange: New Download Count: "+newDownloadscount);

                        //setup data update
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("downloadsCount",newDownloadscount);

                        //Steo 2 Uptade new incrementaddownloads count to db
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Bokks");
                        reference.child(bookId).updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG_DOWNLOAD,"onSucces: Downloads Count update...");
                                    }
                                })
                                  .addOnFailureListener(new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                          Log.d(TAG_DOWNLOAD,"onFailure: Failed to update Downloads Count due to"+e.getMessage());
                                      }
                                  });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
