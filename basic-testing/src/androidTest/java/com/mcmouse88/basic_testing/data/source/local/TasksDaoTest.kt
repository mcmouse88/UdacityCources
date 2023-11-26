package com.mcmouse88.basic_testing.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.mcmouse88.basic_testing.data.Task
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class TasksDaoTest {

    // Executes each task synchronously using Architecture Component
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ToDoDatabase
    private lateinit var dao: TasksDao

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        ).build()
        dao = database.taskDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertTaskAndGetById() = runTest {
        // GIVEN - insert a task
        val task = Task("title", "description")
        dao.insertTask(task)

        // WHEN - Get the task by id from the database
        val loaded = dao.getTaskById(task.id)

        // THEN - The loaded data contains the expected value
        MatcherAssert.assertThat(loaded as Task, notNullValue())
        MatcherAssert.assertThat(loaded.id, `is`(task.id))
        MatcherAssert.assertThat(loaded.title, `is`(task.title))
        MatcherAssert.assertThat(loaded.description, `is`(task.description))
        MatcherAssert.assertThat(loaded.isCompleted, `is`(task.isCompleted))
    }

    @Test
    fun updateTaskAndGetById() = runTest {
        // GIVEN - Insert a task into the DAO, than update the task by creating a new task
        // with the same ID but different attributes
        val task = Task("title", "description")
        dao.insertTask(task)
        dao.updateTask(
            task.copy(
                title = "Updated Title",
                description = "Updated description"
            )
        )

        // WHEN - Get the task by id from the database
        val updated = dao.getTaskById(task.id)

        // THEN - Check that when you get the task by its ID, it has the updated values.
        MatcherAssert.assertThat(updated as Task, notNullValue())
        MatcherAssert.assertThat(updated.id, `is`(task.id))
        MatcherAssert.assertThat(updated.title, `is`("Updated Title"))
        MatcherAssert.assertThat(updated.description, `is`("Updated description"))
        MatcherAssert.assertThat(updated.isCompleted, `is`(task.isCompleted))
    }
}