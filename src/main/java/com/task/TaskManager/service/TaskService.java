package com.task.TaskManager.service;

import com.task.TaskManager.model.Project;
import com.task.TaskManager.model.Task;
import com.task.TaskManager.model.User;
import com.task.TaskManager.repository.ProjectRepository;
import com.task.TaskManager.repository.TaskRepository;
import com.task.TaskManager.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public Task createTask(Task task, Long projectId) {
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title must not be null or empty.");
        }

        if (task.getDueDate() == null) {
            throw new IllegalArgumentException("Due date must not be null.");
        }

        LocalDate dueDate = Instant.ofEpochMilli(task.getDueDate())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        if (dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past.");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project with ID " + projectId + " not found."));
        task.setProject(project);

        if (task.getAssignedToId() != null) {
            userRepository.findById(task.getAssignedToId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user with ID " + task.getAssignedToId() + " not found."));
        }

        if (task.getStatus() == null) {
            task.setStatus(Task.ETaskStatus.TO_DO);
        }

        if (task.getPriority() == null) {
            task.setPriority(Task.ETaskPriority.MEDIUM);
        }

        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public boolean deleteTaskById(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
