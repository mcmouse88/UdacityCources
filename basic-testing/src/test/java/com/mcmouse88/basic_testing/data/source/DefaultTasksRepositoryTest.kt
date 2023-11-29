package com.mcmouse88.basic_testing.data.source

import com.mcmouse88.basic_testing.data.Result
import com.mcmouse88.basic_testing.data.Task
import com.mcmouse88.basic_testing.utils.MainCoroutineRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DefaultTasksRepositoryTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2")
    private val task3 = Task("Title3", "Description3")
    private val remoteTasks = listOf(task1, task2).sortedBy { it.id }
    private val localeTasks = listOf(task3).sortedBy { it.id }
    private val newTasks = listOf(task3).sortedBy { it.id }

    private lateinit var taskRemoteDataSource: FakeDataSource
    private lateinit var taskLocalDataSource: FakeDataSource

    // Class under test
    private lateinit var tasksRepository: DefaultTasksRepository

    @Before
    fun createRepository() {
        taskRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())
        taskLocalDataSource = FakeDataSource(localeTasks.toMutableList())

        tasksRepository = DefaultTasksRepository(
            tasksRemoteDataSource = taskRemoteDataSource,
            tasksLocalDataSource = taskLocalDataSource,
            ioDispatcher = Dispatchers.Main
        )
    }

    @Test
    fun `get tasks request all tasks from remote date source`() = runTest {
        // When tasks are requested from the tasks repository
        val tasks = tasksRepository.getTasks(true) as Result.Success

        // Then tasks are loaded from the remote date source
        MatcherAssert.assertThat(tasks.data, IsEqual(remoteTasks))
    }
}