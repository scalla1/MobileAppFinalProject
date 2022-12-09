package com.mobilapp.geotagging;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

//Dao = data access object  or how the rest of the app interacts with the db
@Dao
public interface TagDao {
    @Query("SELECT * FROM Tag")
    List<Tag> getAll();

    @Query("SELECT * FROM Tag WHERE tid IN (:id)")
    Tag getTagByID(int id);

    @Insert
    public void insertNewTag(Tag tag);

    @Query("DELETE FROM Tag WHERE tid IN (:id)")
    public void deleteTagAtID(int id);

}