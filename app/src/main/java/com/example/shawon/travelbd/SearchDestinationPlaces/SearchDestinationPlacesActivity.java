package com.example.shawon.travelbd.SearchDestinationPlaces;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;

import com.example.shawon.travelbd.R;

/**
 * Created by SHAWON on 1/3/2019.
 */

public class SearchDestinationPlacesActivity extends AppCompatActivity {

    private static final String TAG = "SearchDestinationPlaces";
    private Context context = SearchDestinationPlacesActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_search_destination_places);
        Log.d(TAG,"onCreate: Started.");

        Toolbar mToolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG,"Adding the menu.");

        getMenuInflater().inflate(R.menu.search_destination_places_toolbar_menu,menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_destination_places).getActionView();
        searchView.setIconified(false);
        searchView.setQueryHint(Html.fromHtml("<small>"+getString(R.string.where_do_you_want_to_go)+"</small>"));
        EditText textSearch = ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        textSearch.setTextColor(getResources().getColor(R.color.light_black));
        textSearch.setTextSize(18);
        return true;
    }
}
