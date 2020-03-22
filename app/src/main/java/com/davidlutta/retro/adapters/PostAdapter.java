package com.davidlutta.retro.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

import com.davidlutta.retro.R;
import com.davidlutta.retro.model.Post;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostAdapter extends PagedListAdapter<Post, BaseViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private OnPostListener postListener;

    public PostAdapter(OnPostListener postListener) {
        super(Post.CALLBACK);
        this.postListener = postListener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new PostViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false),
                        postListener
                );
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false)
                );
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == Objects.requireNonNull(getCurrentList()).size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(getItem(position));
    }

    public Post getSelectedPost(int postion) {
        if (getCurrentList() != null) {
            if (getCurrentList().size() > 0) {
                return getItem(postion);
            }
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

    public class ProgressHolder extends BaseViewHolder {
        public ProgressHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {

        }
    }
}
