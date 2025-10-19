package be.taskmaster.task_service.adapters.web;

import be.taskmaster.task_service.domain.task.Task;
import be.taskmaster.task_service.domain.task.TaskRepository;
import be.taskmaster.task_service.domain.task.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    private final TaskRepository repo;

    public TaskController(TaskService taskService, TaskRepository repo) {
        this.taskService = taskService;
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User", required = false) String user) {
        Task saved = taskService.createTask(
                body.get("teamId"),
                body.get("title"),
                body.get("description"),
                user);
        return ResponseEntity.ok(Map.of(
                "id", saved.getId(),
                "teamId", saved.getTeamId(),
                "status", "persisted+published"));
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam String teamId) {
        return ResponseEntity.ok(repo.findByTeamIdOrderByCreatedAtDesc(teamId));
    }
}
