package com.example.progetto.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.progetto.CardItem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {CardItem.class}, version = 1)
public abstract class CardItemDatabase extends RoomDatabase {
    public abstract CardItemDAO cardItemDAO();

    private static volatile CardItemDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static CardItemDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CardItemDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CardItemDatabase.class, "word_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
