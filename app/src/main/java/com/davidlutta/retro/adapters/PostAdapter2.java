package com.davidlutta.retro.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.davidlutta.retro.R;
import com.davidlutta.retro.model.Post;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostAdapter2 extends RecyclerView.Adapter<PostAdapter2.PostViewHolder> {
    private List<Post> postList;
    private OnPostListener postListener;

    public PostAdapter2(List<Post> postList, OnPostListener postListener) {
        this.postList = postList;
        this.postListener = postListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false), postListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = getPost(position);
        if (post != null) {
            holder.onBind(post);
        }
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    public Post getPost(int index) {
        if (postList.size() > 0) {
            return postList.get(index);
        }
        return null;
    }

    public class PostViewHolder extends BaseViewHolder implements View.OnClickListener {
        @BindView(R.id.titleTextView)
        TextView postTitle;
        @BindView(R.id.bodyTextView)
        TextView postBody;
        @BindView(R.id.userIdTextView)
        TextView userId;

        OnPostListener postListener;

        PostViewHolder(@NonNull View itemView, OnPostListener onPostListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.postListener = onPostListener;
            itemView.setOnClickListener(this);
        }

        @Override
        protected void clear() {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBind(Post item) {
            if (item != null) {
                postTitle.setText(item.getTitle());
                postBody.setText(item.getBody());
                userId.setText("userID: " + item.getUserId().toString());
            } else {
                postTitle.setText("Loading..");
                postBody.setText("Loading...");
                userId.setText("Loading");
            }
        }

        @Override
        public int getCurrentPosition() {
            return super.getCurrentPosition();
        }

        @Override
        public void onClick(View v) {
            postListener.onRecipeClick(getAdapterPosition());
        }
    }
}
