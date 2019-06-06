package com.example.shawon.travelbd.TouristDestination;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shawon.travelbd.ModelClass.TouringPlaceItem;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.RecyclerViewItemClickListener;
import com.example.shawon.travelbd.ViewHolder.TouringPlaceItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

/**
 * Created by SHAWON on 6/6/2019.
 */

public class TouringPlacesItemActivity extends AppCompatActivity{

    private static final String TAG = "TouringPlacesItem";

    // ...
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference mDatabaseTouringPlaces;
    private String districtID = "";
    private Context mContext;
    private FirebaseRecyclerAdapter<TouringPlaceItem,TouringPlaceItemViewHolder> adapter;
    private ImageView mBackArrow;

    // will be deleted...
    private EditText Name,Description,Location,Type,Wikipedia,How_to_go,Where_to_stay;
    private Button mButtonSelect,mButtonUpload;
    private static final int PICK_IMAGE_REQUEST = 71;
    private Uri mImageUri = null;
    private TouringPlaceItem mTouringPlaceItem;
    private StorageReference mStorage;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touring_places_item);

        mDatabaseTouringPlaces = FirebaseDatabase.getInstance().getReference("Touring Places");
        mStorage = FirebaseStorage.getInstance().getReference();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_touring_places);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mContext = TouringPlacesItemActivity.this;
        mBackArrow = (ImageView) findViewById(R.id.backArrow);

        if(getIntent() != null) {
            districtID = getIntent().getStringExtra("DistrictID");
        }
        if(!districtID.isEmpty() && districtID != null){
            loadListFood(districtID);
        }

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Add New Place");
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View addPlaceLayout = layoutInflater.inflate(R.layout.add_new_place_layout,null);

        Name= (EditText) addPlaceLayout.findViewById(R.id.name);
        Description = (EditText) addPlaceLayout.findViewById(R.id.description);
        Location = (EditText) addPlaceLayout.findViewById(R.id.location);
        Type = (EditText) addPlaceLayout.findViewById(R.id.type);
        Wikipedia = (EditText) addPlaceLayout.findViewById(R.id.wikipedia);
        How_to_go = (EditText) addPlaceLayout.findViewById(R.id.how_to_go);
        Where_to_stay = (EditText) addPlaceLayout.findViewById(R.id.where_to_stay);
        mButtonSelect = (Button) addPlaceLayout.findViewById(R.id.image_select_btn);
        mButtonUpload = (Button) addPlaceLayout.findViewById(R.id.upload_btn);

        mButtonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialogBuilder.setView(addPlaceLayout);

        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                if(mTouringPlaceItem != null){

                    mDatabaseTouringPlaces.push().setValue(mTouringPlaceItem);
                    Toast.makeText(mContext,"Added!",Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(mContext,"Please fill up all information about to the new food!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        alertDialogBuilder.show();
    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            mImageUri = data.getData();
            mButtonSelect.setText("SELECTED");

        }

    }

    private void uploadImage() {

        if(mImageUri != null && !TextUtils.isEmpty(Name.getText().toString()) && !TextUtils.isEmpty(Description.getText().toString())
                && !TextUtils.isEmpty(Location.getText().toString()) && !TextUtils.isEmpty(Wikipedia.getText().toString())
                && !TextUtils.isEmpty(How_to_go.getText().toString()) && !TextUtils.isEmpty(Where_to_stay.getText().toString())){

            final ProgressDialog mProgress = new ProgressDialog(this);
            mProgress.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference placeImage = mStorage.child("Touring Place Images/"+imageName);
            placeImage.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mProgress.dismiss();
                    Toast.makeText(mContext,"Uploaded!",Toast.LENGTH_SHORT).show();
                    placeImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            // Set value for New Food if image uploaded...

                            mTouringPlaceItem = new TouringPlaceItem();
                            mTouringPlaceItem.setName(Name.getText().toString());
                            mTouringPlaceItem.setDescription(Description.getText().toString());
                            mTouringPlaceItem.setLocation(Location.getText().toString());
                            mTouringPlaceItem.setType(Type.getText().toString());
                            mTouringPlaceItem.setWikipedia(Wikipedia.getText().toString());
                            mTouringPlaceItem.setHow_to_go(How_to_go.getText().toString());
                            mTouringPlaceItem.setWhere_to_stay(Where_to_stay.getText().toString());
                            mTouringPlaceItem.setDistrict_id(districtID);
                            mTouringPlaceItem.setImage(uri.toString());

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    mProgress.dismiss();
                    Toast.makeText(mContext,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgress.setMessage("Uploading "+progress+"%");

                }
            });

        }
        else {
            Toast.makeText(mContext,"Please fill up all information about to the new food!",Toast.LENGTH_SHORT).show();
        }

    }

    private void loadListFood(String districtID) {

        adapter = new FirebaseRecyclerAdapter<TouringPlaceItem, TouringPlaceItemViewHolder>(
                TouringPlaceItem.class,
                R.layout.touring_place_item,
                TouringPlaceItemViewHolder.class,
                mDatabaseTouringPlaces.orderByChild("district_id").equalTo(districtID)
        ) {
            @Override
            protected void populateViewHolder(TouringPlaceItemViewHolder viewHolder, final TouringPlaceItem model, int position) {

                viewHolder.placeName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.placeImage);

                viewHolder.setItemClickListener(new RecyclerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(mContext,""+adapter.getRef(position).getKey(),Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }
}