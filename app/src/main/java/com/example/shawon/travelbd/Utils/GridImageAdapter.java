package com.example.shawon.travelbd.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.shawon.travelbd.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by SHAWON on 7/20/2018.
 */

public class GridImageAdapter extends ArrayAdapter<String>{

    private Context context;
    private int mLayoutResources;
    private ArrayList<String> imageUrl;
    private LayoutInflater layoutInflater;
    private String mAppend;

    public GridImageAdapter(Context context, int mLayoutResources, ArrayList<String> imageUrl, String mAppend) {
        super(context, mLayoutResources, imageUrl);

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.context = context;
        this.mLayoutResources = mLayoutResources;
        this.imageUrl = imageUrl;
        this.mAppend = mAppend;

    }

    private static class Viewholder{
        SquareImageView squareImageView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final Viewholder viewholder;

        if(convertView == null){
            convertView = layoutInflater.inflate(mLayoutResources,parent,false);
            viewholder = new Viewholder();
            viewholder.squareImageView = (SquareImageView) convertView.findViewById(R.id.gridImageView);
            convertView.setTag(viewholder);
        }
        else {
            viewholder = (Viewholder) convertView.getTag();
        }

        String s = mAppend;
        String imageURL = getItem(position);

        s+=imageURL;

        Picasso.get().load(s).resize(200,200).centerCrop().into(viewholder.squareImageView);

        return convertView;

    }
}