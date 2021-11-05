package com.example.krastaffapp.ui.notifications;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities={NotificationsList.class}, version = 5, exportSchema = false)

public abstract class NotificationsDB  extends RoomDatabase {
    public abstract NotificationsDAO notificationsDAO();
}
