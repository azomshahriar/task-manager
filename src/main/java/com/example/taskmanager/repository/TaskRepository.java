package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    // @EntityGraph("task.project")
    //    @Query(value = "FROM Task AS t LEFT JOIN t.project AS p LEFT JOIN p.user u WHERE u.id
    // =:userId ORDER BY t.id DESC offset: offset limit:limit ", nativeQuery = true)
    //    public List<Task> findAllTaskByUser(@Param("userId") Long userId,@Param("offset") Long
    // offset ,@Param("limit") int limit);

    Page<Task> findAllByUser(User user, Pageable pageable);

    Page<Task> findAllByProject(Project project, Pageable pageable);

    Page<Task> findAllByDueDateBefore(Instant instant, Pageable pageable);

    Page<Task> findAllByUserAndDueDateBefore(User user, Instant instant, Pageable pageable);
}
