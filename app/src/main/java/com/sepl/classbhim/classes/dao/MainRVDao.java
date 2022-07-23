package com.sepl.classbhim.classes.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sepl.classbhim.classes.models.MainRvModel;

import java.util.List;

@Dao
public interface MainRVDao {

    @Query("SELECT * FROM mainrvmodel")
    List<MainRvModel> getAllClasses();

    @Insert
    void insertClass(MainRvModel... mainRvModels);

    @Delete
    void deleteClass(MainRvModel mainRVModel);

    @Query("UPDATE mainrvmodel SET status = :status WHERE requestId =:id")
    void updateStatus (int status, String id);

    @Query("DELETE FROM mainrvmodel")
    void deleteAll();
}
