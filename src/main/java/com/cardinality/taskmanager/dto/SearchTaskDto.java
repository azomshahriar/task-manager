package com.cardinality.taskmanager.dto;

import com.cardinality.taskmanager.entity.Task.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchTaskDto {

    Long projectId;
    Status status;
    Boolean expired;

}
