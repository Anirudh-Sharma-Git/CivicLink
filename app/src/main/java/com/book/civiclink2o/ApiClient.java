package com.book.civiclink2o;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//API connection.
public class ApiClient {


        public static final String BASE_URL = "https://7pwl5dh2-3000.inc1.devtunnels.ms/";

    private static Retrofit retrofit = null;

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