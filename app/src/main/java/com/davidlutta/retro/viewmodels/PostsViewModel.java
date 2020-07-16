package com.davidlutta.retro.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.davidlutta.retro.datasource.PostDataSource;
import com.davidlutta.retro.datasource.PostDataSourceFactory;
import com.davidlutta.retro.model.Post;
import com.davidlutta.retro.networking.JsonRepository;
import com.davidlutta.retro.util.Resource;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PostsViewModel extends AndroidViewModel {
    private static final String TAG = "PostsViewModel";
    /*PostDataSourceFactory postDataSourceFactory;
    MutableLiveData<PostDataSource> dataSourceMutableLiveData;
    Executor executor;
    LiveData<PagedList<Post>> pagedListLiveData;*/

    private JsonRepository jsonRepository;
    private MediatorLiveData<Resource<List<Post>>> mPosts = new MediatorLiveData<>();

    public PostsViewModel(@NonNull Application application) {
        super(application);
        jsonRepository = JsonRepository.getInstance(application);

        /*postDataSourceFactory = new PostDataSourceFactory();
        dataSourceMutableLiveData = postDataSourceFactory.getMutableLiveData();
        PagedList.Config config = (new PagedList.Config.Builder())
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(10)
                .setPageSize(20)
                .setPrefetchDistance(4)
                .build();
        executor = Executors.newFixedThreadPool(5);

        pagedListLiveData = new LivePagedListBuilder<Long, Post>(postDataSourceFactory, config)
                .setFetchExecutor(executor)
                .build();*/

    }

    public LiveData<Resource<List<Post>>> getPosts() {
        return mPosts;
    }

    public void fetchPosts(String owner) {
        getData(owner);
    }

    private void getData(String owner) {
        final LiveData<Resource<List<Post>>> repoSource = jsonRepository.getPostList(owner);
        mPosts.addSource(repoSource, listResource -> {
            if (listResource != null) {

                mPosts.setValue(listResource);
                if (listResource.status == Resource.Status.SUCCESS) {
                    if (listResource.data != null) {
                        if (listResource.data.size() == 0) {
                            Log.d(TAG, "getData: ===================== Exhausted =====================");
                        }
                    }
                    mPosts.removeSource(repoSource);
                } else if (listResource.status == Resource.Status.ERROR) {
                    mPosts.removeSource(repoSource);
                }
            }
        });
    }

    /*public LiveData<PagedList<Post>> getPagedListLiveData() {
        if (pagedListLiveData != null) {
            return pagedListLiveData;
        } else {
            return null;
        }
    }*/

}
