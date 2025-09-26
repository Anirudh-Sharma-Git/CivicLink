package com.book.civiclink2o;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// This is our "Single Source of Truth" for the API connection.
public class ApiClient {

    // THIS IS THE ONLY PLACE YOU WILL EVER NEED TO CHANGE THE URL
        public static final String BASE_URL = "https://7pwl5dh2-3000.inc1.devtunnels.ms/";

    private static Retrofit retrofit = null;

    // This method builds and provides the Retrofit instance to the rest of the app.
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}