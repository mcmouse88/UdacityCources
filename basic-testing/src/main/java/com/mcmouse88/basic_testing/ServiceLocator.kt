package com.mcmouse88.basic_testing

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.mcmouse88.basic_testing.data.source.DefaultTasksRepository
import com.mcmouse88.basic_testing.data.source.TasksDataSource
import com.mcmouse88.basic_testing.data.source.TasksRepository
import com.mcmouse88.basic_testing.data.source.local.TasksLocalDataSource
import com.mcmouse88.basic_testing.data.source.local.ToDoDatabase
import com.mcmouse88.basic_testing.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.runBlocking

object ServiceLocator {

    private val lock = Any()

    private var database: ToDoDatabase? = null

    @Volatile
    var taskRepository: TasksRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): TasksRepository {
        synchronized(this) {
            return taskRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): TasksRepository {
        val newRepo = DefaultTasksRepository(
            tasksRemoteDataSource = TasksRemoteDataSource,
            tasksLocalDataSource = createTasksLocalDateSource(context)
        )
        taskRepository = newRepo
        return newRepo
    }

    private fun createTasksLocalDateSource(context: Context): TasksDataSource {
        val database = database ?: createDatabase(context)
        return TasksLocalDataSource(database.taskDao())
    }

    private fun createDatabase(context: Context): ToDoDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java,
            "Tasks.db"
        ).build()
        database = result
        return result
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                TasksRemoteDataSource.deleteAllTasks()
            }
            // Clear all data to avoid test pollution
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            taskRepository = null
        }
    }
}