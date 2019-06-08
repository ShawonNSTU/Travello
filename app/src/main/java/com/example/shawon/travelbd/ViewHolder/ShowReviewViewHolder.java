package com.example.shawon.travelbd.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.RecyclerViewItemClickListener;

/**
 * Created by SHAWON on 6/8/2019.
 */

public class ShowReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mUserName,mUserReview,mTimestamp;
    public RatingBar mRatingBar;
    public ImageView mUserProfileImage;
    private RecyclerViewItemClickListener itemClickListener;

    public ShowReviewViewHolder(View itemView) {
        super(itemView);

        mUserName = (TextView) itemView.findViewById(R.id.comment_username);
        mUserReview = (TextView) itemView.findViewById(R.id.txt_Comment);
        mRatingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        mUserProfileImage = (ImageView) itemView.findViewById(R.id.comment_profile_image);
        mTimestamp = (TextView) itemView.findViewById(R.id.comment_time_posted);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(RecyclerViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

    }
}