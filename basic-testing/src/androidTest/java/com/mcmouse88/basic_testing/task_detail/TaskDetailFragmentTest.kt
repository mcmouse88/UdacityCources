package com.mcmouse88.basic_testing.task_detail

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.mcmouse88.basic_testing.data.Task
import org.junit.Test
import org.junit.runner.RunWith
import com.mcmouse88.basic_testing.R

@RunWith(AndroidJUnit4::class)
@MediumTest
class TaskDetailFragmentTest {

    @Test
    fun activeTaskDetails_DisplayedInUi() {
        // GIVEN - Add active (incomplete) task to the database
        val activeTask = Task("Active Task", "AndroidX Rocks", false)

        // WHEN - Details fragment launched to display task
        val bundle = TaskDetailFragmentArgs(activeTask.id).toBundle()
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.BasicTestTheme)
    }
}