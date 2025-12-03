package com.taskflow.repository;

import com.taskflow.model.Category;
import com.taskflow.model.Priority;
import com.taskflow.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    /* --- Search --- */
    List<Task> findByTitleContainingIgnoreCase(String title);

    /* --- Filters (Priority / Category / Combinations) --- */
    List<Task> findByPriority(Priority priority);

    List<Task> findByCategory(Category category);

    List<Task> findByPriorityAndCategory(Priority priority, Category category);

    List<Task> findByTitleContainingIgnoreCaseAndPriority(String title, Priority priority);

    List<Task> findByTitleContainingIgnoreCaseAndCategory(String title, Category category);

    List<Task> findByTitleContainingIgnoreCaseAndPriorityAndCategory(
            String title,
            Priority priority,
            Category category
    );

    /* --- Dashboard counts --- */
    long countByCompleted(boolean completed);

    long countByDueDate(LocalDate dueDate);

    long countByDueDateBeforeAndCompletedFalse(LocalDate date);
}
