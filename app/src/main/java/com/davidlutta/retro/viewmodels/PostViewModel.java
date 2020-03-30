package com.davidlutta.retro.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.davidlutta.retro.networking.JsonRepository;
import com.davidlutta.retro.model.Post;

public class PostViewModel extends ViewModel {
    private JsonRepository jsonRepository;
    private boolean didReceiveRecipe;

    public PostViewModel() {
        jsonRepository = JsonRepository.getInstance();
        didReceiveRecipe = false;
    }

    public LiveData<Post> getPost(String recipeId) {
        return jsonRepository.getSinglePost(recipeId);
    }
}
