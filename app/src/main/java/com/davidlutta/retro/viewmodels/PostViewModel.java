package com.davidlutta.retro.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.davidlutta.retro.networking.JsonRepository;
import com.davidlutta.retro.model.Post;
import com.davidlutta.retro.util.Resource;

public class PostViewModel extends AndroidViewModel {
    private JsonRepository jsonRepository;

    public PostViewModel(Application application) {
        super(application);
        jsonRepository = JsonRepository.getInstance(application);
    }

    public LiveData<Resource<Post>> getPost(String recipeId) {
        return jsonRepository.getSinglePost(recipeId);
    }
}
