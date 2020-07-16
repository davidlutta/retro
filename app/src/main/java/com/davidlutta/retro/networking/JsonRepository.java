package com.davidlutta.retro.networking;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.davidlutta.retro.Api.ApiResponse;
import com.davidlutta.retro.Api.JsonApi;
import com.davidlutta.retro.model.Post;
import com.davidlutta.retro.persistance.PostsDao;
import com.davidlutta.retro.persistance.PostsDatabase;
import com.davidlutta.retro.util.AppExecutors;
import com.davidlutta.retro.util.NetworkBoundResource;
import com.davidlutta.retro.util.RateLimiter;
import com.davidlutta.retro.util.Resource;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class JsonRepository {
    private static final String TAG = "JsonRepository";
    private static JsonRepository newsRepository;
    private PostsDao postsDao;
    private JsonApi jsonApi;
    private RateLimiter<String> postListLimit = new RateLimiter<>(10, TimeUnit.MINUTES); // Will try to ReFetch data after 10 mins

    public JsonRepository(Context context) {
        jsonApi = RetrofitService.createService(JsonApi.class);
        postsDao = PostsDatabase.getInstance(context).getPostsDao();
    }

    public static JsonRepository getInstance(Context context) {
        if (newsRepository == null) {
            newsRepository = new JsonRepository(context);
        }
        return newsRepository;
    }

/*    public MutableLiveData<List<Post>> getPostList() {
        final MutableLiveData<List<Post>> postsData = new MutableLiveData<>();
        jsonApi.getPostList().enqueue(new Callback<List<Post>>() {
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
        });
        return postsData;
    }*/

    public LiveData<Resource<List<Post>>> getPostList(String owner) {
        return new NetworkBoundResource<List<Post>, List<Post>>(AppExecutors.getInstance()) {
            @Override
            public void saveCallResult(@NonNull List<Post> item) {
                if (item.size() > 0) {
                    Post[] posts = new Post[item.size()];
                    int index = 0;
                    for (long rowId : postsDao.insertPosts((Post[]) (item.toArray(posts)))) {
                        if (rowId == -1) { // conflict detected
                            postsDao.updatePost(
                                    posts[index].getUserId().toString(),
                                    posts[index].getId().toString(),
                                    posts[index].getTitle(),
                                    posts[index].getBody()
                            );
                        }
                        index++;
                    }
                }
            }

            @Override
            public boolean shouldFetch(@Nullable List<Post> data) {
//                return true; // always query the network since the queries can be anything
                return data == null || data.isEmpty() || postListLimit.shouldFetch(owner);
            }

            @NonNull
            @Override
            public LiveData<List<Post>> loadFromDb() {
                return postsDao.getPosts();
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<List<Post>>> createCall() {
                return jsonApi.getPostList();
            }

            @Override
            public void onFetchFailed() {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<Post>> getSinglePost(String id) {
        return new NetworkBoundResource<Post, Post>(AppExecutors.getInstance()) {
            @Override
            public void saveCallResult(@NonNull Post item) {
                postsDao.insertPost(item);
            }

            @Override
            public boolean shouldFetch(@Nullable Post data) {
                return data == null;
            }

            @NonNull
            @Override
            public LiveData<Post> loadFromDb() {
                return postsDao.getPost(id);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<Post>> createCall() {
                return jsonApi.getPost(id);
            }

            @Override
            public void onFetchFailed() {

            }
        }.getAsLiveData();
    }

    /*public MutableLiveData<Post> getSinglePost(String id) {
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
    }*/
}
