package com.task.TaskManager.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.task.TaskManager.model.Project;
import com.task.TaskManager.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find tasks by assignedToId with pagination
    Page<Task> findByAssignedToId(Long assignedToId, Pageable pageable);

    // Find tasks by project with pagination
    Page<Task> findByProject(Project project, Pageable pageable);

    // Find tasks by assignedToId and status with pagination
    Page<Task> findByAssignedToIdAndStatus(Long assignedToId, Task.ETaskStatus status, Pageable pageable);

    // Find tasks by project and status with pagination
    Page<Task> findByProjectAndStatus(Project project, Task.ETaskStatus status, Pageable pageable);

    // Find tasks due before or on a specific date
    List<Task> findByDueDateLessThanEqual(Long dueDate);

    // Get all tasks with pagination
    Page<Task> findAll(Pageable pageable);

    // Find tasks by status
    List<Task> findByStatus(Task.ETaskStatus status);

    // Count tasks by status
    long countByStatus(Task.ETaskStatus status);

    // Count tasks due before or on a specific date
    long countByDueDateLessThanEqual(Long dueDate);

    // Find all tasks ordered by creation date descending (recent first)
    List<Task> findAllByOrderByCreatedAtDesc();
}
