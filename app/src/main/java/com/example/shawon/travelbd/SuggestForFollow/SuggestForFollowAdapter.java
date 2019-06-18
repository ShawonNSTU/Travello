package com.example.shawon.travelbd.SuggestForFollow;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.shawon.travelbd.ModelClass.UserPersonalInfo;
import com.example.shawon.travelbd.Profile.ProfileActivity;
import com.example.shawon.travelbd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by SHAWON on 6/18/2019.
 */

public class SuggestForFollowAdapter extends ArrayAdapter<NearbyPeople> {

    private static final String TAG = "SuggestForFollowAdapter";

    private LayoutInflater mInflater;
    private int mLayoutResource;
    private DatabaseReference mReference;
    private Context mContext;

    public SuggestForFollowAdapter(@NonNull Context context, @LayoutRes int resource, List<NearbyPeople> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        mContext = context;
        mReference = FirebaseDatabase.getInstance().getReference();
    }
    private static class ViewHolder{
        CircleImageView mProfilePhoto;
        TextView username, mMutualFriend, mHometown;
        Button mFollowUnFollowButton;

        UserPersonalInfo user  = new UserPersonalInfo();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(mLayoutResource, parent, false);
            holder = new ViewHolder();
            holder.mMutualFriend = (TextView) convertView.findViewById(R.id.numberOfMutualFriend);
            holder.mProfilePhoto = (CircleImageView) convertView.findViewById(R.id.profile_image);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.mFollowUnFollowButton = (Button) convertView.findViewById(R.id.followUnfollowButton);
            holder.mHometown = (TextView) convertView.findViewById(R.id.hometown);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mMutualFriend.setText(String.valueOf(getItem(position).getMutual_friend())+" mutual friend");
        Picasso.get().load(getItem(position).getProfile_photo()).into(holder.mProfilePhoto);
        holder.username.setText(getItem(position).getUsername());
        holder.mHometown.setText(getItem(position).getHometown());

        holder.mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra(mContext.getString(R.string.calling_activity),
                        mContext.getString(R.string.home_activity));
                intent.putExtra(mContext.getString(R.string.intent_user), holder.user);
                mContext.startActivity(intent);
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra(mContext.getString(R.string.calling_activity),
                        mContext.getString(R.string.home_activity));
                intent.putExtra(mContext.getString(R.string.intent_user), holder.user);
                mContext.startActivity(intent);
            }
        });

        holder.mFollowUnFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = holder.mFollowUnFollowButton.getText().toString();
                if(text.equals("FOLLOW")){

                    FirebaseDatabase.getInstance().getReference()
                            .child(mContext.getString(R.string.dbname_following))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getItem(position).getUser_id())
                            .child(mContext.getString(R.string.field_user_id))
                            .setValue(getItem(position).getUser_id());

                    FirebaseDatabase.getInstance().getReference()
                            .child(mContext.getString(R.string.dbname_followers))
                            .child(getItem(position).getUser_id())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(mContext.getString(R.string.field_user_id))
                            .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    holder.mFollowUnFollowButton.setText("UNFOLLOW");
                    holder.mFollowUnFollowButton.setBackgroundColor(mContext.getResources().getColor(R.color.red));

                } else{

                    FirebaseDatabase.getInstance().getReference()
                            .child(mContext.getString(R.string.dbname_following))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getItem(position).getUser_id())
                            .removeValue();

                    FirebaseDatabase.getInstance().getReference()
                            .child(mContext.getString(R.string.dbname_followers))
                            .child(getItem(position).getUser_id())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .removeValue();
                    holder.mFollowUnFollowButton.setText("FOLLOW");
                    holder.mFollowUnFollowButton.setBackgroundColor(mContext.getResources().getColor(R.color.next));

                }
            }
        });

        //get the user object
        Query userQuery = mReference
                .child(mContext.getString(R.string.user_personal_Info))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.getValue(UserPersonalInfo.class).getUsername());

                    holder.user = singleSnapshot.getValue(UserPersonalInfo.class);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return convertView;
    }
}