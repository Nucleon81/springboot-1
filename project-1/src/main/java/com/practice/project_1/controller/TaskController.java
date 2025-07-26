package com.practice.project_1.controller;


import com.practice.project_1.model.Task;
import com.practice.project_1.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    public TaskService taskService;

     @PostMapping
     @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody Task task){
        return taskService.addTask(task);
    }

    @GetMapping
    public List<Task> getTasks(){
         return taskService.findAllTasks();
    }

    @GetMapping("/{taskId}")
    public Task getTask(@PathVariable String taskId){
         return taskService.getByTaskId(taskId);
    }

    @GetMapping("/severity/{severity}")
    public List<Task> findTaskUsingSeverity(@PathVariable int severity){
         return taskService.FindBySeverity(severity);
    }

    @PutMapping
    public Task modifyTask(@RequestBody Task task){
         return taskService.updateTasks(task);
    }

    @DeleteMapping("/delete/{severity}")
    public int deleteTask(@PathVariable int severity){
         return taskService.deleteTask(severity);
    }

    @DeleteMapping("/deletebyId/{taskId}")
    public String deleteTask(@PathVariable String taskId){
        return taskService.deleteTask(taskId);
    }
}
