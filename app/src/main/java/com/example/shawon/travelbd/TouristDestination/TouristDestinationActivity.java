package com.example.shawon.travelbd.TouristDestination;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.shawon.travelbd.ModelClass.DistrictModel;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.RecyclerViewItemClickListener;
import com.example.shawon.travelbd.ViewHolder.DistrictViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * Created by SHAWON on 6/5/2019.
 */

public class TouristDestinationActivity extends AppCompatActivity {

    private static final String TAG = "TouristDestination";

    private DatabaseReference mDatabaseDistrict;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<DistrictModel, DistrictViewHolder> adapter;

    private ImageView mBackArrow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_destination);

        mDatabaseDistrict = FirebaseDatabase.getInstance().getReference().child("district");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        mBackArrow = (ImageView) findViewById(R.id.backArrow);

        // Recycler view with GridLayout...
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Here 2 is number of columns...

        loadMenu();

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<DistrictModel, DistrictViewHolder>(
                DistrictModel.class,
                R.layout.district_menu_item,
                DistrictViewHolder.class,
                mDatabaseDistrict
        ) {
            @Override
            protected void populateViewHolder(DistrictViewHolder viewHolder, DistrictModel model, final int position) {

                viewHolder.districtName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.districtImage);

                final DistrictModel mDistrictModel= model;

                viewHolder.setItemClickListener(new RecyclerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(TouristDestinationActivity.this, TouringPlacesItemActivity.class);
                        intent.putExtra("DistrictID", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

}