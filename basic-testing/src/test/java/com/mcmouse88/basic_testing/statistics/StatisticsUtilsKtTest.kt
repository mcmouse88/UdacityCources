package com.mcmouse88.basic_testing.statistics

import com.mcmouse88.basic_testing.data.Task
import org.junit.Assert
import org.junit.Test

class StatisticsUtilsKtTest {

    // If there's no completed task and one active task
    // then there are 100% active tasks and 0% completed tasks

    @Test
    fun `get active and completed stats no completed returns zero hundred`() {
        val tasks = listOf(
            Task(
                title = "title",
                description = "description",
                isCompleted = false
            )
        )

        val result = getActiveAndCompletedStats(tasks)

        Assert.assertEquals(0f, result.completedTasksPercent)
        Assert.assertEquals(100f, result.activeTasksPercent)
    }

    // If there's 2 completed tasks and 3 active tasks,
    // then there are 40% percent completed tasks and 60% active tasks
    @Test
    fun `get active and completed stats both returns forty sixty`() {
        val tasks = listOf(
            Task(
                title = "title",
                description = "description",
                isCompleted = true
            ),
            Task(
                title = "title",
                description = "description",
                isCompleted = true
            ),
            Task(
                title = "title",
                description = "description",
                isCompleted = false
            ),
            Task(
                title = "title",
                description = "description",
                isCompleted = false
            ),
            Task(
                title = "title",
                description = "description",
                isCompleted = false
            )
        )

        val result = getActiveAndCompletedStats(tasks)

        Assert.assertEquals(40f, result.completedTasksPercent)
        Assert.assertEquals(60f, result.activeTasksPercent)
    }

    @Test
    fun `get active and completed stats empty returns zeros`() {
        val tasks = emptyList<Task>()

        val result = getActiveAndCompletedStats(tasks)

        Assert.assertEquals(0f, result.completedTasksPercent)
        Assert.assertEquals(0f, result.activeTasksPercent)
    }

    @Test
    fun `get active and completed stats error returns zeros`() {
        val tasks = null

        val result = getActiveAndCompletedStats(tasks)

        Assert.assertEquals(0f, result.completedTasksPercent)
        Assert.assertEquals(0f, result.activeTasksPercent)
    }
}