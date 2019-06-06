package com.example.shawon.travelbd.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.RecyclerViewItemClickListener;

/**
 * Created by SHAWON on 6/6/2019.
 */

public class TouringPlaceItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView placeName;
    public ImageView placeImage;

    private RecyclerViewItemClickListener itemClickListener;

    public TouringPlaceItemViewHolder(View itemView) {
        super(itemView);

        placeName = (TextView) itemView.findViewById(R.id.place_name);
        placeImage = (ImageView) itemView.findViewById(R.id.place_image);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(RecyclerViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}