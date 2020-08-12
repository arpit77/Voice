package com.example.voice;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface RoomDao {

    @Query("Select * from RoomModel")
    LiveData<List<RoomModel>> getAllRoomData();

    @Query("Select * from RoomModel")
    List<RoomModel> getRoomData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(RoomModel roomModel);

}
