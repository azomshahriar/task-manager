package com.example.taskmanager.dto;

import com.example.taskmanager.entity.Task.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchTaskDto {

    Long projectId;
    Status status;
    Boolean expired;
}
