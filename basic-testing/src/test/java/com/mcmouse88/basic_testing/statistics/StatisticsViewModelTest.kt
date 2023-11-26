package com.mcmouse88.basic_testing.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mcmouse88.basic_testing.data.source.FakeTasksRepository
import com.mcmouse88.basic_testing.utils.MainCoroutineRule
import com.mcmouse88.basic_testing.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModelTest {

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

    @Test
    fun `load tasks loading is displayed`() = runTest{
        statisticsViewModel.refresh()

        MatcherAssert.assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(true))
        runCurrent()

        MatcherAssert.assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun `load statistics when tasks are unavailable call error to display`() = runTest {
        // Make the repository return errors
        tasksRepository.setReturnsError(true)
        statisticsViewModel.refresh()
        runCurrent()

        // Then an error message is shown
        MatcherAssert.assertThat(statisticsViewModel.empty.getOrAwaitValue(), `is`(true))
        MatcherAssert.assertThat(statisticsViewModel.error.getOrAwaitValue(), `is`(true))
    }
}