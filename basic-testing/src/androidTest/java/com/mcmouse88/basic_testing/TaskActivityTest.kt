package com.mcmouse88.basic_testing

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
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
import com.mcmouse88.basic_testing.util.EspressoIdlingResource
import com.mcmouse88.basic_testing.utils.DataBindingIdlingResource
import com.mcmouse88.basic_testing.utils.monitorActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TaskActivityTest {

    private lateinit var repository: TasksRepository

    // An idling resource that waits for Data Binding to have no pending bindings.
    private val dataBindingIdlingResource = DataBindingIdlingResource()

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

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(
            EspressoIdlingResource.countingIdlingResource,
            dataBindingIdlingResource
        )
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(
            EspressoIdlingResource.countingIdlingResource,
            dataBindingIdlingResource
        )
    }

    @Test
    fun editTask() = runBlocking {
        // Set initial state
        repository.saveTask(Task("title1", "description"))

        // Start up Tasks Screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list and verify that all the data is correct
        onView(withText("title1")).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("title1")))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("description")))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isNotChecked()))

        // Click on the edit button, edit and save
        onView(withId(R.id.edit_task_fab)).perform(click())
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

    @Test
    fun createOneTask_deleteTask() = runBlocking {
        // 1. Start TasksActivity.
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // 2. Add an active task by clicking on the FAB and saving a new task.
        onView(withId(R.id.add_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(typeText("new title"), closeSoftKeyboard())
        onView(withId(R.id.add_task_description_edit_text)).perform(typeText("new description"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // 3. Open the new task in a details view.
        onView(withText("new title")).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("new title")))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("new description")))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isNotChecked()))

        // 4. Click delete task in menu.
        onView(withId(R.id.menu_delete)).perform(click())

        // 5. Verify it was deleted.
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("new title")).check(doesNotExist())

        // 6. Make sure the activity is closed.
        activityScenario.close()
    }
}