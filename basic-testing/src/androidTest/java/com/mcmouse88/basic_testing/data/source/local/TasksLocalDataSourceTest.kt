package com.mcmouse88.basic_testing.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.mcmouse88.basic_testing.data.Result
import com.mcmouse88.basic_testing.data.Task
import com.mcmouse88.basic_testing.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class TasksLocalDataSourceTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: TasksLocalDataSource
    private lateinit var database: ToDoDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource = TasksLocalDataSource(
            tasksDao = database.taskDao(),
            ioDispatcher = Dispatchers.Main
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveTask_retrievesTask() = runTest {
        // GIVEN - A new task saved in the database.
        val newTask = Task("title", "description", false)
        localDataSource.saveTask(newTask)

        // WHEN  - Task retrieved by ID.
        val result = localDataSource.getTask(newTask.id)

        // THEN - Same task is returned.
        MatcherAssert.assertThat(result.succeeded, `is`(true))
        result as Result.Success
        MatcherAssert.assertThat(result.data.title, `is`("title"))
        MatcherAssert.assertThat(result.data.description, `is`("description"))
        MatcherAssert.assertThat(result.data.isCompleted, `is`(false))
    }

    @Test
    fun completeTask_retrievedTaskIsComplete() = runTest {
        // GIVEN - Save a new active task in the local data source.
        val newTask = Task("title", "description", false)
        localDataSource.saveTask(newTask)

        // WHEN - Mark it as complete.
        localDataSource.completeTask(newTask)
        val completed = localDataSource.getTask(newTask.id)

        // THEN - Check that the task can be retrieved from the local data source and is complete.
        MatcherAssert.assertThat(completed.succeeded, `is`(true))
        completed as Result.Success
        MatcherAssert.assertThat(completed.data.title, `is`("title"))
        MatcherAssert.assertThat(completed.data.description, `is`("description"))
        MatcherAssert.assertThat(completed.data.isCompleted, `is`(true))
    }
}