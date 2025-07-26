package com.practice.project_1.service;

import com.practice.project_1.model.Task;
import com.practice.project_1.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

//Creating a CRUD Operation Services.
    public Task addTask(Task task){
        task.setTaskId(UUID.randomUUID().toString().split("-")[0]);
        return repository.save(task);
    }

    public List<Task> findAllTasks(){
        return repository.findAll();
    }

    public Task getByTaskId(String taskId){
        return repository.findById(taskId).get();
    }


    public List<Task> FindBySeverity(int severity){
        return repository.findBySeverity(severity);
    }


    public Task updateTasks(Task taskRequests){
        Task existingTask = repository.findById(taskRequests.getTaskId()).get();
        existingTask.setDescription(taskRequests.getDescription());
        existingTask.setSeverity(taskRequests.getSeverity());
        existingTask.setSeverity(taskRequests.getSeverity());
        existingTask.setStoryPoint(taskRequests.getStoryPoint());
        return repository.save(existingTask);
    }

    public int deleteTask(int severity){
        repository.deleteBySeverity(severity);
        return 0;
    }

    public String deleteTask(String taskId){
        repository.deleteById(taskId);
        return taskId + " Deleted";
    }

}
