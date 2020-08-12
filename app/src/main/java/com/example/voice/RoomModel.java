package com.example.voice;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RoomModel {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "Identifier")
    private String identifier;

    @ColumnInfo(name = "path")
    private String path;

    @NonNull
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(@NonNull String identifier) {
        this.identifier = identifier;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "RoomModel{" +
                "identifier='" + identifier + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
