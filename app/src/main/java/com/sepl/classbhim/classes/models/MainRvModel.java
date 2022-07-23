package com.sepl.classbhim.classes.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MainRvModel {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "schoolName")
    public String schoolName;

    @ColumnInfo(name = "classID")
    public String classId;

    @ColumnInfo(name = "className")
    public String className;

    @ColumnInfo(name = "status")
    public int status;

    @ColumnInfo(name = "requestId")
    public String requestId;

    public MainRvModel(String schoolName, String className,int status,  String requestId, String classId) {
        this.schoolName = schoolName;
        this.classId = classId;
        this.className = className;
        this.status = status;
        this.requestId = requestId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getClassName() {
        return className;
    }

    public int getStatus() {
        return status;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
