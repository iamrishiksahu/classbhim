package com.sepl.classbhim.classes.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
public class NotificationModel {

    @PrimaryKey(autoGenerate = true)
    public int notificationId;

    @ColumnInfo(name = "notificationTitle")
    public String title;

    @ColumnInfo(name = "notificationBody")
    public String body;

    @ColumnInfo(name = "classNumber")
    public int classNumber;

    @ColumnInfo(name = "author")
    public String author;


    @ColumnInfo(name = "notificationArrivalTime")
    public String dateValue;


    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(int classNumber) {
        this.classNumber = classNumber;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDateValue() {
        return dateValue;
    }

    public void setDateValue(String dateValue) {
        this.dateValue = dateValue;
    }

    private String getTime (){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyy 'at' h:mm a");
        return String.valueOf(format.format(calendar.getTime()));
    }
}
