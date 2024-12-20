package com.example.amtodolist.repositories

import com.example.amtodolist.db.TaskDao
import com.example.amtodolist.models.TaskEntity
import javax.inject.Inject


class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

    fun getTasks(): List<TaskEntity> = taskDao.getTasks()

    suspend fun insertTask(taskItem: TaskEntity) = taskDao.insertTask(taskItem)

    suspend fun delete(taskItem: TaskEntity) = taskDao.deleteTask(taskItem)

    suspend fun updateTask(taskItem: TaskEntity) = taskDao.updateTask(taskItem)
}