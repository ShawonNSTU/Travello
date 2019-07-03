package com.example.shawon.travelbd.MyTrips;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shawon.travelbd.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by SHAWON on 7/4/2019.
 */

public class Adapter extends PagerAdapter {

    private List<Trip> myTrips;
    private LayoutInflater layoutInflater;
    private Context context;

    public Adapter(List<Trip> myTrips, Context context) {
        this.myTrips = myTrips;
        this.context = context;
    }

    @Override
    public int getCount() {
        return myTrips.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item, container, false);

        ImageView imageView;
        TextView title, desc;

        imageView = view.findViewById(R.id.image);
        title = view.findViewById(R.id.name);
        desc = view.findViewById(R.id.date);

        Picasso.get().load(myTrips.get(position).getCity_image()).into(imageView);
        title.setText(myTrips.get(position).getCity_name());
        desc.setText(myTrips.get(position).getTrip_start_date());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // later code...
            }
        });

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}