package com.mcmouse88.basic_testing.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mcmouse88.basic_testing.data.source.FakeTasksRepository
import com.mcmouse88.basic_testing.utils.MainCoroutineRule
import org.junit.Before
import org.junit.Rule

class StatisticsFragmentTest {

    @get:Rule
    var executorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var tasksRepository: FakeTasksRepository
    private lateinit var statisticsViewModel: StatisticsViewModel

    @Before
    fun setupViewModel() {
        // Initialize the repository with no task
        tasksRepository = FakeTasksRepository()
        statisticsViewModel = StatisticsViewModel(tasksRepository)
    }
}