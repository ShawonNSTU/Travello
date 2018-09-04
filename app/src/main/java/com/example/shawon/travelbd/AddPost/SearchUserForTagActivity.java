package com.example.shawon.travelbd.AddPost;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.example.shawon.travelbd.ModelClass.UserPublicInfo;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.RecyclerViewItemClickListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

/**
 * Created by SHAWON on 9/2/2018.
 */

public class SearchUserForTagActivity extends AppCompatActivity implements TextWatcher {

    private static final String TAG = "SearchUserForTag";
    private Context context = SearchUserForTagActivity.this;

    private RecyclerView recyclerView;
    private AutoCompleteTextView mInputSearch;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserPublicInfo;
    private FirebaseRecyclerAdapter<UserPublicInfo,SearchUserForTagRecyclerViewHolder> mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_for_tag);

        Log.d(TAG, "onCreate : Started.");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_user);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        mDatabase = FirebaseDatabase.getInstance();
        mUserPublicInfo = mDatabase.getReference().child(getString(R.string.user_public_Info));

        loadAllUserInRecyclerAdapter("");

        mInputSearch = (AutoCompleteTextView) findViewById(R.id.user_input_search);
        mInputSearch.addTextChangedListener(this);

    }

    private void loadAllUserInRecyclerAdapter(String search) {

        Log.d(TAG,"loadAllUserInRecyclerAdapter : Loading Recycler Adapter For Searched String : "+search);

        Query query = mUserPublicInfo.orderByChild(getString(R.string.field_username)).startAt(search).endAt(search + "\uf8ff");

        mAdapter = new FirebaseRecyclerAdapter<UserPublicInfo, SearchUserForTagRecyclerViewHolder>(
                UserPublicInfo.class,
                R.layout.search_user_for_tag_viewholder,
                SearchUserForTagRecyclerViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(SearchUserForTagRecyclerViewHolder viewHolder, UserPublicInfo model, int position) {

                String profile_image = model.getProfile_photo();

                if (!profile_image.isEmpty()){
                    Picasso.get().load(model.getProfile_photo()).resize(100,100).centerCrop().into(viewHolder.mProfileImage);
                }
                else {
                    Picasso.get().load(R.drawable.avatar).resize(100,100).centerCrop().into(viewHolder.mProfileImage);
                }

                viewHolder.mUserName.setText(model.getUsername());

                viewHolder.setRecyclerViewItemClickListener(new RecyclerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Log.d(TAG,"RecyclerViewItemClickListener : onClick");
                    }
                });

            }
        };

        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        Log.d(TAG, "onTextChanged : Current Text In The Search Bar is : "+mInputSearch.getText().toString());

        String mSearchedString = mInputSearch.getText().toString();

        if (!mSearchedString.isEmpty()) {

            String mFirstLetter = String.valueOf(mSearchedString.charAt(0));

            mFirstLetter = mFirstLetter.toUpperCase();

            String mRestString = "";

            for (int i = 1; i < mSearchedString.length(); i++) {
                mRestString += mSearchedString.charAt(i);
            }

            mFirstLetter += mRestString;

            loadAllUserInRecyclerAdapter(mFirstLetter);
        }
        else {
            loadAllUserInRecyclerAdapter("");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}