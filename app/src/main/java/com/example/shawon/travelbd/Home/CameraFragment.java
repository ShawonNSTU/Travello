package com.example.shawon.travelbd.Home;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.shawon.travelbd.AddPost.NextShareActivity;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.ShowCamera;

/**
 * Created by SHAWON on 7/17/2018.
 */

public class CameraFragment extends Fragment{

    private static final String TAG = "Camera Fragment";

    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;

    private ImageView mCaptureButton;
    private Bitmap bitmap = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        frameLayout = (FrameLayout) getView().findViewById(R.id.frameLayout);
        mCaptureButton = (ImageView) getView().findViewById(R.id.capture_image);

        mCaptureButton.setColorFilter(getActivity().getResources().getColor(R.color.divider));

        capturePhoto();

    }

    /**
     * handle the photo that was captured
     */
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "Camera:PictureCallback:Handling the picture that was taken");
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            // getting bitmap of the captured photo
            bitmap = BitmapFactory.decodeByteArray(data,0,data.length);

            // reducing the size of bitmap
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float bitmapRatio = (float)width / (float) height;
            if (bitmapRatio > 1){
                width = 300;
                height = (int) (width/bitmapRatio);
            }
            else {
                height = 300;
                width = (int) (height*bitmapRatio);
            }

            // getting the bitmap with new height and width
            bitmap = Bitmap.createScaledBitmap(bitmap,width,height,true);
            progressDialog.dismiss();
            if(bitmap != null){
                Log.d(TAG,"nextButtonHandler : onClicked : Bitmap : "+bitmap);
                try {
                    Intent intent = new Intent(getActivity(), NextShareActivity.class);
                    intent.putExtra(getString(R.string.captured_image_bitmap), bitmap);
                    startActivity(intent);
                }catch (RuntimeException e){
                    e.getMessage();
                }
            }
            else{
                Toast.makeText(getActivity(),"Sorry, you have not captured any photo!",Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * Place the camera by SurfaceView and SurfaceHolder
     */
    private void showCameraSurface() {
        Log.d(TAG,"showCameraSurface : Preparing the surface for showing camera");

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
                Log.d(TAG,"onClick : capturePhoto");

                if (camera != null && bitmap == null){
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