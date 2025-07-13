package com.task.TaskManager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.TaskManager.model.Role;
import com.task.TaskManager.model.Role.ERole;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}



