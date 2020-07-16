package com.davidlutta.retro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.davidlutta.retro.model.Post;
import com.davidlutta.retro.util.Resource;
import com.davidlutta.retro.util.SharedPref;
import com.davidlutta.retro.viewmodels.PostViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostActivity extends AppCompatActivity {
    private static final String TAG = "PostActivity";
    @BindView(R.id.postTitleTextView)
    TextView postTitleTextView;
    @BindView(R.id.postBodyTextView)
    TextView postBodyTextView;
    private PostViewModel mPostViewModel;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppearance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        mPostViewModel = ViewModelProviders.of(this).get(PostViewModel.class);
        subscribeObservers();
    }

    private void setAppearance() {
        sharedPref = new SharedPref(this);
        if (sharedPref.getNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
    }


    private void subscribeObservers() {
        if (getIntent().hasExtra("postID")) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            String postId = null;
            if (bundle != null) {
                postId = bundle.getString("postID");
            }
            /*mPostViewModel.getPost(postId).observe(this, new Observer<Post>() {
                @Override
                public void onChanged(Post post) {
                    setPostProps(post);
                }
            });*/
            mPostViewModel.getPost(postId).observe(this, new Observer<Resource<Post>>() {
                @Override
                public void onChanged(Resource<Post> postResource) {
                    if (postResource != null) {
                        if (postResource.data != null) {
                            switch (postResource.status) {
                                case LOADING:
                                    Toast.makeText(PostActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
                                    break;
                                case SUCCESS:
                                    Log.d(TAG, "onChanged: Cache Refreshed");
                                    setPostProps(postResource.data);
                                    break;
                                case ERROR:
                                    Log.d(TAG, "onChanged: ERROR: Title" + postResource.data.getTitle());
                                    Log.d(TAG, "onChanged: ERROR: message" + postResource.message);
                                    Toast.makeText(PostActivity.this, postResource.message, Toast.LENGTH_SHORT).show();
                                    setPostProps(postResource.data);
                                    break;
                            }
                        }
                    }
                }
            });
        }
    }

    private void setPostProps(Post post) {
        if (post != null) {
            postTitleTextView.setText(post.getTitle());
            postBodyTextView.setText(post.getBody());
        } else {
            postTitleTextView.setText("Loading...");
            postBodyTextView.setText("Loading..");
        }
    }
}
