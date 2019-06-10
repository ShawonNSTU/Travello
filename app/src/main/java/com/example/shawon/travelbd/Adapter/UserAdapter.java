package com.example.shawon.travelbd.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shawon.travelbd.ChatWithFriends.MessageActivity;
import com.example.shawon.travelbd.ModelClass.UserInfo;
import com.example.shawon.travelbd.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by SHAWON on 6/11/2019.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>  {

    private Context mContext;
    private List<UserInfo> mUsers;

    public UserAdapter(Context mContext, List<UserInfo> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder holder, int position) {
        final UserInfo user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        Picasso.get().load(user.getProfile_image()).into(holder.profile_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getUser_id());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}