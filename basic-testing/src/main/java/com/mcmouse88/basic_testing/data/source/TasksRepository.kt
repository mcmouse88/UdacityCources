package com.mcmouse88.basic_testing.data.source

import androidx.lifecycle.LiveData
import com.mcmouse88.basic_testing.data.Result
import com.mcmouse88.basic_testing.data.Task

interface TasksRepository {
    suspend fun getTasks(forceUpdate: Boolean = false): Result<List<Task>>

    suspend fun refreshTasks()
    fun observeTasks(): LiveData<Result<List<Task>>>

    suspend fun refreshTask(taskId: String)

    suspend fun updateTasksFromRemoteDataSource()
    fun observeTask(taskId: String): LiveData<Result<Task>>

    suspend fun updateTaskFromRemoteDataSource(taskId: String)

    /**
     * Relies on [getTasks] to fetch data and picks the task with the same ID.
     */
    suspend fun getTask(taskId: String, forceUpdate: Boolean = false): Result<Task>

    suspend fun saveTask(task: Task)

    suspend fun completeTask(task: Task)

    suspend fun completeTask(taskId: String)

    suspend fun activateTask(task: Task)

    suspend fun activateTask(taskId: String)

    suspend fun clearCompletedTasks()

    suspend fun deleteAllTasks()

    suspend fun deleteTask(taskId: String)
}