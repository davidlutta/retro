package com.davidlutta.retro.Api;

import com.davidlutta.retro.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonApi {
    @GET("/posts")
    Call<List<Post>> getPostList(@Query("userId") long userId);

    @GET("/posts/{id}")
    Call<Post> getPost(@Path("id") String postId);
}
