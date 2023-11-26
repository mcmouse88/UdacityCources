package com.mcmouse88.basic_testing

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.mcmouse88.basic_testing.data.Task
import com.mcmouse88.basic_testing.data.source.TasksRepository
import com.mcmouse88.basic_testing.tasks.TasksActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TaskActivityTest {

    private lateinit var repository: TasksRepository

    @Before
    fun init() {
        repository = ServiceLocator.provideTasksRepository(
            ApplicationProvider.getApplicationContext()
        )
        runBlocking {
            repository.deleteAllTasks()
        }
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }

    @Test
    fun editTask() = runBlocking {
        // Set initial state
        repository.saveTask(Task("title1", "description"))

        // Start up Tasks Screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)

        // Click on the task on the list and verify that all the data is correct
        onView(withText("title1")).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("title1")))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("description")))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isNotChecked()))

        // Click on the edit button, edit and save
        onView(withId(R.id.add_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("new title"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("new description"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // Verify task is displayed on screen in the task list
        onView(withText("new title")).check(matches(isDisplayed()))
        // Verify previous task is not displayed
        onView(withText("title1")).check(doesNotExist())

        // Make sure the activity is closed before resetting the database
        activityScenario.close()
    }
}