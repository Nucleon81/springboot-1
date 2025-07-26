package com.practice.project_1.repository;

import com.practice.project_1.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskRepository extends MongoRepository<Task , String> {
    List<Task> findBySeverity(int severity);

    void deleteBySeverity(int severity);
}

