package com.example.amtodolist.fragments

import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.example.amtodolist.R
import com.example.amtodolist.databinding.CreateTaskFragmentBinding
import com.example.amtodolist.models.TaskItem
import com.example.amtodolist.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateTaskFragment : Fragment() {

    private var taskItem: TaskItem?= null
    private var _binding: CreateTaskFragmentBinding? = null
    private val taskViewModel : TaskViewModel by activityViewModels<TaskViewModel>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = CreateTaskFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menu.findItem(R.id.action_delete).setVisible(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        taskViewModel.mSelectedItem.observe(viewLifecycleOwner) {
            taskItem = it
            _binding?.taskTitle?.setText(taskItem?.title)
            _binding?.taskDescription?.setText(taskItem?.description)
            if(taskItem != null) {
                _binding?.actionButton?.text = getString(R.string.update)
            }
        }

        _binding?.let{ bind ->
            bind.actionButton.setOnClickListener {
                val title = bind.taskTitle.text.toString()
                val description = bind.taskDescription.text.toString()
                if(taskItem != null) {
                    taskViewModel.updateTask(TaskItem(id=taskItem!!.id, title = title, description = description))
                } else {
                    taskViewModel.insertTask(TaskItem(title = title, description = description))
                }

                Toast.makeText(context, getString(R.string.task_was_successfully_created), Toast.LENGTH_LONG).show()
                findNavController(this).popBackStack()
            }
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            }

            override fun onPrepareMenu(menu: Menu) {
                val deleteItem = menu.findItem(R.id.action_delete)
                deleteItem?.setVisible(false)
                val completeTasks = menu.findItem(R.id.action_complete_tasks)
                completeTasks?.setVisible(false)
                val undoTasks = menu.findItem(R.id.action_undo_tasks)
                undoTasks?.setVisible(false)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                //...

                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


    }


    override fun onDestroyView() {
        super.onDestroyView()
        taskViewModel.clearSelection()
        _binding = null
    }
}