package com.example.progetto.Database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.progetto.CardItem;

import java.util.List;

public class CardItemRepository {
    private CardItemDAO cardItemDAO;
    private LiveData<List<CardItem>> cardItemList;

    public CardItemRepository(Application application) {
        CardItemDatabase db = CardItemDatabase.getDatabase(application);
        cardItemDAO = db.cardItemDAO();
        cardItemList = cardItemDAO.getCardItems();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<CardItem>> getCardItemList(){
        return cardItemList;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void addCardItem(final CardItem CardItem) {
        CardItemDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                cardItemDAO.addCardItem(CardItem);
            }
        });
    }
}
