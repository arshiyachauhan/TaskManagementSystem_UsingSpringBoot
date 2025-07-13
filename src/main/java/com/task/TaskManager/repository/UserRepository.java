package com.task.TaskManager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.TaskManager.model.User;


public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by their username
    Optional<User> findByUserName(String userName);

    // Check if a user with the given username exists
    boolean existsByUserName(String userName);

    // Check if a user with the given email exists
    boolean existsByEmail(String email);
}
