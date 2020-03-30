package com.davidlutta.retro.datasource;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.davidlutta.retro.Api.JsonApi;
import com.davidlutta.retro.networking.RetrofitService;
import com.davidlutta.retro.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDataSource extends PageKeyedDataSource<Long, Post> {
    private static final String TAG = "com.davidlutta.retro.datasource.PostDataSource";
    private JsonApi jsonApi;

    public PostDataSource() {
        jsonApi = RetrofitService.createService(JsonApi.class);
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Long, Post> callback) {
        final long page = 1;
        Call<List<Post>> data = jsonApi.getPostList(page);
        data.enqueue(new Callback<List<Post>>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> postsList = response.body();
                if (postsList != null) {
                    callback.onResult(postsList, null, page);
                } else {
                    Log.d(TAG, "onResponse: NO POSTS");
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
            }
        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Post> callback) {

    }

    @SuppressLint("LongLogTag")
    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Post> callback) {
        final long page = params.key;
        System.out.println("----------------- PAGE NUMBER IS : " + page + " -------------------------");
        Call<List<Post>> data = jsonApi.getPostList(page);
        data.enqueue(new Callback<List<Post>>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> postList = response.body();
                if (postList != null) {
                    callback.onResult(postList, page + 1);
                } else {
                    Log.d(TAG, "onResponse: NO POSTS");
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.d(TAG, "onFailure: NO POSTS");
            }
        });
    }
}
