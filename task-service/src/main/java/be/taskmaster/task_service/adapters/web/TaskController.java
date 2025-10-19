package be.taskmaster.task_service.adapters.web;

import be.taskmaster.task_service.adapters.messaging.RabbitMqMessageSender;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final RabbitMqMessageSender sender;
    private final AtomicLong ids = new AtomicLong(1);

    public TaskController(RabbitMqMessageSender sender) {
        this.sender = sender;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User", required = false) String user) {
        var teamId = body.get("teamId");
        if (teamId == null || teamId.isBlank())
            return ResponseEntity.badRequest().body("teamId required");

        long id = ids.getAndIncrement();
        sender.publishTaskCreated(
                id,
                teamId,
                body.getOrDefault("title", "Untitled"),
                body.getOrDefault("description", ""),
                user);
        return ResponseEntity.ok(Map.of("id", id, "teamId", teamId, "status", "published"));
    }
}
