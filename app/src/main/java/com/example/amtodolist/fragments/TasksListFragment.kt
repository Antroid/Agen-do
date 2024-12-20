package com.example.amtodolist.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.amtodolist.R
import com.example.amtodolist.databinding.TasksListFragmentBinding
import com.example.amtodolist.ui.TaskRecyclerViewAdapter
import com.example.amtodolist.viewmodel.TaskViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class TasksListFragment : Fragment(), View.OnClickListener, View.OnLongClickListener{

    private var _binding: TasksListFragmentBinding? = null
    private val taskViewModel : TaskViewModel by activityViewModels<TaskViewModel>()
    private var tasksAdapter: TaskRecyclerViewAdapter? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = TasksListFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tasksAdapter = TaskRecyclerViewAdapter(this, this, ArrayList())
        _binding?.tasksList?.adapter = tasksAdapter

        _binding?.tasksList?.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )

        taskViewModel.mTaskItemsLiveData.observe(viewLifecycleOwner) { dataList ->

            tasksAdapter?.addData(dataList)
            _binding?.tasksList?.isVisible = dataList.size > 0
            _binding?.emptyTasksPlaceholder?.isVisible = dataList.size == 0

        }

        binding.addTask.backgroundTintList = ColorStateList.valueOf(Color.rgb(36,140,204))

        binding.addTask.setOnClickListener {
            findNavController().navigate(R.id.action_TasksList_to_CreateTaskFragment)
        }

        updateContextMenu(false)//by default context menu needs to be hidden

        //dealing with onbackpress while in editmode (the user long clicked on an item/items and clicked back
        //we need to exit edit mode before exiting the application
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(taskViewModel.isTasksSelected()){
                    updateContextMenu(false)
                    taskViewModel.resetSelectedTasks()
                } else {
                    requireActivity().finish()
                }
            }
        })

    }

    private fun updateContextMenu(visibility: Boolean){
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            }

            override fun onPrepareMenu(menu: Menu) {
                val deleteItem = menu.findItem(R.id.action_delete)
                deleteItem?.setVisible(visibility)
                val selectedStates = taskViewModel.getCompletedTasksStates()

                val completeTasks = menu.findItem(R.id.action_complete_tasks)
                val undoTasks = menu.findItem(R.id.action_undo_tasks)

                if(selectedStates == TaskViewModel.TASK_STATE.NONE && visibility) {
                    completeTasks?.setVisible(false)
                    undoTasks?.setVisible(false)
                } else if(selectedStates == TaskViewModel.TASK_STATE.DONE && visibility) {
                    completeTasks?.setVisible(false)
                    undoTasks?.setVisible(true)
                } else if(selectedStates == TaskViewModel.TASK_STATE.NOT_DONE && visibility) {
                    completeTasks?.setVisible(true)
                    undoTasks?.setVisible(false)
                } else {
                    completeTasks?.setVisible(visibility)
                    undoTasks?.setVisible(visibility)
                }


            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.action_delete -> {
                        _binding?.addTask?.isVisible = false
                        taskViewModel.prepTasksForDelete()
                        updateContextMenu(false)
                        Snackbar
                            .make(view!!, getString(R.string.undo_delete), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.undo)) {
                                taskViewModel.rollBackTasks()
                                _binding?.addTask?.isVisible = true
                            }
                            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                override fun onDismissed(
                                    transientBottomBar: Snackbar?,
                                    event: Int
                                ) {
                                    super.onDismissed(transientBottomBar, event)
                                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                        taskViewModel.deleteTasks()
                                        Toast.makeText(requireContext(), getString(R.string.tasks_were_successfully_deleted), Toast.LENGTH_LONG).show()
                                        _binding?.addTask?.isVisible = true
                                    }
                                }
                            })
                        .show()
                        return true
                    }
                    R.id.action_complete_tasks ->{
                        taskViewModel.updateSelectedTasks(true)
                        updateContextMenu(false)//hide context menu
                        taskViewModel.updateTasks()
                        return true
                    }
                    R.id.action_undo_tasks ->{
                        taskViewModel.updateSelectedTasks(false)
                        updateContextMenu(false) //hide context menu
                        taskViewModel.updateTasks()
                        return true
                    }
                }

                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    override fun onResume() {
        super.onResume()
        taskViewModel.getTasks()
        updateContextMenu(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.task_layout ->{
                val index = view.tag as Int
                taskViewModel.setSelectedTask(index)
                findNavController().navigate(R.id.action_TasksList_to_CreateTaskFragment)
            }
        }
    }

    override fun onLongClick(view: View): Boolean {
        when(view.id){
            R.id.task_layout ->{
                val index = view.tag as Int

                //set next state for long press - than update ui accordingly to the next state
                taskViewModel.setSelectedTaskState(index, !taskViewModel.getSelectedTaskState(index))

                //we have at least one task which is "selected" - in edit mode - return true
                val inEditMode = taskViewModel.isTasksInEditMode()

                updateContextMenu(inEditMode)
                if(taskViewModel.getSelectedTaskState(index)) {
                    view.setBackgroundResource(R.drawable.selected_task_drawable)
                } else {
                    view.setBackgroundResource(R.drawable.regular_task_drawable)
                }

                return true
            }
        }
       return false
    }
}