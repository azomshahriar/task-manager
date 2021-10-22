package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.Task.Status;
import com.example.taskmanager.entity.User;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

public class TaskSearchRepositoryImpl implements TaskSearchRepository {

    @PersistenceContext private EntityManager entityManager;

    public List<Task> searchTask(Project project, Boolean expired, Status status, User user) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> cq = cb.createQuery(Task.class);

        Root<Task> taskRoot = cq.from(Task.class);
        List<Predicate> predicates = new ArrayList<>();

        if (project != null) {
            predicates.add(cb.equal(taskRoot.get("project"), project));
        }
        if (expired != null && expired) {
            predicates.add(cb.lessThan(taskRoot.get("dueDate"), Instant.now()));
        }
        if (status != null) {
            predicates.add(cb.equal(taskRoot.get("status"), status));
        }
        if (user != null) {
            predicates.add(cb.equal(taskRoot.get("user"), user));
        }
        cq.where(predicates.toArray(new Predicate[predicates.size()]));

        return entityManager.createQuery(cq).getResultList();
    }
}
