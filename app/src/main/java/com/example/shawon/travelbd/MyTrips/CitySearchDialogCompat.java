package com.example.shawon.travelbd.MyTrips;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;

import com.example.shawon.travelbd.R;

import java.util.ArrayList;

import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;

/**
 * Created by SHAWON on 6/22/2019.
 */

public class CitySearchDialogCompat<T extends Searchable> extends BaseSearchDialogCompat<T> {

    private String mTitle;
    private String mSearchHint;
    private SearchResultListener<T> mSearchResultListener;

    public CitySearchDialogCompat(Context context, String title, String searchHint,
                                  @Nullable Filter filter, ArrayList<T> items,
                                  SearchResultListener<T> searchResultListener) {
        super(context, items, filter, null, null);
        init(title, searchHint, searchResultListener);
    }

    private void init(String title, String searchHint,
                      SearchResultListener<T> searchResultListener) {
        mTitle = title;
        mSearchHint = searchHint;
        mSearchResultListener = searchResultListener;
    }

    @Override
    protected void getView(View view) {
        setContentView(view);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(true);
        TextView txtTitle = view.findViewById(ir.mirrajabi.searchdialog.R.id.txt_title);
        final EditText searchBox = view.findViewById(getSearchBoxId());
        txtTitle.setText(mTitle);
        searchBox.setHint(mSearchHint);

        view.findViewById(ir.mirrajabi.searchdialog.R.id.dummy_background)
                .setOnClickListener(view1 -> dismiss());

        final CitySearchModelAdapter adapter = new CitySearchModelAdapter(getContext(),
                R.layout.search_list_item, getItems());
        adapter.setSearchResultListener(mSearchResultListener);
        adapter.setSearchDialog(this);
        setFilterResultListener(items -> ((CitySearchModelAdapter<T>) getAdapter())
                .setSearchTag(searchBox.getText().toString())
                .setItems(items));
        setAdapter(adapter);
    }

    public CitySearchDialogCompat<T> setTitle(String title) {
        mTitle = title;
        return this;
    }

    @LayoutRes
    @Override
    protected int getLayoutResId() {
        return ir.mirrajabi.searchdialog.R.layout.search_dialog_compat;
    }

    @IdRes
    @Override
    protected int getSearchBoxId() {
        return ir.mirrajabi.searchdialog.R.id.txt_search;
    }

    @IdRes
    @Override
    protected int getRecyclerViewId() {
        return ir.mirrajabi.searchdialog.R.id.rv_items;
    }
}