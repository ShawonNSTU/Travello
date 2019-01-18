package com.example.shawon.travelbd.Remote;

/**
 * Created by SHAWON on 1/18/2019.
 */

public class Common {

    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static IGoogleAPIService getGoogleAPIService(){
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }

}
