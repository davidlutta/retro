package com.davidlutta.retro.networking;

import androidx.lifecycle.MutableLiveData;

import com.davidlutta.retro.Api.JsonApi;
import com.davidlutta.retro.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JsonRepository {
    private static JsonRepository newsRepository;

    public static JsonRepository getInstance() {
        if (newsRepository == null) {
            newsRepository = new JsonRepository();
        }
        return newsRepository;
    }

    private JsonApi jsonApi;

    public JsonRepository() {
        jsonApi = RetrofitService.createService(JsonApi.class);
    }

    public MutableLiveData<List<Post>> getPostList() {
        final MutableLiveData<List<Post>> postsData = new MutableLiveData<>();
        /*jsonApi.getPostList().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    postsData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                postsData.setValue(null);
            }
        });*/
        return postsData;
    }

    public MutableLiveData<Post> getSinglePost(String id) {
        final MutableLiveData<Post> singlePostData = new MutableLiveData<>();
        jsonApi.getPost(id).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                System.out.println("----------------------- URL IS : " + response.raw().request().url() + " -------------------");
                if (response.isSuccessful()) {
                    singlePostData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                singlePostData.setValue(null);
            }
        });
        return singlePostData;
    }
}
