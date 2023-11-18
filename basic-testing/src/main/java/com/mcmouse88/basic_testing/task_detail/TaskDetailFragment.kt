package com.mcmouse88.basic_testing.task_detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.mcmouse88.basic_testing.EventObserver
import com.mcmouse88.basic_testing.R
import com.mcmouse88.basic_testing.data.source.DefaultTasksRepository
import com.mcmouse88.basic_testing.databinding.TaskDetailFragmentBinding
import com.mcmouse88.basic_testing.tasks.DELETE_RESULT_OK
import com.mcmouse88.basic_testing.util.setupRefreshLayout
import com.mcmouse88.basic_testing.util.setupSnackbar

/**
 * Main UI for the task detail screen.
 */
class TaskDetailFragment : Fragment(R.layout.task_detail_fragment) {
    private lateinit var viewDataBinding: TaskDetailFragmentBinding

    private val args: TaskDetailFragmentArgs by navArgs()

    private val viewModel by viewModels<TaskDetailViewModel> {
        TaskDetailViewModel.TaskDetailViewModelFactory(
            DefaultTasksRepository.getRepository(requireActivity().application)
        )
    }

    private fun setupNavigation() {
        viewModel.deleteTaskEvent.observe(viewLifecycleOwner, EventObserver {
            val action = TaskDetailFragmentDirections
                .actionTaskDetailFragmentToTasksFragment(DELETE_RESULT_OK)
            findNavController().navigate(action)
        })
        viewModel.editTaskEvent.observe(viewLifecycleOwner, EventObserver {
            val action = TaskDetailFragmentDirections
                .actionTaskDetailFragmentToAddEditTaskFragment(
                    args.taskId,
                    resources.getString(R.string.edit_task)
                )
            findNavController().navigate(action)
        })
    }

    private fun setupFab() {
        activity?.findViewById<View>(R.id.edit_task_fab)?.setOnClickListener {
            viewModel.editTask()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding = TaskDetailFragmentBinding.bind(view).apply {
            viewmodel = viewModel
        }

        setupFab()
        view.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        setupNavigation()
        this.setupRefreshLayout(viewDataBinding.refreshLayout)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.start(args.taskId)

        provideOptionMenu()
    }

    private fun provideOptionMenu() {
        val menuProvider = createMenuProvider()
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun createMenuProvider(): MenuProvider {
        return object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.taskdetail_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_delete -> {
                        viewModel.deleteTask()
                        true
                    }
                    else -> false
                }
            }
        }
    }
}
