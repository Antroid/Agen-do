package com.example.amtodolist.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.amtodolist.models.TaskEntity

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task : TaskEntity)

    @Query("SELECT * FROM task_table order by completed,timestamp")
    fun getTasks(): List<TaskEntity>

    @Delete
    suspend fun deleteTask(taskItem: TaskEntity)

    @Update
    suspend fun updateTask(taskItem : TaskEntity)

}