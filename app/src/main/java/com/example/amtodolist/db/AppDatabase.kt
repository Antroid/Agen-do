package com.example.amtodolist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.amtodolist.models.TaskEntity

@Database(entities = [TaskEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getTaskDao(): TaskDao
}