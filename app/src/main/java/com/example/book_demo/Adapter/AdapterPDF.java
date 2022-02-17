package com.example.book_demo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.book_demo.Filter.FilterPDF;
import com.example.book_demo.Model.ModelPdf;
import com.example.book_demo.MyApplication;
import com.example.book_demo.PdfDetail;
import com.example.book_demo.databinding.RowPdfBinding;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterPDF extends RecyclerView.Adapter<AdapterPDF.HolderPdf> implements Filterable {
    //context
    private Context context;
    //arrayList to hold list of data of type modelPdf
    public ArrayList<ModelPdf> pdfArrayList, filterList;

    //view bindingrowpdf.xml
    private RowPdfBinding binding;

    private SpinKitView RlProgressBar;

    private FilterPDF filter;

    private static final String TAG = "PDF_ADAPTER_TAG";

    //constructor
    public AdapterPDF(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdf onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind layout using view binding
        binding = RowPdfBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdf(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPDF.HolderPdf holder, int position) {
        //Get data, set data, handle click

        //get data
        ModelPdf model = pdfArrayList.get(position);
        String pdfId = model.getId();
        String title = model.getTitle();
        String categoryId = model.getCategoryId();
        String pdfUrl = model.getUrl();
        String description = model.getDescription();
        long timestamp = model.getTimestamp();

        //we need to convert timestann to dd/MM/yyyy
        String formattedate = MyApplication.formatTimesTamp(timestamp);

        //set holder
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formattedate);

        //We will need these fuctions many time

        //load further details like category, pdf from url in seprate functions
        MyApplication.loadCategory(
                ""+categoryId,
                holder.categoryTv);
        MyApplication.loadPdfFromUrlSinglePage(
                ""+pdfUrl,
                ""+title,
                holder.pdfView,
                holder.RlProgressBar
        );
        MyApplication.loadPdfSize(
                ""+pdfUrl,
                ""+title,
                 holder.sizeTv);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfDetail.class);
                intent.putExtra("bookId",pdfId);
                context.startActivity(intent);
            }
        });

    }







    @Override
    public int getItemCount() {
        return pdfArrayList.size();//return number of records | list size
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterPDF(filterList,this);
        }
        return filter;
    }

    //View Holder class for rowpdf.xml
    class HolderPdf extends RecyclerView.ViewHolder{
        //ui views of rowpdf.xml
        PDFView pdfView;
        SpinKitView RlProgressBar;
        TextView titleTv, descriptionTv, categoryTv, sizeTv, dateTv;
        //ImageButton btnMore;




        public HolderPdf(@NonNull View itemView){
            super(itemView);

            //init ui views
            pdfView = binding.pdfView;
            RlProgressBar = binding.RlProgressBar;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTV;
            categoryTv = binding.categoryTV;
            sizeTv = binding.sizeTv;
            dateTv = binding.dateTv;
            //btnMore = binding.btnMore;



        }
    }

}

