package com.example.shawon.travelbd.AddPost;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.Permissions;
import com.example.shawon.travelbd.Utils.SectionsPagerAdapter;

/**
 * Created by SHAWON on 7/17/2018.
 */

public class AddPostActivity extends AppCompatActivity {

    private static final String TAG = "AddPostActivity";
    private static final int ACTIVITY_NUM = 2;
    private Context context = AddPostActivity.this;

    private static final int VERIFY_PERMISSION_REQUEST = 1;

    ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        Log.d(TAG, "AddPostActivity onCreate: Starting.");

        if (checkPermissionArray(Permissions.PERMISSIONS)){
            setupViewPager();
        }
        else {
            verifyPermissionArray(Permissions.PERMISSIONS);
        }

    }

    /**
     * return the current tab number
     * 0 = GalleryFragment
     * 1 = PhotoFragment
     * @return
     */
    public int getCurrentTabNumber() {
        return mViewPager.getCurrentItem();
    }

    /**
     * adding Gallery and Photo Fragment in AddPost Activity with viewpager
     */
    private void setupViewPager() {
        Log.d(TAG, "setupViewPager:\nadding Gallery and Photo Fragment with viewpager");

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        sectionsPagerAdapter.addFragment(new GalleryFragment());        // Fragment Number 0

        sectionsPagerAdapter.addFragment(new PhotoFragment());          // Fragment Number 1

        mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(sectionsPagerAdapter);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabsBottom);

        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setText(getString(R.string.gallery));

        mTabLayout.getTabAt(1).setText(getString(R.string.photo));
    }

    public int getTask(){
        Log.d(TAG,"getTask : Task is : "+getIntent().getFlags());
        return getIntent().getFlags();
    }

    /**
     * check an array of permission
     * @param permissions
     * @return
     */
    public boolean checkPermissionArray(String[] permissions){
        Log.d(TAG, "checkPermissionArray:Checking permission array");

        for (int i = 0; i < permissions.length; i++){

            String mSinglePermissionCheck = permissions[i];

            if (!singlePermissionCheck(mSinglePermissionCheck)){
                Log.d(TAG,"checkPermissionArray:All permissions are not granted");

                return false;
            }
        }
        Log.d(TAG,"checkPermissionArray:All permissions are granted");

        return true;
    }

    /**
     * check a single permission
     * @param singlePermission
     * @return
     */
    public boolean singlePermissionCheck(String singlePermission){
        Log.d(TAG, "singlePermissionCheck:checking permission: "+singlePermission);

        int permissionRequest = ActivityCompat.checkSelfPermission(AddPostActivity.this,singlePermission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"singlePermissionCheck:\nPermission is not granted for "+singlePermission);

            return false;
        }
        else{
            Log.d(TAG,"singlePermissionCheck:\nPermission is granted for "+singlePermission);

            return true;
        }
    }

    /**
     * verify an array of permission
     * @param permissions
     */

    public void verifyPermissionArray(String[] permissions){
        Log.d(TAG,"verifyPermissionArray:verifying all the permissions");

        ActivityCompat.requestPermissions(
                AddPostActivity.this,
                permissions,
                VERIFY_PERMISSION_REQUEST
        );
    }

}