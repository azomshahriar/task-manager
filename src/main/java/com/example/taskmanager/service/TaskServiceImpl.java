package com.example.taskmanager.service;

import com.example.taskmanager.config.SecurityUtils;
import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.Task.Status;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.entity.User.Role;
import com.example.taskmanager.exception.BadRequestException;
import com.example.taskmanager.exception.ElementNotFoundException;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.util.TaskManErrors;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired TaskRepository taskRepository;
    @Autowired ProjectRepository projectRepository;

    @Autowired UserRepository userRepository;

    @Transactional
    public void createTask(TaskDto taskDto) {
        Task task = adaptTask(taskDto);
        taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public TaskDto getTask(Long id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (!taskOptional.isPresent()) {
            throw new ElementNotFoundException(
                    TaskManErrors.getErrorCode(
                            TaskManErrors.TASK_USER, TaskManErrors.TASK_NOT_FOUND),
                    TaskManErrors.getErrorMessage(
                            TaskManErrors.ERROR_MAP.get(TaskManErrors.TASK_NOT_FOUND)));
        }
        validateSecurityUser(taskOptional.get().getUser());
        return adaptTaskDto(taskOptional.get());
    }

    private Task adaptTask(TaskDto taskDto) {
        Task task = new Task();
        task.setDescription(taskDto.getDescription());
        task.setDueDate(taskDto.getDueDate());
        Optional<Project> projectOptional = projectRepository.findById(taskDto.getProjectId());
        if (!projectOptional.isPresent()) {
            throw new ElementNotFoundException(
                    TaskManErrors.getErrorCode(
                            TaskManErrors.TASK_USER, TaskManErrors.PROJECT_NOT_FOUND),
                    TaskManErrors.getErrorMessage(
                            TaskManErrors.ERROR_MAP.get(TaskManErrors.PROJECT_NOT_FOUND)));
        }
        task.setProject(projectOptional.get());
        task.setUser(projectOptional.get().getUser());
        task.setStatus(taskDto.getStatus() != null ? taskDto.getStatus() : Status.OPEN);
        task.setCreatedDate(Instant.now());
        validateUser(projectOptional.get());
        return task;
    }

    private void validateUser(Project project) {
        if (SecurityUtils.getCurrentUserLogin().isPresent()) {
            User user = getSecurityUser();
            if (user != null) {
                if (user.getId() != project.getUser().getId()) {
                    throw new ElementNotFoundException(
                            TaskManErrors.getErrorCode(
                                    TaskManErrors.TASK_USER, TaskManErrors.INVALID_USER_CONTEXT),
                            TaskManErrors.getErrorMessage(
                                    TaskManErrors.ERROR_MAP.get(
                                            TaskManErrors.INVALID_USER_CONTEXT)));
                }
            } else {
                throw new ElementNotFoundException(
                        TaskManErrors.getErrorCode(
                                TaskManErrors.TASK_USER, TaskManErrors.USER_NOT_FOUND),
                        TaskManErrors.getErrorMessage(
                                TaskManErrors.ERROR_MAP.get(TaskManErrors.USER_NOT_FOUND)));
            }

        } else {
            throw new ElementNotFoundException(
                    TaskManErrors.getErrorCode(
                            TaskManErrors.TASK_USER, TaskManErrors.USER_NOT_FOUND),
                    TaskManErrors.getErrorMessage(
                            TaskManErrors.ERROR_MAP.get(TaskManErrors.USER_NOT_FOUND)));
        }
    }

    @Transactional(readOnly = true)
    public Page<TaskDto> getTasks(Pageable pageable) {
        Page<Task> taskPage;

        if (SecurityUtils.hasCurrentUserThisAuthority(Role.ADMIN.name())) {
            taskPage = taskRepository.findAll(pageable);
        } else {
            taskPage = taskRepository.findAllByUser(getSecurityUser(), pageable);
        }
        return taskPage.map(this::adaptTaskDto);
    }

    @Transactional(readOnly = true)
    public Page<TaskDto> getTasksByProject(Long projectId, Pageable pageable) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            throw new BadRequestException(
                    TaskManErrors.getErrorCode(
                            TaskManErrors.TASK_USER, TaskManErrors.PROJECT_NOT_FOUND),
                    TaskManErrors.getErrorMessage(
                            TaskManErrors.ERROR_MAP.get(TaskManErrors.PROJECT_NOT_FOUND)));
        }
        validateSecurityUser(projectOptional.get().getUser());
        Page<Task> taskPage = taskRepository.findAllByProject(projectOptional.get(), pageable);
        return taskPage.map(this::adaptTaskDto);
    }

    void validateSecurityUser(User taskUser) {
        if (!SecurityUtils.hasCurrentUserThisAuthority(Role.ADMIN.name())) {
            User user = getSecurityUser();
            if (user.getId() != taskUser.getId()) {
                throw new ElementNotFoundException(
                        TaskManErrors.getErrorCode(
                                TaskManErrors.TASK_USER, TaskManErrors.INVALID_USER_CONTEXT),
                        TaskManErrors.getErrorMessage(
                                TaskManErrors.ERROR_MAP.get(TaskManErrors.INVALID_USER_CONTEXT)));
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<TaskDto> getExpiredTasks(Pageable pageable) {
        Instant currentInstant = Instant.now();
        Page<Task> taskPage;
        if (SecurityUtils.hasCurrentUserThisAuthority(Role.ADMIN.name())) {
            taskPage = taskRepository.findAllByDueDateBefore(currentInstant, pageable);
        } else {
            taskPage =
                    taskRepository.findAllByUserAndDueDateBefore(
                            getSecurityUser(), currentInstant, pageable);
        }
        return taskPage.map(this::adaptTaskDto);
    }

    private TaskDto adaptTaskDto(Task task) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setDescription(task.getDescription());
        taskDto.setProjectId(task.getProject().getId());
        taskDto.setDueDate(task.getDueDate());
        taskDto.setStatus(task.getStatus());
        return taskDto;
    }

    @Transactional
    public void updateTask(TaskDto taskDto) {
        Optional<Task> taskOptional = taskRepository.findById(taskDto.getId());
        if (!taskOptional.isPresent()) {
            throw new ElementNotFoundException(
                    TaskManErrors.getErrorCode(
                            TaskManErrors.TASK_USER, TaskManErrors.TASK_NOT_FOUND),
                    TaskManErrors.getErrorMessage(
                            TaskManErrors.ERROR_MAP.get(TaskManErrors.TASK_NOT_FOUND)));
        }
        validateSecurityUser(taskOptional.get().getUser());

        Task task = taskOptional.get();
        if (task.getStatus() == Status.CLOSED) {
            throw new BadRequestException(
                    TaskManErrors.getErrorCode(
                            TaskManErrors.TASK_USER, TaskManErrors.NOT_ALLOWED_CLOSED_TASK),
                    TaskManErrors.getErrorMessage(
                            TaskManErrors.ERROR_MAP.get(TaskManErrors.NOT_ALLOWED_CLOSED_TASK)));
        }

        task.setDescription(taskDto.getDescription());
        task.setDueDate(taskDto.getDueDate());
        task.setStatus(taskDto.getStatus());
        task.setLastModifiedDate(Instant.now());
        task.setLastModifiedBy(SecurityUtils.getCurrentUserLogin().get());

        taskRepository.save(task);
    }

    public Page<TaskDto> findTaskByUser(Long userId, Pageable pageable) {

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new BadRequestException(
                    TaskManErrors.getErrorCode(
                            TaskManErrors.TASK_USER, TaskManErrors.USER_NOT_FOUND),
                    TaskManErrors.getErrorMessage(
                            TaskManErrors.ERROR_MAP.get(TaskManErrors.USER_NOT_FOUND)));
        }
        Page<Task> taskPage = taskRepository.findAllByUser(userOptional.get(), pageable);

        return taskPage.map(this::adaptTaskDto);
    }

    public List<TaskDto> searchTasks(Long projectId, Boolean expired, Status status) {
        User user = null;
        if (!SecurityUtils.hasCurrentUserThisAuthority(Role.ADMIN.name())) {
            user = getSecurityUser();
        }
        Project project = null;
        if (projectId != null && projectId > 0) {
            project = projectRepository.findById(projectId).get();
        }
        return null;
       // List<Task> taskList = taskRepository.searchTask(project, expired, status, user);
       // return taskList.stream().map(this::adaptTaskDto).collect(Collectors.toList());
    }

    public Page<TaskDto> searchTask(Long projectId, Boolean expired, Status status,Pageable pageable){

        Page<Task> page = taskRepository.findAll(new Specification<Task>() {
            @Override
            public Predicate toPredicate(
                    Root<Task> taskRoot, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates = new ArrayList<>();

                if (projectId != null && projectId > 0) {
                    Project project = projectRepository.findById(projectId).get();
                    if (project != null) {
                        predicates.add(criteriaBuilder.equal(taskRoot.get("project"), project));
                    }
                }
                if (expired != null && expired) {
                    predicates.add(criteriaBuilder.lessThan(taskRoot.get("dueDate"), Instant.now()));
                }
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(taskRoot.get("status"), status));
                }
                if (!SecurityUtils.hasCurrentUserThisAuthority(Role.ADMIN.name())) {
                    predicates.add(criteriaBuilder.equal(taskRoot.get("user"),  getSecurityUser()));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

        return page.map(this::adaptTaskDto);
    }

    private User getSecurityUser() {
        String userName = SecurityUtils.getCurrentUserLogin().get();
        User user = userRepository.findByUserName(userName);
        if (user == null)
            throw new ElementNotFoundException(
                    TaskManErrors.getErrorCode(
                            TaskManErrors.TASK_USER, TaskManErrors.INVALID_USER_CONTEXT),
                    TaskManErrors.getErrorMessage(
                            TaskManErrors.ERROR_MAP.get(TaskManErrors.INVALID_USER_CONTEXT)));
        return user;
    }
}
