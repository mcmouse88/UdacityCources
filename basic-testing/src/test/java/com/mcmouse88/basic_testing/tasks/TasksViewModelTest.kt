package com.mcmouse88.basic_testing.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mcmouse88.basic_testing.data.Task
import com.mcmouse88.basic_testing.data.source.FakeTasksRepository
import com.mcmouse88.basic_testing.utils.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TasksViewModelTest {

    @get:Rule
    var executorRule = InstantTaskExecutorRule()

    private lateinit var tasksRepository: FakeTasksRepository
    private lateinit var tasksViewModel: TasksViewModel

    @Before
    fun setupViewModel() {
        tasksRepository = FakeTasksRepository()
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2")
        val task3 = Task("Title3", "Description3")
        tasksRepository.addTasks(task1, task2, task3)
        tasksViewModel = TasksViewModel(tasksRepository)
    }

    @Test
    fun `add new task sets new task event`() {

        // When adding a new task
        tasksViewModel.addNewTask()

        // Then the new task event is triggered
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()
        MatcherAssert.assertThat(value.getContentIfNotHandled(), not(nullValue()))
    }

    @Test
    fun `set filter all tasks then tasks add view visible`() {

        // When the filter type is ALL_TASKS
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        // Then the "Add task" action is visible
        val value = tasksViewModel.tasksAddViewVisible.getOrAwaitValue()
        MatcherAssert.assertThat(value, `is`(true))
    }
}