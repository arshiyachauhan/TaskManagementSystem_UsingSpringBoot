package com.task.TaskManager.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.task.TaskManager.model.Project;
import com.task.TaskManager.model.User;



public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Find projects by project manager
    List<Project> findByProjectManager(User projectManager);

    // Get all projects with pagination
    Page<Project> findAll(Pageable pageable);

    // Find projects by name containing a string (case-insensitive) with pagination
    Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Count projects that are still active (not ended yet)
    long countByEndDateIsNullOrEndDateGreaterThan(Long currentTimestamp);

    // Find all projects ordered by creation date descending
    List<Project> findAllByOrderByCreatedAtDesc();
}
