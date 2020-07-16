package com.davidlutta.retro.persistance;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.davidlutta.retro.model.Post;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PostsDao {
    @Insert(onConflict = IGNORE)
    long[] insertPosts(Post... posts);

    @Insert(onConflict = REPLACE)
    void insertPost(Post post);

    @Query("SELECT * FROM posts")
    LiveData<List<Post>> getPosts();

    @Query("UPDATE posts SET userId = :userId, title = :title, body = :body WHERE id = :id")
    void updatePost(String userId, String id, String title, String body);

    @Query("SELECT * FROM posts WHERE id = :id")
    LiveData<Post> getPost(String id);
}
