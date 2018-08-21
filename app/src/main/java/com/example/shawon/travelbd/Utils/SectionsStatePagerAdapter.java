package com.example.shawon.travelbd.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SHAWON on 7/21/2018.
 */

public class SectionsStatePagerAdapter extends FragmentStatePagerAdapter{

    private static final String TAG = "SectionsStatePagerAdapter";

    private final List<Fragment> mFragmentList = new ArrayList<>();             // List of Fragment

    private final HashMap<Fragment, Integer> mFragments = new HashMap<>();      // Fragment Object and its number

    private final HashMap<String, Integer> mFragmentNumbers = new HashMap<>();  // Fragment name and its number

    private final HashMap<Integer, String> mFragmentNames = new HashMap<>();    // Fragment number and its name

    public SectionsStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String fragmentName){

        mFragmentList.add(fragment);

        mFragments.put(fragment,mFragmentList.size()-1);

        mFragmentNumbers.put(fragmentName,mFragmentList.size()-1);

        mFragmentNames.put(mFragmentList.size()-1,fragmentName);

    }

    /*
     * returns the fragment number / Integer
     * @param fragmentName / String
     */

    public Integer getFragmentNumber(String fragmentName) {

        if(mFragmentNumbers.containsKey(fragmentName)){
            return mFragmentNumbers.get(fragmentName);
        }
        else{
            return null;
        }

    }

    /*
     * returns the fragment number / Integer
     * @param fragment / Object
     */

    public Integer getFragmentNumber(Fragment fragment) {

        if(mFragments.containsKey(fragment)){
            return mFragments.get(fragment);
        }
        else{
            return null;
        }

    }

    /*
     * returns the fragmentName / String
     * @param fragmentNumber / Integer
     */

    public String getFragmentName(Integer fragmentNumber){
        if(mFragmentNames.containsKey(fragmentNumber)){
            return mFragmentNames.get(fragmentNumber);
        }
        else{
            return null;
        }
    }

}