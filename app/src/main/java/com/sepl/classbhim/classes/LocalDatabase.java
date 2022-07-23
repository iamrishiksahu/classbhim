package com.sepl.classbhim.classes;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sepl.classbhim.classes.dao.MainRVDao;
import com.sepl.classbhim.classes.dao.NotificationDao;
import com.sepl.classbhim.classes.models.MainRvModel;
import com.sepl.classbhim.classes.models.NotificationModel;

@Database(entities = {NotificationModel.class, MainRvModel.class}, version =  1)
public abstract class LocalDatabase extends RoomDatabase {

    public abstract NotificationDao notificationDao();
    public abstract MainRVDao mainRvDao();

    private static LocalDatabase INSTANCE;

    public static LocalDatabase getDbInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), LocalDatabase.class, "LOCAL_DATABASE")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}

