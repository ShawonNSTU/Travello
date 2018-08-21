package com.example.shawon.travelbd.AddPost;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.FilePath;
import com.example.shawon.travelbd.Utils.FileSearch;
import com.example.shawon.travelbd.Utils.GridImageAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by SHAWON on 8/11/2018.
 */

public class GalleryFragment extends Fragment {

    private static final String TAG = "Gallery Fragment";
    private static final int NUM_GRID_COLUMNS = 3;

    private ImageView mGalleryImageView;
    private GridView mGridView;
    private Spinner mSpinnerDirectory;
    private ProgressBar mProgressBar;
    private Button mZoomInOut;

    private ArrayList<String> mDirectories;
    private String mAppend = "file://";

    private int count = 0;
    private String mSelectedImage;
    private RelativeLayout mRelativeLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery,container,false);
        Log.d(TAG, "onCreateView : started");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated : started");

        mGalleryImageView = (ImageView) getView().findViewById(R.id.galleryImageView);
        mGridView = (GridView) getView().findViewById(R.id.gridView);
        mSpinnerDirectory = (Spinner) getView().findViewById(R.id.spinnerDirectory);
        mZoomInOut = (Button) getView().findViewById(R.id.zoom_in_out);
        mProgressBar = (ProgressBar) getView().findViewById(R.id.galleryFragmentProgressBar);
        mRelativeLayout = (RelativeLayout) getView().findViewById(R.id.relativeLayout2);
        mProgressBar.setVisibility(View.GONE);
        mZoomInOut.setVisibility(View.INVISIBLE);

        if (Build.VERSION.SDK_INT >= 21){
            mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.gray),android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        mDirectories = new ArrayList<>();

        closeButtonHandler();

        nextButtonHandler();

        initDirectory();

        mGalleryImageViewOnTouch();

        mZoomInOutOnClick();

    }

    private void mZoomInOutOnClick() {

        mZoomInOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count == 0) {
                    Log.d(TAG, "mZoomInOutOnClick:Clicked to zoom out of the image");
                    mGalleryImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    mGalleryImageView.setAdjustViewBounds(true);
                    count = 1;
                }
                else if(count == 1){
                    if (mGalleryImageView.getScaleType() != ImageView.ScaleType.CENTER_CROP) {
                        Log.d(TAG, "mZoomInOutOnClick:Clicked to zoom in of the image");
                        mGalleryImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                    if (mGalleryImageView.getAdjustViewBounds()) mGalleryImageView.setAdjustViewBounds(false);
                    count = 0;
                }
            }
        });

    }

    private void mGalleryImageViewOnTouch() {

        mGalleryImageView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getActivity(),new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (count == 0) {
                        Log.d(TAG, "mGalleryImageViewOnTouch:Double Tapped to fit the image");
                        mGalleryImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        mGalleryImageView.setAdjustViewBounds(true);
                        count = 1;
                    }
                    else if(count == 1){
                        if (mGalleryImageView.getScaleType() != ImageView.ScaleType.CENTER_CROP) {
                            Log.d(TAG, "mGalleryImageViewOnTouch:Double Tapped to crop the image");
                            mGalleryImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                        if (mGalleryImageView.getAdjustViewBounds()) mGalleryImageView.setAdjustViewBounds(false);
                        count = 0;
                    }
                    return super.onDoubleTap(e);
                }
            });
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG,"mGalleryImageViewOnTouch:onTouch "+"("+event.getRawX()+") , ("+event.getRawY()+")");
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

    }

    private void nextButtonHandler() {

        TextView mTextViewNext = (TextView) getView().findViewById(R.id.tvNext);

        mTextViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick : Navigating to the share screen");

                if (mSelectedImage.equals("")){
                    Snackbar snackbar = Snackbar.make(mRelativeLayout,"Please select an image.",Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(getResources().getColor(R.color.white));
                    snackbar.show();
                }else{
                    Intent intent = new Intent(getActivity(), NextShareActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    startActivity(intent);
                }
            }
        });

    }

    private void closeButtonHandler() {

        ImageView mClose = (ImageView) getView().findViewById(R.id.close);

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick : Closing the Gallery Fragment");
                getActivity().finish();
            }
        });

    }

    private void initDirectory() {

        FilePath filePath = new FilePath();

        if (FileSearch.getDirectoryPaths(filePath.PICTURES) != null){
            mDirectories.addAll(FileSearch.getDirectoryPaths(filePath.PICTURES));
        }
        if (FileSearch.getDirectoryPaths(filePath.DCIM) != null){
            mDirectories.addAll(FileSearch.getDirectoryPaths(filePath.DCIM));
        }
        if (FileSearch.getDirectoryPaths(filePath.PICTURE) != null){
            mDirectories.addAll(FileSearch.getDirectoryPaths(filePath.PICTURE));
        }
        if (FileSearch.getDirectoryPaths(filePath.DOWNLOAD) != null){
            mDirectories.addAll(FileSearch.getDirectoryPaths(filePath.DOWNLOAD));
        }
        mDirectories.add(filePath.SHAREit_PICTURES);

        ArrayList<String> directoryShortNames = new ArrayList<>();

        for (int i=0; i<mDirectories.size(); i++){
            int index = mDirectories.get(i).lastIndexOf("/");
            String directoryName = mDirectories.get(i).substring(index+1);
            directoryShortNames.add(directoryName);
        }

        if (getActivity() != null) {

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.spinner_custom_arrow, directoryShortNames);

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mSpinnerDirectory.setAdapter(arrayAdapter);

            mSpinnerDirectory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemClick:Selected: "+mDirectories.get(position));

                    mZoomInOut.setVisibility(View.INVISIBLE);

                    // setup grid image for the directory chosen
                    setupGridView(mDirectories.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void setupGridView(String selectedDirectory) {
        Log.d(TAG, "setupGridView:directory chosen: "+selectedDirectory);

        final ArrayList<String> imageURLs = FileSearch.getFilePaths(selectedDirectory);

        // set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        mGridView.setColumnWidth(imageWidth);

        GridImageAdapter gridImageAdapter = new GridImageAdapter(getActivity(),R.layout.square_grid_image_view,imageURLs,mAppend);
        mGridView.setAdapter(gridImageAdapter);

        // set the first image to be displayed when the activity fragment is inflated
        if (imageURLs.size() != 0) {
            setImage(imageURLs.get(0), mGalleryImageView, mAppend);
            mSelectedImage = imageURLs.get(0);
        }
        else {
            mGalleryImageView.setVisibility(View.INVISIBLE);
            mSelectedImage="";
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG,"onItemClick:grid view image selected: "+imageURLs.get(position));

                mZoomInOut.setVisibility(View.INVISIBLE);

                if (imageURLs.size() != 0){
                    setImage(imageURLs.get(position),mGalleryImageView,mAppend);
                    mSelectedImage = imageURLs.get(position);
                }
                else {
                    mGalleryImageView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void setImage(String imgUrl, final ImageView image, String append) {
        Log.d(TAG,"setImage:display of gallery image view");

        if (count == 1){
            image.setAdjustViewBounds(false);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            count = 0;
        }

        image.setVisibility(View.VISIBLE);

        mProgressBar.setVisibility(View.VISIBLE);

        String s = append;

        s+=imgUrl;

        // the image will only be resized if it's bigger than 1000*1000 pixels.
        Picasso.get().load(s).resize(1000,1000).onlyScaleDown().centerCrop().into(image, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"setImage:Success");

                mProgressBar.setVisibility(View.INVISIBLE);
                mZoomInOut.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}