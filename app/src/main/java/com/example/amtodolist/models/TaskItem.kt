package com.example.amtodolist.models


data class TaskItem(
    val id: Int = 0,
    val title : String,
    val description : String,
    var completed : Boolean = false,
    var selected : Boolean = false,
    var timestamp: Long = 0,
    var timestampStr : String = ""
)
