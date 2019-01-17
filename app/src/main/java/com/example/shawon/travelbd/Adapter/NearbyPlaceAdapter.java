package com.example.shawon.travelbd.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shawon.travelbd.ModelClass.NearbyPlacesInfo;
import com.example.shawon.travelbd.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by SHAWON on 1/15/2019.
 */

public class NearbyPlaceAdapter extends RecyclerView.Adapter<NearbyPlaceAdapter.NearbyPlaceViewHolder> {

    private Context context;
    private List<NearbyPlacesInfo> nearbyPlacesInfoList;

    public NearbyPlaceAdapter(Context context, List<NearbyPlacesInfo> nearbyPlacesInfoList) {
        this.context = context;
        this.nearbyPlacesInfoList = nearbyPlacesInfoList;
    }

    @Override
    public NearbyPlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.nearby_place_cardview,null);
        return new NearbyPlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NearbyPlaceAdapter.NearbyPlaceViewHolder holder, int position) {
        NearbyPlacesInfo nearbyPlacesInfo = nearbyPlacesInfoList.get(position);
        holder.place_name.setText(nearbyPlacesInfo.getNearby_place_name());
        Picasso.get().load(nearbyPlacesInfo.getNearby_place_image()).into(holder.place_image);
    }

    @Override
    public int getItemCount() {
        return nearbyPlacesInfoList.size();
    }

    public class NearbyPlaceViewHolder extends RecyclerView.ViewHolder{

        TextView place_name;
        ImageView place_image;

        public NearbyPlaceViewHolder(View itemView) {
            super(itemView);

            place_name = itemView.findViewById(R.id.nearby_place_name);
            place_image = itemView.findViewById(R.id.nearby_place_image);

        }

    }
}