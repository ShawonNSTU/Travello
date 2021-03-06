package com.example.shawon.travelbd.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by SHAWON on 12/9/2018.
 */

public class ImageManager  {

    private static final String TAG = "ImageManager";

    public static Bitmap getBitmap(String imgUrl){
        File imageFile = new File(imgUrl);
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);
        }catch (FileNotFoundException e){
            Log.e(TAG, "getBitmap : FileNotFoundException: "+e.getMessage());
        }finally {
            try {
                fis.close();
            }catch (IOException | NullPointerException e){
                Log.e(TAG, "getBitmap : IOException | NullPointerException: "+e.getMessage());
            }
        }
        return bitmap;
    }

    /**
     * return byte array from bitmap
     * quality is greater than 0 but less than 100
     * @param bitmap
     * @param quality
     * @return
     */
    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG,quality,stream);
        }catch (NullPointerException e){
            Log.e(TAG, "getBytesFromBitmap : NullPointerException: "+e.getMessage());
        }
        return stream.toByteArray();
    }

}