package com.cardinality.taskmanager.repository;

import com.cardinality.taskmanager.entity.Project;
import com.cardinality.taskmanager.entity.Task;
import com.cardinality.taskmanager.entity.Task.Status;
import com.cardinality.taskmanager.entity.User;
import java.util.List;

public interface TaskSearchRepository {

    List<Task> searchTask(Project project,Boolean expired, Status status, User user);

}
