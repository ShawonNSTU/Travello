package com.example.shawon.travelbd.Remote;

import com.example.shawon.travelbd.ModelClass.NearbyPlaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by SHAWON on 1/17/2019.
 */

public interface IGoogleAPIService {

    @GET
    Call<NearbyPlaces> getNearByPlaces(@Url String url);

}
