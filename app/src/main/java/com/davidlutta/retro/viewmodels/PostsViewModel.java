package com.davidlutta.retro.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.davidlutta.retro.datasource.PostDataSource;
import com.davidlutta.retro.datasource.PostDataSourceFactory;
import com.davidlutta.retro.model.Post;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PostsViewModel extends AndroidViewModel {

    PostDataSourceFactory postDataSourceFactory;
    MutableLiveData<PostDataSource> dataSourceMutableLiveData;
    Executor executor;
    LiveData<PagedList<Post>> pagedListLiveData;

    public PostsViewModel(@NonNull Application application) {
        super(application);
        postDataSourceFactory = new PostDataSourceFactory();
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
                .build();

    }

    public LiveData<PagedList<Post>> getPagedListLiveData() {
        if (pagedListLiveData != null) {
            return pagedListLiveData;
        } else {
            return null;
        }
    }

}
