package com.mcmouse88.basic_testing.tasks

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.mcmouse88.basic_testing.ServiceLocator
import com.mcmouse88.basic_testing.data.Task
import com.mcmouse88.basic_testing.data.source.FakeAndroidTasksRepository
import com.mcmouse88.basic_testing.data.source.TasksRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.mcmouse88.basic_testing.R
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@MediumTest
class TasksFragmentTest {

    private lateinit var repository: TasksRepository

    @Before
    fun initRepository() {
        repository = FakeAndroidTasksRepository()
        ServiceLocator.taskRepository = repository
    }

    @After
    fun cleanUp() = runTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun clickTask_navigateToDetailFragmentOne() = runTest {
        // GIVEN - On the tasks screen with two tasks
        repository.saveTask(Task("Title1", "Description1", false, "id1"))
        repository.saveTask(Task("Title2", "Description2", true, "id2"))

        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.BasicTestTheme)

        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        // WHEN - Click on the first list item
        onView(withId(R.id.tasks_list))
            .perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText("Title1")),
                click()
            ))

        // THEN - Verify that we navigate to the first detail screen
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment("id1")
        )
    }

    @Test
    fun clickAddTaskButton_navigateToAddEditFragment() = runTest {
        // GIVEN - On the task screen with empty tasks
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.BasicTestTheme)
        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - Click on the FAB
        onView(withId(R.id.add_task_fab))
            .perform(click())

        // THEN - Verify that we navigate to add new task screen
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                null,
                InstrumentationRegistry.getInstrumentation().targetContext.resources.getString(R.string.add_task)
            )
        )
    }
}