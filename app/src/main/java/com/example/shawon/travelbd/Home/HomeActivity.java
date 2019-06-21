package com.example.shawon.travelbd.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.shawon.travelbd.ChatWithFriends.MainActivity;
import com.example.shawon.travelbd.FavouritePlace.FavouritePlaceActivity;
import com.example.shawon.travelbd.Login.LoginActivity;
import com.example.shawon.travelbd.ModelClass.Photo;
import com.example.shawon.travelbd.MyTrips.AddNewTripActivity;
import com.example.shawon.travelbd.Profile.ProfileActivity;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.SearchDestinationPlaces.SearchDestinationPlacesActivity;
import com.example.shawon.travelbd.SuggestForFollow.SuggestForFollowActivity;
import com.example.shawon.travelbd.SuggestedPlaces.SuggestedPlacesActivity;
import com.example.shawon.travelbd.TouristDestination.TouristDestinationActivity;
import com.example.shawon.travelbd.Utilities.UtilitiesActivity;
import com.example.shawon.travelbd.Utils.BottomNavigationViewHelper;
import com.example.shawon.travelbd.Utils.MainfeedListAdapter;
import com.example.shawon.travelbd.Utils.SectionsPagerAdapter;
import com.example.shawon.travelbd.Utils.ViewCommentsFragment;
import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        MainfeedListAdapter.OnLoadMoreItemsListener{

    @Override
    public void onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos");
        HomeFragment fragment = (HomeFragment)getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + mViewPager.getCurrentItem());
        if(fragment != null){
            fragment.displayMorePhotos();
        }
    }

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private NavigationView navigationView;
    private ImageView cameraFragment;

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;
    private Context context = HomeActivity.this;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;

    private static final int HOME_FRAGMENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //facebook SDK initialize when a user opens the application
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_home);
        Log.d(TAG,"HomeActivity onCreate: Starting.");

        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFrameLayout = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);

        navigationView = (NavigationView) findViewById(R.id.nav_view_home_page);
        navigationView.setNavigationItemSelectedListener(this);

        mToolbar = (Toolbar) findViewById(R.id.tabs);
        setSupportActionBar(mToolbar);

        cameraFragment = (ImageView) findViewById(R.id.camera_fragment);
        cameraFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);    // Slide to the next viewpager
            }
        });

        setupViewPagerListener();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout_HomeActivity);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupBottomNavigationView();

        setupViewPager();

        isUserLoggedInOrNot();
    }

    public void onCommentThreadSelected(Photo photo,String callingActivity){
        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread");

        ViewCommentsFragment fragment  = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putString(getString(R.string.home_activity), getString(R.string.home_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }

    public void hideLayout(){
        Log.d(TAG, "hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    public void showLayout(){
        Log.d(TAG, "hideLayout: showing layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }

    private void setupViewPagerListener() {
        Log.d(TAG,"Setting Up ViewPagerListener");

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // No need to code
            }
            @Override
            public void onPageSelected(int position) {
                if (position == 1){         // Position 1 means Camera Fragment
                    getSupportActionBar().hide();
                }
                else if (position == 0){        // Position 0 means Home Fragment
                    getSupportActionBar().show();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                // No need to code
            }
        });
    }

    // adding Home and Camera pager in Home Activity with viewpager
    private void setupViewPager() {
        Log.d(TAG,"Adding Home and Camera pager with viewpager");

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        sectionsPagerAdapter.addFragment(new HomeFragment());
        sectionsPagerAdapter.addFragment(new CameraFragment());
        mViewPager.setAdapter(sectionsPagerAdapter);
    }

    // BottomNavigationView Setup
    private void setupBottomNavigationView() {
        Log.d(TAG,"HomeActivity setupBottomNavigationView: setting up BottomNavigationView");

        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(context, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Handle navigation view item clicks here
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG,"onNavigationItemSelected : handling navigation view item click");

        int id = item.getItemId();

        if (id == R.id.nav_profile){
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        }
        else if(id == R.id.nav_search_destination_places){
            startActivity(new Intent(HomeActivity.this, SearchDestinationPlacesActivity.class));
        }
        else if (id == R.id.nav_sign_out){
            mAuth.signOut();
            finish();
        }
        else if (id == R.id.nav_tourist_destination){
            startActivity(new Intent(HomeActivity.this, TouristDestinationActivity.class));
        }
        else if (id == R.id.nav_message){
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }

        else if(id == R.id.nav_utilities_info){
            startActivity(new Intent(HomeActivity.this, UtilitiesActivity.class));
        }

        else if(id == R.id.nav_suggested_places){
            startActivity(new Intent(HomeActivity.this, SuggestedPlacesActivity.class));
        }

        else if (id == R.id.nav_nearbyPeople) {
            startActivity(new Intent(HomeActivity.this, SuggestForFollowActivity.class));
        }

        else if (id == R.id.nav_favourite_places){
            startActivity(new Intent(HomeActivity.this, FavouritePlaceActivity.class));
        }

        else if (id == R.id.nav_my_trips){
            startActivity(new Intent(HomeActivity.this, AddNewTripActivity.class));
        }

        DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawerLayout_HomeActivity);
        mDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if(mFrameLayout.getVisibility() == View.VISIBLE){
            showLayout();
        }
        DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawerLayout_HomeActivity);
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupViewPagerListener();       // to check whether the viewpager is in Home or Camera ( background runnable check)
    }

    /*
        --------------------------------- Firebase ----------------------------------
     */

    /**
     * checks to see whether the @param 'user' is null or not.
     * if null then go to the LoginActivity for Signing in.
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in or not.");

        if(user == null){
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // This method is to check whether user logged in or not.
    private void isUserLoggedInOrNot() {
        Log.d(TAG,"HomeActivity : isUserLoggedInOrNot()");

        mAuth = FirebaseAuth.getInstance();

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

                //check if the user is logged in or not. if not then go to the LoginActivity.
                checkCurrentUser(user);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mViewPager.setCurrentItem(HOME_FRAGMENT);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}