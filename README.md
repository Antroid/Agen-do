Implemented with the following technologies:
1. Kotlin
2. Room DB
3. Kotlin Coroutines
4. MVVM design pattern for using viewmodels
5. XML to build the app ui elements
6. Used Hilt for dependency injection (used for injecting repository into the viewmodal)
7. Created DAO entity to describe a task
   @Entity(tableName = "task_table") data class TaskEntity
   ( @PrimaryKey(autoGenerate = true)
   val id: Int = 0,
   val title : String="",
   val description : String="",
   val completed : Boolean = false,
   val timestamp : Long = System.currentTimeMillis() )
8. Used LiveData to handle the ui updates from logic to the interface

Optional Enhancements: 
1. Dark theme 
2. Add undo functionality for deleted tasks.

How to Build:
1. Developed in latest (at this time) android studio (LadyBug version)
2. Used Kotlin v2.0.21
3. Clone project from GitHub
4. Open the project in Android Studio and you need to be able to run it from there.

How to use:
1. Add new task
 * Click on '+' at the bottom of the screen to enter add new task screen
 * Once finished click save and you will return to the main screen (todo list) with the new task
2. Update exiting task
 * Click on a task to enter the update mode screen of edit a task
 * Once finsih click update and you will return to the main screen (todo list) with the updaetd task, the timestamp will be updated as well
3. Selecting tasks
 -   The user can long press on a task to open a context menu (support multiple selection)
 -   Once in this mode, you will see the following options in the context menu
 -      Delete 
 -      Complete - if the user select tasks which are not completed, click on complete icon to apply the changes, the timestamp will be updated as well
 -      Not completed - if the user select tasks which are completed, click on not complete icon to apply the changes, the timestamp will be updated as well
 -      (*) If the user selected both completed and uncompleted tasks complete and no completed options will be hidden
4. Delete tasks
 -      Long press on tasks you wish to delete
 -      Press delete in the context menu, the list of tasks will be updated accordingly
 -      You will see a snackbar menu with an option to undo deletion
 -         If you press on undo, the original list will apper back
 -         If you will wait till the snackbar will be dismissed the selected tasks for deletion will be permanently deleted
5. Exit from selection mode
 -    Long press a task/tasks
 -    Press onback to exit from the selection mode
