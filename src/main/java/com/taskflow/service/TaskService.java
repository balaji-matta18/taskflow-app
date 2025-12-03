package com.taskflow.service;

import com.taskflow.model.Category;
import com.taskflow.model.Priority;
import com.taskflow.model.Task;
import com.taskflow.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /* ---------- CRUD ---------- */

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    /* ---------- Unified Search + Filter Logic ---------- */

    public List<Task> getFilteredTasks(String query, Priority priority, Category category) {

        boolean hasQuery = (query != null && !query.isBlank());
        boolean hasPriority = (priority != null);
        boolean hasCategory = (category != null);

        // Case 1 – Search + Priority + Category
        if (hasQuery && hasPriority && hasCategory) {
            return taskRepository.findByTitleContainingIgnoreCaseAndPriorityAndCategory(query, priority, category);
        }

        // Case 2 – Search + Priority
        if (hasQuery && hasPriority) {
            return taskRepository.findByTitleContainingIgnoreCaseAndPriority(query, priority);
        }

        // Case 3 – Search + Category
        if (hasQuery && hasCategory) {
            return taskRepository.findByTitleContainingIgnoreCaseAndCategory(query, category);
        }

        // Case 4 – Priority + Category
        if (hasPriority && hasCategory) {
            return taskRepository.findByPriorityAndCategory(priority, category);
        }

        // Case 5 – Only search
        if (hasQuery) {
            return taskRepository.findByTitleContainingIgnoreCase(query);
        }

        // Case 6 – Only priority
        if (hasPriority) {
            return taskRepository.findByPriority(priority);
        }

        // Case 7 – Only category
        if (hasCategory) {
            return taskRepository.findByCategory(category);
        }

        // Case 8 – No filters
        return taskRepository.findAll();
    }

    /* ---------- Toggle completion ---------- */

    public void toggleComplete(Long id) {
        taskRepository.findById(id).ifPresent(task -> {
            task.setCompleted(!task.isCompleted());
            taskRepository.save(task);
        });
    }

    /* ---------- Dashboard statistics ---------- */

    public long countAll() {
        return taskRepository.count();
    }

    public long countCompleted() {
        return taskRepository.countByCompleted(true);
    }

    public long countPending() {
        return taskRepository.countByCompleted(false);
    }

    public long countDueToday() {
        return taskRepository.countByDueDate(LocalDate.now());
    }

    public long countOverdue() {
        return taskRepository.countByDueDateBeforeAndCompletedFalse(LocalDate.now());
    }
}
