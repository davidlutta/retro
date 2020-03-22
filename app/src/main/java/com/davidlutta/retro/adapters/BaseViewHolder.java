package com.davidlutta.retro.adapters;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.davidlutta.retro.model.Post;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    private int mCurrentPosition;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    protected abstract void clear();

    public void onBind(Post item) {
        clear();
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

}
