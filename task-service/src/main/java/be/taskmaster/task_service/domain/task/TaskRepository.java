package be.taskmaster.task_service.domain.task;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByTeamIdOrderByCreatedAtDesc(String teamId);
}
