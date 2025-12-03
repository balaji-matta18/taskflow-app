package com.taskflow.controller;

import com.taskflow.model.Category;
import com.taskflow.model.Priority;
import com.taskflow.model.Task;
import com.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Redirect /tasks/ → /tasks
    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/tasks";
    }

    // LIST + FILTER + DASHBOARD
    @GetMapping
    public String listTasks(@RequestParam(required = false) String q,
                            @RequestParam(required = false) Priority priority,
                            @RequestParam(required = false) Category category,
                            Model model) {

        List<Task> tasks = taskService.getFilteredTasks(q, priority, category);

        // Dashboard summary statistics
        model.addAttribute("totalTasks", taskService.countAll());
        model.addAttribute("completedTasks", taskService.countCompleted());
        model.addAttribute("pendingTasks", taskService.countPending());
        model.addAttribute("dueToday", taskService.countDueToday());
        model.addAttribute("overdue", taskService.countOverdue());

        // Current filter values
        model.addAttribute("tasks", tasks);
        model.addAttribute("query", q);
        model.addAttribute("priority", priority);
        model.addAttribute("category", category);

        // Enums for UI
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("categories", Category.values());

        return "tasks/index";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("categories", Category.values());
        return "tasks/form";
    }

    @PostMapping
    public String createTask(@Valid @ModelAttribute("task") Task task,
                             BindingResult result,
                             Model model) {

        if (result.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("categories", Category.values());
            return "tasks/form";
        }

        taskService.saveTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {

        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task id: " + id));

        model.addAttribute("task", task);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("categories", Category.values());

        return "tasks/form";
    }

    @PostMapping("/{id}")
    public String updateTask(@PathVariable Long id,
                             @Valid @ModelAttribute("task") Task task,
                             BindingResult result,
                             Model model) {

        if (result.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("categories", Category.values());
            return "tasks/form";
        }

        task.setId(id);
        taskService.saveTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/toggle")
    public String toggleComplete(@PathVariable Long id) {
        taskService.toggleComplete(id);
        return "redirect:/tasks";
    }
}
