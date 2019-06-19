package com.example.shawon.travelbd.Utilities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shawon.travelbd.CurrencyConverter.CurrencyConverterActivity;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Weather.SearchLocationForWeather;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by SHAWON on 6/20/2019.
 */

public class UtilitiesItemAdapter extends ArrayAdapter<String> {

    private static final String TAG = "UtilitiesItemAdapter";

    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;

    public UtilitiesItemAdapter(@NonNull Context context, @LayoutRes int resource, List<String> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        mContext = context;
    }

    private static class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null){
            convertView = mInflater.inflate(mLayoutResource, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            holder.textView = (TextView) convertView.findViewById(R.id.text);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        String name = getItem(position);
        if(name.equals("Weather Forecast")){
            Picasso.get().load(R.drawable.weather).into(holder.imageView);
        }
        else if(name.equals("Currency Converter")){
            Picasso.get().load(R.drawable.currency).into(holder.imageView);
        }
        else if(name.equals("World Clock Time")){
            Picasso.get().load(R.drawable.world_clock_time).into(holder.imageView);
        }
        holder.textView.setText(name);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getItem(position).equals("Weather Forecast")){
                    mContext.startActivity(new Intent(mContext,SearchLocationForWeather.class));
                }
                else if(getItem(position).equals("Currency Converter")){
                    mContext.startActivity(new Intent(mContext,CurrencyConverterActivity.class));
                }
            }
        });

        return convertView;
    }
}