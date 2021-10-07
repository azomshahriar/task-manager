package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.Task.Status;
import com.example.taskmanager.entity.User;
import java.util.List;

public interface TaskSearchRepository {

    List<Task> searchTask(Project project, Boolean expired, Status status, User user);
}
