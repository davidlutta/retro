package com.davidlutta.retro.persistance;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.davidlutta.retro.model.Post;

@Database(entities = {Post.class}, version = 1)
public abstract class PostsDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "PostsDatabase";
    private static PostsDatabase instance;

    public static PostsDatabase getInstance(final Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    PostsDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract PostsDao getPostsDao();
}
