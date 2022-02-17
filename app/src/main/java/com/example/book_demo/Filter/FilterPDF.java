package com.example.book_demo.Filter;



import android.widget.Filter;

import com.example.book_demo.Adapter.AdapterPDF;
import com.example.book_demo.Model.ModelPdf;

import java.util.ArrayList;

public class FilterPDF extends Filter {
    //arrayList in witch se want to search
    ArrayList<ModelPdf> filterList;
    //adapter in which filter need to be inplemented
    AdapterPDF adapterPDF;

    //Constructor

    public FilterPDF(ArrayList<ModelPdf> filterList, AdapterPDF adapterPDF) {
        this.filterList = filterList;
        this.adapterPDF = adapterPDF;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //value should not be null and empty
        if (constraint != null && constraint.length()>0){

            //Change to uppercase, or lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelPdf>filterModels = new ArrayList<>();

            for (int i=0; i<filterList.size(); i++){
                //validate
                if (filterList.get(i).getTitle().toUpperCase().contains(constraint)){
                    //add to filtered list
                    filterModels.add(filterList.get(i));
                }
            }
            results.count = filterModels.size();
            results.values = filterModels;
        }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results; //dont miss it
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //Apply filter cahnges
        adapterPDF.pdfArrayList = (ArrayList<ModelPdf>)results.values;
        //notifi cahnges
        adapterPDF.notifyDataSetChanged();

    }
}
