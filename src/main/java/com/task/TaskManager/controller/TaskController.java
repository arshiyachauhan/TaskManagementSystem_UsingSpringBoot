package com.task.TaskManager.controller;

import com.task.TaskManager.model.Task;
import com.task.TaskManager.service.TaskService;
import com.task.TaskManager.service.UserService;
import com.task.TaskManager.model.User;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_TEAM_MANAGER')")
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        try {
            Long projectId = task.getProject() != null ? task.getProject().getId() : null;
            if (projectId == null) {
                return ResponseEntity.badRequest().body("Project ID is required.");
            }

            Task created = taskService.createTask(task, projectId);
            return ResponseEntity.status(201).body(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_TEAM_MANAGER', 'ROLE_TEAM_MEMBER')")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        Optional<Task> optionalTask = taskService.getTaskById(id);

        if (optionalTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Task task = optionalTask.get();
        Optional<User> currentUserOpt = userService.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User currentUser = currentUserOpt.get();

        if (task.getAssignedToId() != null && task.getAssignedToId().equals(currentUser.getId()) ||
            task.getProject().getProjectManager().getId().equals(currentUser.getId())) {
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.status(403).body("Access denied");
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_TEAM_MANAGER', 'ROLE_TEAM_MEMBER')")
    public Page<Task> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title,asc") String sort,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long assignedToId
    ) {
        String[] sortParams = sort.split(",");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]));
        return taskService.findTasksWithFilters(projectId, status, assignedToId, pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_TEAM_MANAGER')")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task updateData) {
        try {
            Optional<Task> optionalTask = taskService.getTaskById(id);
            if (optionalTask.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Task existing = optionalTask.get();

            if (updateData.getTitle() != null) existing.setTitle(updateData.getTitle());
            if (updateData.getDescription() != null) existing.setDescription(updateData.getDescription());
            if (updateData.getStatus() != null) existing.setStatus(updateData.getStatus());
            if (updateData.getPriority() != null) existing.setPriority(updateData.getPriority());
            if (updateData.getDueDate() != null) existing.setDueDate(updateData.getDueDate());

            if (updateData.getProject() != null && updateData.getProject().getId() != null) {
                existing.setProject(updateData.getProject());
            }

            if (updateData.getAssignedToId() != null) {
                existing.setAssignedToId(updateData.getAssignedToId());
            }

            Task updated = taskService.save(existing);
            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_TEAM_MANAGER')")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        boolean deleted = taskService.deleteTaskById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
