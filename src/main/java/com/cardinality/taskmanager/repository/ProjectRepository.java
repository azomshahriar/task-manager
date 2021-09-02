package com.cardinality.taskmanager.repository;

import com.cardinality.taskmanager.entity.Project;
import com.cardinality.taskmanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project,Long> {

    Page<Project> findAllByUser(User user,Pageable pageable);

}
