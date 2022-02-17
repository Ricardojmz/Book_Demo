package com.example.book_demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class Cargar_PDF extends AppCompatActivity {

    TextView TvCategorias;
    EditText EtTitle, EtDescription;
    Button btnSubmit;
    ImageButton btnAtras, btnAttach;
    //Firebase auth
    private FirebaseAuth firebaseAuth;
    //arrayList to hold pdf categories
    private ArrayList<String> categoryTitleArrayList, categoryIdArrayList;
    //Tag for Debugging
    private static final String TAG = "ADD_PDF_TAG";
    //uri of picked
    private Uri pdfUri = null;
    //progress Dialog
    private ProgressDialog progressDialog;

    private static final int PDF_PICK_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_pdf);
        TvCategorias = findViewById(R.id.TvCategorias);
        EtTitle = findViewById(R.id.EtTitle);
        EtDescription = findViewById(R.id.EtDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnAtras = findViewById(R.id.btnAtras);
        btnAttach = findViewById(R.id.btnAttach);

        //Initialice Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        loadPdfCategories();

        //setup progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //handle click attach PDF
        btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfPickIntent();
            }
        });

        //handle click pik category
        TvCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryPickDialog();
            }
        });
        //Handle click upload PDF
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate data
                validateData();

            }
        });

    }

    private String title = "", description= ""; //category= "";
    private void validateData() {
        //Step 1: validate data
        Log.d(TAG,"validateData: validating data...");
        //get data
        title = EtTitle.getText().toString().trim();
        description = EtDescription.getText().toString().trim();
       // category = TvCategorias.getText().toString().trim();

        //Validate data
        if (TextUtils.isEmpty(title)){
            Toast.makeText(this,"Enter title...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(description)){
            Toast.makeText(this,"Enter Description...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(selectedCategoryId )) {
            Toast.makeText(this, "Pick category...", Toast.LENGTH_SHORT).show();
        }
        else if (pdfUri==null) {
            Toast.makeText(this, "Pick Pdf...", Toast.LENGTH_SHORT).show();
        }
        else {
            //all data is valid, can upload now
            uploadPdfToStorage();
        }
    }

    private void uploadPdfToStorage() {
        //Step 2: Upload pdf to firebase
        Log.d(TAG, "uploadPdfToStorage: uploading to storage...");

        //show progress
        progressDialog.setMessage("Uploading Pdf...");
        progressDialog.show();

        //timestamp
        long timestamp = System.currentTimeMillis();

        //path of pdf in firebase storage
        String filePathAndName = "Books/" + timestamp;
        //storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSucces: PDF uploaded to storage...");
                        Log.d(TAG, "onSucces: getting pdf uri...");

                        //get pdf uri
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String uploadedPdfUrl = "" + uriTask.getResult();

                        //upload to firebase DB
                        uploadPdfInfoToDB(uploadedPdfUrl, timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       progressDialog.dismiss();
                        Log.d(TAG, "onFailure: PDF upload failed due to" + e.getMessage());
                        Toast.makeText(Cargar_PDF.this, "PDF upload failed due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadPdfInfoToDB(String uploadedPdfUrl, long timestamp) {
        //Step 3: Upload pdf info to firebase db
        Log.d(TAG,"uploadPdfToStorage: uploading pdf info to firebase db...");

        progressDialog.setMessage("Uploading pdf info...");

        String vid = firebaseAuth.getUid();
        //setup data to upload also add view count download count while
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("vid",""+vid);
        hashMap.put("id",""+timestamp);
        hashMap.put("title",""+title);
        hashMap.put("description",""+description);
        hashMap.put("categoryId",""+selectedCategoryId);
        hashMap.put("url",""+uploadedPdfUrl);
        hashMap.put("timestamp",timestamp);
        hashMap.put("viewsCount",0);
        hashMap.put("downloadsCount",0);

        //db reference: DB > Books
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Bokks");
        ref.child("" + timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG,"onSucces: Successfully uploaded...");
                        Toast.makeText(Cargar_PDF.this,"Seccessfully uploaded...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Failed to upload to db due to" + e.getMessage());
                        Toast.makeText(Cargar_PDF.this, "Failed to upload to db due to"
                                + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void loadPdfCategories() {
        Log.d(TAG,"LoadPdfCategories: Loading pdf categories..");
        categoryTitleArrayList = new ArrayList<>();
        categoryIdArrayList = new ArrayList<>();

        //DB reference to load categories..... DB > categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();//clear before adding data
                categoryIdArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    //ModelCategory model = ds.getValue(ModelCategory.class);
                    //add to arrayList
                    //categoryTitleArrayList.add(model);
                    //Log.d(TAG,"onDateChange: "+ model.getCategory());
                    String categoryId = ""+ds.child("id").getValue();
                    String categoryTitle = ""+ds.child("category").getValue();
                    //add to respective arraylist
                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //selected category id an category title
    private String selectedCategoryId, selectedCategoryTitle;

    private void categoryPickDialog() {
        Log.d(TAG,"CategoryPickDialog: showing category pick dialog");

        //get array of category from arrayList
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for (int i = 0; i< categoryTitleArrayList.size(); i++){
            categoriesArray[i] = categoryTitleArrayList.get(i);
        }
        //AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elige la Categoria")
               .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       //handle item click
                       //get clicked item from list
                       selectedCategoryTitle = categoryTitleArrayList.get(which);
                       selectedCategoryId = categoryIdArrayList.get(which);
                       //Set to category textview
                       TvCategorias.setText(selectedCategoryTitle);

                       Log.d(TAG,"onClick: Selected Category: "+selectedCategoryId+" "+selectedCategoryTitle);
                   }
               }).show();
    }

    private void pdfPickIntent() {
        Log.d(TAG,"pdfPickIntent; starting pdf pick intent");
        Intent intent = new Intent();
        intent.setType("aplication/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona el PDF"),PDF_PICK_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode ==RESULT_OK){
            Log.d(TAG,"onActivityResult: PDF Picked");

            pdfUri = data.getData();
            Log.d(TAG,"onActivityResult: URI "+pdfUri);
        }
        else{
            Log.d(TAG,"onActivityResult: cancelled picking pdf");
            Toast.makeText(this,"cancelled picking pdf",Toast.LENGTH_SHORT).show();
        }
    }

}
