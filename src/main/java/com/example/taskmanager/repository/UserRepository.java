package com.example.taskmanager.repository;

import com.example.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String userName);

    @Query(value = "select * from User LIMIT 1", nativeQuery = true)
    User findFirstUser();
}
