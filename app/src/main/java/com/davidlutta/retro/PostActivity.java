package com.davidlutta.retro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.davidlutta.retro.model.Post;
import com.davidlutta.retro.viewmodels.PostViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostActivity extends AppCompatActivity {
    @BindView(R.id.postTitleTextView)
    TextView postTitleTextView;
    @BindView(R.id.postBodyTextView)
    TextView postBodyTextView;
    private PostViewModel mPostViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        mPostViewModel = ViewModelProviders.of(this).get(PostViewModel.class);
        subscribeObservers();
    }

    private void subscribeObservers() {
        if (getIntent().hasExtra("postID")) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            String postId = null;
            if (bundle != null) {
                postId = bundle.getString("postID");
            }
            mPostViewModel.getPost(postId).observe(this, new Observer<Post>() {
                @Override
                public void onChanged(Post post) {
                    setPostProps(post);
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
