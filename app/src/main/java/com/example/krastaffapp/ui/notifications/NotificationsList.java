package com.example.krastaffapp.ui.notifications;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="notifications")
public class NotificationsList {
    @PrimaryKey
    @NonNull
    private String id = "";

    @ColumnInfo(name = "nTitle")
    private String Ntitle;

    @ColumnInfo(name = "nMessage")
    private String Nmessage;

    @ColumnInfo(name = "nTime")
    private String Ntime;

    @ColumnInfo(name = "nRead")
    private String Nread;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getNtitle() {
        return Ntitle;
    }
    public void setNtitle(String Ntitle) {
        this.Ntitle = Ntitle;
    }


    public String getNmessage() { return Nmessage; }
    public void setNmessage(String Nmessage) {
        this.Nmessage = Nmessage;
    }


    public String getNtime() {
        return Ntime;
    }
    public void setNtime(String Ntime) {
        this.Ntime = Ntime;
    }


    public String getNread(){ return Nread; }
    public void setNread(String Nread){ this.Nread = Nread; }


}
