package com.example.krastaffapp.ui.notifications;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NotificationsDAO  {
    @Insert
    public void addData(NotificationsList notificationsList);

    @Query("select * from notifications ORDER BY nTime DESC")
    public List<NotificationsList> getMyData();


    @Delete
    public void delete(NotificationsList notificationsList);

    @Update
    public void update(NotificationsList notificationsList);

    @Query("update notifications set nRead =:opened WHERE id =:nId")
    void updateNotif(String opened,String nId);


}