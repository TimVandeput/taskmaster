package be.taskmaster.task_service.domain.task;

import be.taskmaster.task_service.adapters.messaging.RabbitMqMessageSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {
    private final TaskRepository repo;
    private final RabbitMqMessageSender sender;

    public TaskService(TaskRepository repo, RabbitMqMessageSender sender) {
        this.repo = repo;
        this.sender = sender;
    }

    @Transactional
    public Task createTask(String teamId, String title, String description, String createdBy) {
        if (teamId == null || teamId.isBlank())
            throw new IllegalArgumentException("teamId required");
        if (title == null || title.isBlank())
            title = "Untitled";
        if (description == null)
            description = "";
        if (createdBy == null || createdBy.isBlank())
            createdBy = "anonymous";

        Task t = new Task();
        t.setTeamId(teamId);
        t.setTitle(title);
        t.setDescription(description);
        t.setCreatedBy(createdBy);

        Task saved = repo.save(t);

        sender.publishTaskCreated(saved.getId(), saved.getTeamId(), saved.getTitle(), saved.getDescription(),
                saved.getCreatedBy());
        return saved;
    }
}
