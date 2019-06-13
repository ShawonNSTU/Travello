package com.example.shawon.travelbd.SuggestedPlaces;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.shawon.travelbd.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by SHAWON on 6/13/2019.
 */

public class ReviewListAdapter extends BaseAdapter {

    private ArrayList<Review> reviewArrayList;
    private LayoutInflater layoutInflater;

    public ReviewListAdapter(Context mContext, ArrayList<Review> reviewArrayList) {
        this.reviewArrayList = reviewArrayList;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return reviewArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return reviewArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.review_list_item,null);
            holder = new ViewHolder();

            holder.author_name = (TextView) convertView.findViewById(R.id.review_username);
            holder.profile_photo_url = (CircleImageView) convertView.findViewById(R.id.review_profile_image);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            holder.relative_time_description = (TextView) convertView.findViewById(R.id.review_time_posted);
            holder.text = (TextView) convertView.findViewById(R.id.txt_review);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.author_name.setText(reviewArrayList.get(position).getAuthor_name());
        Picasso.get().load(reviewArrayList.get(position).getProfile_photo_url()).into(holder.profile_photo_url);
        holder.ratingBar.setRating(Float.parseFloat(reviewArrayList.get(position).getRating()));
        holder.relative_time_description.setText(reviewArrayList.get(position).getRelative_time_description());
        holder.text.setText(reviewArrayList.get(position).getText());

        return  convertView;
    }
    static class ViewHolder{
        TextView author_name;
        CircleImageView profile_photo_url;
        RatingBar ratingBar;
        TextView relative_time_description;
        TextView text;
    }
}
