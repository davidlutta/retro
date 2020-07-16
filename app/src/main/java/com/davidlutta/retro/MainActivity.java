package com.davidlutta.retro;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.davidlutta.retro.adapters.OnPostListener;
import com.davidlutta.retro.adapters.PostAdapter;
import com.davidlutta.retro.adapters.PostAdapter2;
import com.davidlutta.retro.model.Post;
import com.davidlutta.retro.networking.NetworkChangeReceiver;
import com.davidlutta.retro.util.SharedPref;
import com.davidlutta.retro.viewmodels.PostsViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnPostListener {
    public static final String OWNER = "MainActivity";
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";
    private static String LIST_STATE = "list_state";
    PostAdapter postAdapter;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    PostsViewModel postsViewModel;
    SharedPref sharedPref;
    LinearLayoutManager mLayoutManager;
    Parcelable state;
    private String TAG = "com.davidlutta.retro.MainActivity";
    private BroadcastReceiver mNetworkReceiver;
    private int scrollPosition = 0;
    private boolean shouldKeepScrollPosition = true;
    private Parcelable savedRecyclerLayoutState;
    private PostAdapter2 postAdapter2;
    private List<Post> postList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppearance();
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        }
        setContentView(R.layout.activity_main);
        mLayoutManager = new LinearLayoutManager(this);
        ButterKnife.bind(this);
        postsViewModel = ViewModelProviders.of(this).get(PostsViewModel.class);
        postsViewModel.fetchPosts(OWNER);
        setUpBroadcastReceiver();
//        setUpRecyclerView();
        subscribeObservers();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable(LIST_STATE, mLayoutManager.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        state = savedInstanceState.getParcelable(LIST_STATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAppearance() {
        sharedPref = new SharedPref(this);
        if (sharedPref.getNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
    }

    private void subscribeObservers() {
        /*postsViewModel.getPagedListLiveData().observe(this, new Observer<PagedList<Post>>() {
            @Override
            public void onChanged(PagedList<Post> posts) {
                postAdapter.submitList(posts);
            }
        });*/

        postsViewModel.getPosts().observe(this, (listResource) -> {
            if (listResource != null) {
                Log.d(TAG, "subscribeObservers: OnChanged: Status:" + listResource.status);
                if (listResource.data != null) {
                    switch (listResource.status) {
                        case LOADING:
                            if (listResource.data.size() != 0) {
                                Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case SUCCESS:
                            Log.d(TAG, "subscribeObservers: onChanged: Cache has been refreshed !");
                            postList = listResource.data;
                            setUpRecyclerView();
                            break;
                        case ERROR:
                            Log.d(TAG, "subscribeObservers: OnChanged Error: Cache cannot be refreshed");
                            if (listResource.data.size() == 0) {
                                Log.d(TAG, "subscribeObservers: OnChanged Error: #Posts: " + listResource.data.size());
                                Toast.makeText(this, listResource.message, Toast.LENGTH_SHORT).show();
                            }
                            postList = listResource.data;
                            setUpRecyclerView();
                            break;
                    }
                }
            }
        });
    }

    private void setUpBroadcastReceiver() {
        mNetworkReceiver = new NetworkChangeReceiver();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    // TODO: 4/1/20 Create a way to restore recycler view position
    private void setUpRecyclerView() {
        /*if (postAdapter == null) {
            postAdapter = new PostAdapter(this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setSaveEnabled(true);
            recyclerView.setAdapter(postAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(true);
        }*/
        if (postAdapter2 == null) {
            postAdapter2 = new PostAdapter2(postList, this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(postAdapter2);
        }
    }

    @Override
    public void onRecipeClick(int position) {
        Intent intent = new Intent(MainActivity.this, PostActivity.class);
//        Post post = postAdapter.getSelectedPost(position);
        Post post = postAdapter2.getPost(position);
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
