package com.mcmouse88.basic_testing.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.mcmouse88.basic_testing.EventObserver
import com.mcmouse88.basic_testing.R
import com.mcmouse88.basic_testing.data.Task
import com.mcmouse88.basic_testing.data.source.DefaultTasksRepository
import com.mcmouse88.basic_testing.databinding.TasksFragmentBinding
import com.mcmouse88.basic_testing.util.setupRefreshLayout
import com.mcmouse88.basic_testing.util.setupSnackbar
import timber.log.Timber

/**
 * Display a grid of [Task]s. User can choose to view all, active or completed tasks.
 */
class TasksFragment : Fragment(R.layout.tasks_fragment) {

    private val viewModel by viewModels<TasksViewModel> {
        TasksViewModel.TasksViewModelFactory(
            DefaultTasksRepository.getRepository(requireActivity().application)
        )
    }

    private val args: TasksFragmentArgs by navArgs()

    private lateinit var viewDataBinding: TasksFragmentBinding

    private lateinit var listAdapter: TasksAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding = TasksFragmentBinding.bind(view).apply {
            viewmodel = viewModel
        }

        // Set the lifecycle owner to the lifecycle of the view
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupSnackbar()
        setupListAdapter()
        setupRefreshLayout(viewDataBinding.refreshLayout, viewDataBinding.tasksList)
        setupNavigation()
        setupFab()
        provideOptionMenu()
    }

    private fun provideOptionMenu() {
        val menuProvider = createMenuProvider()
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun setupNavigation() {
        viewModel.openTaskEvent.observe(viewLifecycleOwner, EventObserver {
            openTaskDetails(it)
        })
        viewModel.newTaskEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToAddNewTask()
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        arguments?.let {
            viewModel.showEditResultMessage(args.userMessage)
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_tasks, menu)

            setOnMenuItemClickListener {
                viewModel.setFiltering(
                    when (it.itemId) {
                        R.id.active -> TasksFilterType.ACTIVE_TASKS
                        R.id.completed -> TasksFilterType.COMPLETED_TASKS
                        else -> TasksFilterType.ALL_TASKS
                    }
                )
                true
            }
            show()
        }
    }

    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.add_task_fab)?.let {
            it.setOnClickListener {
                navigateToAddNewTask()
            }
        }
    }

    private fun navigateToAddNewTask() {
        val action = TasksFragmentDirections
            .actionTasksFragmentToAddEditTaskFragment(
                null,
                resources.getString(R.string.add_task)
            )
        findNavController().navigate(action)
    }

    private fun openTaskDetails(taskId: String) {
        val action = TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment(taskId)
        findNavController().navigate(action)
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = TasksAdapter(viewModel)
            viewDataBinding.tasksList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun createMenuProvider(): MenuProvider {
        return object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.tasks_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_clear -> {
                        viewModel.clearCompletedTasks()
                        true
                    }

                    R.id.menu_filter -> {
                        showFilteringPopUpMenu()
                        true
                    }

                    R.id.menu_refresh -> {
                        viewModel.loadTasks(true)
                        true
                    }

                    else -> false
                }
            }
        }
    }
}
