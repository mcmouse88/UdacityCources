package com.mcmouse88.basic_testing.tasks

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TasksViewModelTest {

    @Test
    fun `add new task sets new task event`() {
        // Given a fresh ViewModel
        val tasksViewModel = TasksViewModel(
            ApplicationProvider.getApplicationContext()
        )

        tasksViewModel.addNewTask()
        // When adding a new task

        // Then the new task event is triggered
    }
}