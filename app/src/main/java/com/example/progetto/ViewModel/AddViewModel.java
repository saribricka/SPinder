package com.example.progetto.ViewModel;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.progetto.CardItem;
import com.example.progetto.Database.CardItemRepository;

public class AddViewModel extends AndroidViewModel {
    
    private final MutableLiveData<Bitmap> imageBitmpap = new MutableLiveData<>();

    private CardItemRepository repository;

    public AddViewModel(@NonNull Application application) {
        super(application);
        repository = new CardItemRepository(application);
    }

    public void setImageBitmpap(Bitmap bitmpap) {
        imageBitmpap.setValue(bitmpap);
    }

    public LiveData<Bitmap> getBitmap() {
        return imageBitmpap;
    }

    public void addCardItem(CardItem item){
        repository.addCardItem(item);
    }
}
