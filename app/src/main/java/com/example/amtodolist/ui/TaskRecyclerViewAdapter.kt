package com.example.amtodolist.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.amtodolist.R
import com.example.amtodolist.models.TaskItem

class TaskRecyclerViewAdapter(private val clickListener: View.OnClickListener,
                              private val longClickListener: View.OnLongClickListener,
                              private val taskList: ArrayList<TaskItem>) : RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder>() {

    // holder class to hold reference
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //get view reference
        val title: TextView = view.findViewById(R.id.title)
        val description: TextView = view.findViewById(R.id.description)
        val timestamp: TextView = view.findViewById(R.id.timestamp)
        val taskLayout: View = view.findViewById(R.id.task_layout)
        val taskCompletedBadge: ImageView = view.findViewById(R.id.task_completed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create view holder to hold reference
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //set values
        val task = taskList[position]
        holder.title.text = task.title
        holder.description.text = task.description
        holder.timestamp.text = task.timestampStr
        var res = R.drawable.regular_task_drawable
        if (task.selected) {
            res = R.drawable.selected_task_drawable
        }
        holder.taskLayout.background = AppCompatResources.getDrawable(holder.title.context, res)
        holder.taskCompletedBadge.isVisible = task.completed
        holder.taskLayout.tag = position
        holder.taskLayout.setOnClickListener(clickListener)
        holder.taskLayout.setOnLongClickListener(longClickListener)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    // update your data
    @SuppressLint("NotifyDataSetChanged")
    fun addData(tasksData: ArrayList<TaskItem>) {
        taskList.clear()
        taskList.addAll(tasksData)
        notifyDataSetChanged()

    }
}