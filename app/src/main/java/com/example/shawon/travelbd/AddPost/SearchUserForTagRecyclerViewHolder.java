package com.example.shawon.travelbd.AddPost;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.RecyclerViewItemClickListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by SHAWON on 9/4/2018.
 */

public class SearchUserForTagRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public CircleImageView mProfileImage;
    public TextView mUserName;
    private RecyclerViewItemClickListener recyclerViewItemClickListener;

    public SearchUserForTagRecyclerViewHolder(View itemView) {
        super(itemView);

        mProfileImage = (CircleImageView) itemView.findViewById(R.id.profile_image);
        mUserName = (TextView) itemView.findViewById(R.id.user_name);
        itemView.setOnClickListener(this);
    }

    public void setRecyclerViewItemClickListener(RecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.recyclerViewItemClickListener = recyclerViewItemClickListener;
    }

    @Override
    public void onClick(View v) {
        recyclerViewItemClickListener.onClick(v,getAdapterPosition(),false);
    }
}