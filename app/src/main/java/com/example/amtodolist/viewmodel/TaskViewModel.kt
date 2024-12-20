package com.example.amtodolist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amtodolist.models.TaskEntity
import com.example.amtodolist.models.TaskItem
import com.example.amtodolist.repositories.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class TaskViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel(){
    private val tasksWithoutDeleteTasks = ArrayList<TaskItem>()
    private var taskItems = ArrayList<TaskItem>()
    val mTaskItemsLiveData = MutableLiveData<ArrayList<TaskItem>>()

    var selectedTask : TaskItem? = null
    val mSelectedItem = MutableLiveData<TaskItem?>()

    enum class TASK_STATE {
        NOT_DONE, //the state of the tasks are undone - need to show done button
        DONE, //the state of the selected tasks are done - need to show undone button
        NONE // the user selected both done and undone tasks - hide both options - the selection needs to be deterministic
    }

    fun getCompletedTasksStates() : TASK_STATE{

        val selectedList = taskItems.filter { it.selected }
        val completedList = selectedList.filter {it.completed}
        val unCompletedList = selectedList.filter { !it.completed }
        if(selectedList.size == completedList.size) return TASK_STATE.DONE
        else if(selectedList.size == unCompletedList.size) return TASK_STATE.NOT_DONE
        return TASK_STATE.NONE
    }

    //used for backpress in case the user wants to exit edit mode
    fun isTasksSelected() = taskItems.any { it.selected }

    fun resetSelectedTasks(){
        val tempList = taskItems
        for(task in tempList){
            task.selected = false
        }
        taskItems = tempList
        mTaskItemsLiveData.value = taskItems
    }

    fun clearSelection(){
        selectedTask = null
        mSelectedItem.value = null
    }

    fun setSelectedTask(index : Int){
        if(index != -1) {
            selectedTask = taskItems[index]
        } else {
            selectedTask = null
        }
        mSelectedItem.value = selectedTask
    }

    fun getTasks() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            taskItems.clear()
            repository.getTasks().map { taskEntity ->
                val id = taskEntity.id
                val title = taskEntity.title
                val description = taskEntity.description
                val timestampStr = getTimestampStr(taskEntity.timestamp)
                val completed = taskEntity.completed
                taskItems.add(
                    TaskItem(
                        id,
                        title,
                        description,
                        completed,
                        selected = false,
                        timestampStr = timestampStr
                    )
                )
            }
            withContext(Dispatchers.Main) {
                mTaskItemsLiveData.value = taskItems
            }
        }
    }

    fun isTasksInEditMode(): Boolean{
        for(task in taskItems){
            if(task.selected)
                return true
        }
        return false
    }

    fun setSelectedTaskState(index: Int, selected: Boolean){
        if(index >=0 && index < taskItems.size){
            taskItems[index].selected = selected
        }
    }

    fun getSelectedTaskState(index: Int) :Boolean{
        if(index >=0 && index < taskItems.size){
            return taskItems[index].selected
        }
        return false
    }

    fun updateTask(taskItem: TaskItem) = viewModelScope.launch {
        withContext(Dispatchers.IO){
            repository.updateTask(TaskEntity(id=taskItem.id, title = taskItem.title, description = taskItem.description, timestamp = System.currentTimeMillis()))
        }
    }

    private fun getTimestampStr(timestamp: Long): String {
        val df = SimpleDateFormat("MM/dd/yy hh:mm a", Locale.US)
        return df.format(Date(timestamp))
    }

    fun insertTask(taskItem: TaskItem) = viewModelScope.launch {
        withContext(Dispatchers.IO){
            repository.insertTask(TaskEntity(title = taskItem.title, description = taskItem.description))
        }
    }

    fun updateSelectedTasks(state: Boolean) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val currentUpdatedTasksTimestamp = System.currentTimeMillis()
            for(task in taskItems) {
                if(task.selected) {
                    task.selected = false //reset selected state
                    task.completed = state //we need to update to newer state once we finish updating the db
                    task.timestamp = currentUpdatedTasksTimestamp
                    task.timestampStr = getTimestampStr(currentUpdatedTasksTimestamp)
                    repository.updateTask(
                        TaskEntity(
                            id = task.id,
                            title = task.title,
                            description = task.description,
                            completed = state,
                            timestamp = currentUpdatedTasksTimestamp
                        )
                    )
                }
            }
        }
    }

    fun updateTasks() {
        mTaskItemsLiveData.value = taskItems
    }

    fun prepTasksForDelete(){

        //we want to store only the tasks that are not for deletion - render them, before the actual deletion will start
        //if the user will click on undo we will render the original list with all tasks
        tasksWithoutDeleteTasks.clear()
        for(task in taskItems){
            if(!task.selected){
                tasksWithoutDeleteTasks.add(task)
            }
        }
        mTaskItemsLiveData.value = tasksWithoutDeleteTasks
    }

    //if user decided to click on undo after he clicked on delete - we need to restore tasks
    fun rollBackTasks(){
        //init selection state
        for(task in taskItems){
            if(task.selected) task.selected = false
        }
        mTaskItemsLiveData.value = taskItems
    }

    fun deleteTasks() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                for (task in taskItems) {
                    if(task.selected)
                        repository.delete(TaskEntity(id = task.id))
                }
                taskItems.clear()
                taskItems.addAll(tasksWithoutDeleteTasks)
            }
        }
    }

}