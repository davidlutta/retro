package com.davidlutta.retro.datasource;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class PostDataSourceFactory extends DataSource.Factory {
    private static final String TAG = "com.davidlutta.retro.model.PhotoDataSourceFactory";
    PostDataSource postDataSource;
    MutableLiveData<PostDataSource> mutableLiveData;

    public PostDataSourceFactory() {
        mutableLiveData = new MutableLiveData<>();
    }

    @SuppressLint("LongLogTag")
    @Override
    public DataSource create() {
        postDataSource = new PostDataSource();
        Log.d(TAG, "create: " + postDataSource);
        mutableLiveData.postValue(postDataSource);
        return postDataSource;
    }

    public MutableLiveData<PostDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
