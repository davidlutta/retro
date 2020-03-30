package com.davidlutta.retro.networking;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static RetrofitService instance;

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/posts/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static synchronized RetrofitService getInstance() {
        if (instance == null) {
            instance = new RetrofitService();
        }
        return instance;
    }
    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
