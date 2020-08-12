package com.example.voice;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class RoomViewModel extends AndroidViewModel {

    private RoomRepository repository;
    private LiveData<List<RoomModel>> roomData;

    public RoomViewModel(@NonNull Application application) {
        super(application);
        repository = new RoomRepository(application);
    }

    public LiveData<List<RoomModel>> getRoomData(){
        if (roomData == null){
            roomData = repository.getAllLiveData();
        }
        return roomData;
    }

    public void insertData(RoomModel roomModel){
        repository.insertData(roomModel);
    }
}
