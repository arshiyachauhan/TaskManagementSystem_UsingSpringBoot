package com.task.TaskManager.controller;

import com.task.TaskManager.model.Project;
import com.task.TaskManager.model.User;
import com.task.TaskManager.service.ProjectService;
import com.task.TaskManager.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    //CREATE Project
    @PostMapping
    @PreAuthorize("hasRole('ROLE_TEAM_MANAGER')")
    public ResponseEntity<?> createProject(@RequestBody Project project) {
        try {
            Optional<User> managerOpt = userService.getCurrentUser();
            if (managerOpt.isEmpty()) {
                return ResponseEntity.status(401).body("Unauthorized");
            }

            User manager = managerOpt.get();
            Project created = projectService.createProject(project, manager.getId());
            return ResponseEntity.status(201).body(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // GET Project by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_TEAM_MANAGER', 'ROLE_TEAM_MEMBER')")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        return projectService.getProject(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET Projects for Logged-in Manager
    @GetMapping("/my-projects")
    @PreAuthorize("hasRole('ROLE_TEAM_MANAGER')")
    public ResponseEntity<?> getMyProjects() {
        Optional<User> currentUserOpt = userService.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<Project> projects = projectService.getProjectsByManager(currentUserOpt.get().getId());
        return ResponseEntity.ok(projects);
    }

    //UPDATE Project
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_TEAM_MANAGER')")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @RequestBody Project updateData) {
        Optional<User> managerOpt = userService.getCurrentUser();
        if (managerOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            Project updated = projectService.updateProject(id, updateData, managerOpt.get().getId());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    //DELETE Project
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_TEAM_MANAGER')")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        try {
            boolean deleted = projectService.deleteProject(id);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
