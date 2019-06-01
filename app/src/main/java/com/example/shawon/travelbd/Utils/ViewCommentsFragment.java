package com.example.shawon.travelbd.Utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.shawon.travelbd.ModelClass.Comment;
import com.example.shawon.travelbd.ModelClass.Photo;
import com.example.shawon.travelbd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by SHAWON on 6/1/2019.
 */

public class ViewCommentsFragment extends Fragment {

    private static final String TAG = "ViewCommentsFragment";

    public ViewCommentsFragment(){
        super();
        setArguments(new Bundle());
    }

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    //widgets
    private ImageView mBackArrow, mCheckMark;
    private EditText mComment;
    private ListView mListView;

    //vars
    private Photo mPhoto;
    private ArrayList<Comment> mComments;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_comments, container, false);

        mBackArrow = (ImageView) view.findViewById(R.id.backArrow);
        mCheckMark = (ImageView) view.findViewById(R.id.ivPostComment);
        mComment = (EditText) view.findViewById(R.id.comment);
        mListView = (ListView) view.findViewById(R.id.listView);
        mComments = new ArrayList<>();
        mContext = getActivity();

        try{
            mPhoto = getPhotoFromBundle();
            isUserLoggedInOrNot();

        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
        }

        return view;
    }

    private void setupWidgets(){

        CommentListAdapter adapter = new CommentListAdapter(mContext,
                R.layout.layout_comment, mComments);
        mListView.setAdapter(adapter);

        mCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mComment.getText().toString().equals("")){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment(mComment.getText().toString());

                    mComment.setText("");
                    closeKeyboard();
                }else{
                    Toast.makeText(getActivity(), "you can't post a blank comment", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void addNewComment(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);

        String commentID = myRef.push().getKey();

        Comment comment = new Comment();
        comment.setComment(newComment);
        comment.setDate_created(getTimestamp());
        comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //insert into photos node
        myRef.child(getString(R.string.photos))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

        //insert into user_photos node
        myRef.child(getString(R.string.user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.uploaded))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd't'HH:mm:ss'z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     * @return
     */
    private Photo getPhotoFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getParcelable(getString(R.string.photo));
        }else{
            return null;
        }
    }

    // This method is to check whether user logged in or not.
    private void isUserLoggedInOrNot() {
        Log.d(TAG,"ViewPostFragment : isUserLoggedInOrNot()");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        if(mPhoto.getComments().size() == 0){
            mComments.clear();
            Comment firstComment = new Comment();
            firstComment.setComment(mPhoto.getCaption());
            firstComment.setUser_id(mPhoto.getUser_id());
            firstComment.setDate_created(mPhoto.getUploaded_date());
            mComments.add(firstComment);
            mPhoto.setComments(mComments);
            setupWidgets();
        }

        myRef.child(mContext.getString(R.string.photos))
                .child(mPhoto.getPhoto_id())
                .child(mContext.getString(R.string.field_comments))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "onChildAdded: child added.");

                        Query query = myRef
                                .child(mContext.getString(R.string.photos))
                                .orderByChild(mContext.getString(R.string.field_photo_id))
                                .equalTo(mPhoto.getPhoto_id());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                                    Photo photo = new Photo();
                                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                                    photo.setCaption(objectMap.get(mContext.getString(R.string.field_caption)).toString());

                                    photo.setUploaded_date(objectMap.get(mContext.getString(R.string.field_uploaded_date)).toString());

                                    photo.setImage_url(objectMap.get(mContext.getString(R.string.field_image_url)).toString());

                                    photo.setPhoto_id(objectMap.get(mContext.getString(R.string.field_photo_id)).toString());

                                    photo.setUser_id(objectMap.get(mContext.getString(R.string.field_user_id)).toString());

                                    photo.setLocation(objectMap.get(mContext.getString(R.string.field_location)).toString());

                                    photo.setRating(objectMap.get(mContext.getString(R.string.field_rating)).toString());

                                    photo.setGoogle_places_rating(objectMap.get(mContext.getString(R.string.field_google_places_rating)).toString());

                                    try{
                                        photo.setTagged_people(objectMap.get(mContext.getString(R.string.field_tagged_user_id)).toString());
                                    }catch (RuntimeException e){
                                        e.printStackTrace();
                                    }

                                    photo.setTags(objectMap.get(mContext.getString(R.string.field_tags)).toString());

                                    mComments.clear();
                                    Comment firstComment = new Comment();
                                    firstComment.setComment(mPhoto.getCaption());
                                    firstComment.setUser_id(mPhoto.getUser_id());
                                    firstComment.setDate_created(mPhoto.getUploaded_date());
                                    mComments.add(firstComment);

                                    for (DataSnapshot dSnapshot : singleSnapshot
                                            .child(mContext.getString(R.string.field_comments)).getChildren()){
                                        Comment comment = new Comment();
                                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                        mComments.add(comment);
                                    }

                                    photo.setComments(mComments);

                                    mPhoto = photo;

                                    setupWidgets();

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: query cancelled.");
                            }
                        });
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}