package com.example.voice;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class RoomRepository {

    private RoomDao roomDao;
    private LiveData<List<RoomModel>> roomModel;

    public RoomRepository(Application application){
        AppDatabase database = AppDatabase.getAppDatabase(application);
        roomDao = database.roomDao();
        roomModel = roomDao.getAllRoomData();
    }

    public LiveData<List<RoomModel>> getAllLiveData(){
        if (roomModel == null){
            roomModel = roomDao.getAllRoomData();
        }
        return roomModel;
    }

    public void insertData(RoomModel roomModel){
        new InsertAsyncTask(roomDao).execute(roomModel);
    }

    private static class InsertAsyncTask extends AsyncTask<RoomModel, Void, Void> {
        private RoomDao mDao;

        InsertAsyncTask(RoomDao mDao){
            this.mDao = mDao;
        }

        @Override
        protected Void doInBackground(RoomModel... roomModels) {
            mDao.insertData(roomModels[0]);
            return null;
        }
    }
}
