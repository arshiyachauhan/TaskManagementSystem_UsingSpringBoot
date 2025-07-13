package com.task.TaskManager.service;

import com.task.TaskManager.model.Project;
import com.task.TaskManager.model.User;
import com.task.TaskManager.repository.ProjectRepository;
import com.task.TaskManager.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public Project createProject(Project project, Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException("Project Manager with ID " + managerId + " not found"));
        project.setProjectManager(manager);
        return projectRepository.save(project);
    }

    public Optional<Project> getProject(Long projectId) {
        return projectRepository.findById(projectId);
    }

    public List<Project> getProjectsByManager(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + managerId + " not found"));
        return projectRepository.findByProjectManager(manager);
    }

    @Transactional
    public Project updateProject(Long projectId, Project updatedProject, Long managerId) {
        Project existing = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project with ID " + projectId + " not found"));

        if (!existing.getProjectManager().getId().equals(managerId)) {
            throw new IllegalArgumentException("Only the project manager can update this project");
        }

        if (updatedProject.getName() != null) existing.setName(updatedProject.getName());
        if (updatedProject.getDescription() != null) existing.setDescription(updatedProject.getDescription());
        if (updatedProject.getStartDate() != null) existing.setStartDate(updatedProject.getStartDate());
        if (updatedProject.getEndDate() != null) existing.setEndDate(updatedProject.getEndDate());

        return projectRepository.save(existing);
    }

    @Transactional
    public boolean deleteProject(Long projectId) {
        Optional<Project> existing = projectRepository.findById(projectId);
        if (existing.isEmpty()) {
            return false;
        }
        projectRepository.deleteById(projectId);
        return true;
    }
}
