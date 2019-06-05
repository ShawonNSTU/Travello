package com.example.shawon.travelbd.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.RecyclerViewItemClickListener;

/**
 * Created by SHAWON on 6/5/2019.
 */

public class DistrictViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView districtName;
    public ImageView districtImage;

    private RecyclerViewItemClickListener itemClickListener;

    public DistrictViewHolder(View itemView) {
        super(itemView);

        districtName = (TextView) itemView.findViewById(R.id.district_name);
        districtImage = (ImageView) itemView.findViewById(R.id.district_image);

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