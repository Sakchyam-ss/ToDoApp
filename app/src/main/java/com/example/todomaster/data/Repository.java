package com.example.todomaster.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.todomaster.model.Task;
import com.example.todomaster.util.TaskRoomDatabase;

import java.util.List;

public class Repository {

    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;

    public Repository(Application application) {
        TaskRoomDatabase database = TaskRoomDatabase.getDatabase((application));
        taskDao = database.taskDao();
        allTasks = taskDao.getTasks();  //calls data that is livedata from TaskDao function
    }

    public LiveData<List<Task>> getAllTasks(){
        return allTasks;
    }

    public void insert(Task task){
        TaskRoomDatabase.databaseWriterExecutor.execute( () -> taskDao.insertTask(task));
    }

    public LiveData<Task> get(long id){ return taskDao.get(id);}

    public void  update(Task task){
        TaskRoomDatabase.databaseWriterExecutor.execute( () -> taskDao.update(task));
    }

    public void delete(Task task){
        TaskRoomDatabase.databaseWriterExecutor.execute( () -> taskDao.delete(task));
    }

    public void deleteAllTasks() {
        TaskRoomDatabase.databaseWriterExecutor.execute(() -> taskDao.deleteAll());
    }
}
