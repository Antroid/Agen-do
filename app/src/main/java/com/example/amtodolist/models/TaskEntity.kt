package com.example.amtodolist.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class TaskEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title : String="",
    val description : String="",
    val completed : Boolean = false,
    val timestamp : Long = System.currentTimeMillis()
)