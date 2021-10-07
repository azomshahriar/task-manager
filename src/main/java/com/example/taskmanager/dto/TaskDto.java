package com.example.taskmanager.dto;

import com.example.taskmanager.entity.Task.Status;
import java.time.Instant;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskDto {

    private Long id;

    @NotBlank(message = "taskDto.blank.description")
    private String description;

    @NotNull(message = "status.blank")
    private Status status;

    @NotNull(message = "Task must me under a project")
    @Min(value = 1, message = "Task must me under a project")
    private Long projectId;

    @NotNull(message = "Task mush have a due date")
    @Future(message = "Date Should be future.")
    Instant dueDate;
}
