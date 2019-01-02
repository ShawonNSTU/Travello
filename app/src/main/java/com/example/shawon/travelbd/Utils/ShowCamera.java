package com.example.shawon.travelbd.Utils;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by SHAWON on 8/11/2018.
 */

public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback{

    private static final String TAG = "ShowCamera";

    private Camera camera;
    private SurfaceHolder surfaceHolder;
    Camera.Parameters parameters;

    public ShowCamera(Context context,Camera camera) {
        super(context);
        this.camera = camera;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            Log.d(TAG,"Getting Camera Parameters");
            parameters = camera.getParameters();
        }catch (RuntimeException e){
            e.printStackTrace();
        }

        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();

        Camera.Size mSize = null;

        // getting max picture size according to the users phone
        try {
            for (Camera.Size size : sizes){
                mSize = size;
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        Log.d(TAG,"Supported All Picture Size: "+sizes);

        Log.d(TAG,"Supported Max Picture Size For Capturing Photo: "+mSize);

        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){

            try {
                parameters.set("orientation","portrait");
                camera.setDisplayOrientation(90);
                parameters.setRotation(90);
            }catch (RuntimeException e){
                e.printStackTrace();
            }

        }
        else {

            try {
                parameters.set("orientation","landscape");
                camera.setDisplayOrientation(0);
                parameters.setRotation(0);
            }catch (RuntimeException e){
                e.printStackTrace();
            }

        }

        // set max picture size according to the users phone
        if (mSize != null){
            parameters.setPictureSize(mSize.width,mSize.height);
        }

        // set auto focus
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        try {
            Log.d(TAG,"Setting up the Parameters to Camera");
            camera.setParameters(parameters);
        }catch (RuntimeException e){
            e.printStackTrace();
        }

        try {
            Log.d(TAG,"Starting Preview of Camera");
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }catch (IOException | RuntimeException e){
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        try {
            camera.stopPreview();
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        camera.release();

    }
}