package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.entity.Task.Status;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    void createTask(TaskDto taskDto);

    Page<TaskDto> getTasks(Pageable pageable);

    TaskDto getTask(Long id);

    Page<TaskDto> getExpiredTasks(Pageable pageable);

    Page<TaskDto> getTasksByProject(Long projectId, Pageable pageable);

    void updateTask(TaskDto taskDto);

    Page<TaskDto> findTaskByUser(Long userId, Pageable pageable);

    List<TaskDto> searchTasks(Long projectId, Boolean expired, Status status);
    Page<TaskDto> searchTask(Long projectId, Boolean expired, Status status,Pageable pageable);
}
