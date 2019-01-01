package com.example.shawon.travelbd.AddPost;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.ShowCamera;

/**
 * Created by SHAWON on 8/11/2018.
 */

public class PhotoFragment extends Fragment {

    private static final String TAG = "Photo Fragment";

    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;

    private ImageView mCaptureButton;
    private Bitmap bitmap = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"onViewCreated: started.");

        frameLayout = (FrameLayout) getView().findViewById(R.id.frameLayout);
        mCaptureButton = (ImageView) getView().findViewById(R.id.capture_image);

        mCaptureButton.setColorFilter(getActivity().getResources().getColor(R.color.divider));

        capturePhoto();

        closeButtonHandler();

        nextButtonHandler();

    }

    private void nextButtonHandler() {

        TextView nextButton = (TextView) getView().findViewById(R.id.tvNext);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"nextButtonHandler : onClicked.");

                if(bitmap != null){
                    Toast.makeText(getActivity(),""+bitmap,Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(),"No image captured.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void closeButtonHandler() {

        ImageView mClose = (ImageView) getView().findViewById(R.id.close);

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick : Closing the Photo Fragment");
                getActivity().finish();
            }
        });

    }

    /**
     * handle the photo that was captured
     */
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "Camera:PictureCallback:Handling the picture that was taken");

            // getting bitmap of the captured photo
            bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
        }
    };

    /**
     * Place the camera by SurfaceView and SurfaceHolder
     */
    private void showCameraSurface() {
        Log.d(TAG,"showCameraSurface:Preparing the surface for showing camera");

        try {
            camera = Camera.open();
        }catch (RuntimeException e){
            e.printStackTrace();
        }

        showCamera = new ShowCamera(getActivity(),camera);
        frameLayout.addView(showCamera);

    }

    /**
     * Handle the capture button to capture photo
     */
    private void capturePhoto() {
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick:capturePhoto");

                if (camera != null){
                    camera.takePicture(null,null,mPictureCallback);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        showCameraSurface();
    }

}