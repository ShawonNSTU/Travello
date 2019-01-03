package com.example.shawon.travelbd.SearchDestinationPlaces;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.IsConnectedToInternet;
import com.example.shawon.travelbd.Utils.PlaceAutocompleteAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by SHAWON on 1/3/2019.
 */

public class SearchDestinationPlacesActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SearchDestinationPlaces";
    private Context context = SearchDestinationPlacesActivity.this;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));
    private GoogleApiClient mGoogleApiClientForGooglePlaces;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_search_destination_places);
        Log.d(TAG,"onCreate: Started.");

        Toolbar mToolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(mToolbar);

        if (IsConnectedToInternet.isConnectedToInternet(context)){
            buildGoogleApiClient();
        }
        else {
            Toast.makeText(context,"Please check your internet connection.",Toast.LENGTH_SHORT).show();
        }
    }

    private void buildGoogleApiClient() {

        mGoogleApiClientForGooglePlaces = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(context,mGoogleApiClientForGooglePlaces,LAT_LNG_BOUNDS,null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG,"Adding the menu resource file.");

        getMenuInflater().inflate(R.menu.search_destination_places_toolbar_menu,menu);

        //getting the search view of the menu...
        SearchView searchView = (SearchView) menu.findItem(R.id.search_destination_places).getActionView();
        searchView.setIconified(false);
        searchView.setQueryHint(Html.fromHtml("<small>"+getString(R.string.where_do_you_want_to_go)+"</small>"));

        //getting edit text of the search view to change it's text properties...
        EditText textSearch = ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        textSearch.setTextColor(getResources().getColor(R.color.light_black));
        textSearch.setTextSize(18);
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setAdapter(mPlaceAutocompleteAdapter);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClientForGooglePlaces.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClientForGooglePlaces != null){
            mGoogleApiClientForGooglePlaces.disconnect();
        }
    }
}
