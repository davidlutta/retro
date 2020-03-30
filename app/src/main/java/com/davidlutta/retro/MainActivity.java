package com.davidlutta.retro;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.davidlutta.retro.adapters.OnPostListener;
import com.davidlutta.retro.adapters.PostAdapter;
import com.davidlutta.retro.model.Post;
import com.davidlutta.retro.networking.NetworkChangeReceiver;
import com.davidlutta.retro.viewmodels.PostsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnPostListener {

    PostAdapter postAdapter;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    private BroadcastReceiver mNetworkReceiver;
    PostsViewModel postsViewModel;
    private String TAG = "com.davidlutta.retro.MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpBroadcastReceiver();
        postsViewModel = ViewModelProviders.of(this).get(PostsViewModel.class);
        setUpRecyclerView();
        subscribeObservers();
    }

    private void subscribeObservers() {
        postsViewModel.getPagedListLiveData().observe(this, new Observer<PagedList<Post>>() {
            @Override
            public void onChanged(PagedList<Post> posts) {
                postAdapter.submitList(posts);
            }
        });
    }

    private void setUpBroadcastReceiver() {
        mNetworkReceiver = new NetworkChangeReceiver();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    private void setUpRecyclerView() {
        if (postAdapter == null) {
            postAdapter = new PostAdapter(this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(postAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(true);
        }
    }

    @Override
    public void onRecipeClick(int position) {
        Intent intent = new Intent(MainActivity.this, PostActivity.class);
        Post post = postAdapter.getSelectedPost(position);
        String id = post.getId().toString();
        intent.putExtra("postID", id);
        startActivity(intent);
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }
}
