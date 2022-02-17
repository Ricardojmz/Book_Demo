package com.example.book_demo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.book_demo.Model.ModelViewPager;
import com.example.book_demo.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class AdapterViewPager extends PagerAdapter {
    private Context context;
    private ArrayList<ModelViewPager> modelArrayList;

    //Constructor
    public AdapterViewPager(Context context, ArrayList<ModelViewPager> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
    }

    @Override
    public int getCount() {
        return modelArrayList.size();//Returns size of items in list
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //Inflate layout card_item.xml
        View view = LayoutInflater.from(context).inflate(R.layout.card_item,container,false);

        //Init vid viewsfrom card_item.xml
        ImageView IvBanner = view.findViewById(R.id.IvBanner);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvDescription = view.findViewById(R.id.tvDescription);
        TextView tvAuthor = view.findViewById(R.id.tvAuthor);

        //Get Data
        ModelViewPager model = modelArrayList.get(position);
        String title = model.getTitle();
        String description = model.getDescription();
        String author = model.getAuthor();
        int image = model.getImage();

        //Set Data
        IvBanner.setImageResource(image);
        tvTitle.setText(title);
        tvDescription.setText(description);
        tvAuthor.setText(author);

        //Handle card click
        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,title+"\n"+description+"\n"+author,Toast.LENGTH_SHORT).show();
            }
        });*/

        //Add view to container
        container.addView(view, position);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
