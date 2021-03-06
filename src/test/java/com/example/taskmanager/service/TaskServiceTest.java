package com.example.taskmanager.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.Task.Status;
import com.example.taskmanager.exception.BadRequestException;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.util.TaskManErrors;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskServiceTest {

    public TaskRepository taskRepository;
    public UserRepository userRepository;
    public TaskServiceImpl taskService;

    @BeforeAll
    public void TaskServiceTest() {
        taskService = spy(TaskServiceImpl.class);
        taskRepository = mock(TaskRepository.class);
        userRepository = mock(UserRepository.class);
        taskService.taskRepository = taskRepository;
        taskService.userRepository = userRepository;
    }

    @Test
    public void closedTaskCantEdited() {

        try (MockedStatic<TaskManErrors> mockedStatic = Mockito.mockStatic(TaskManErrors.class)) {

            mockedStatic
                    .when(() -> TaskManErrors.getErrorMessage(any(String.class)))
                    .thenReturn("ERROR!");
            when(taskRepository.findById(1l)).thenReturn(returnCloseTask());
            doNothing().when(taskService).validateSecurityUser(any());
            Assertions.assertThrows(
                    BadRequestException.class, () -> taskService.updateTask(getTaskDTO()));
        }
    }

    public TaskDto getTaskDTO() {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1l);
        taskDto.setStatus(Status.OPEN);
        taskDto.setDescription("Closed Task");
        return taskDto;
    }

    public Optional<Task> returnCloseTask() {
        Task closedTask = new Task();
        closedTask.setId(1l);
        closedTask.setStatus(Status.CLOSED);
        return Optional.of(closedTask);
    }
}
