package be.taskmaster.task_service.adapters.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class RabbitMqMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqMessageSender.class);
    private final RabbitTemplate rabbitTemplate;

    public RabbitMqMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishTaskCreated(long taskId, String teamId, String title, String description, String createdBy) {
        var payload = Map.of(
                "id", taskId,
                "teamId", teamId,
                "title", title,
                "description", description,
                "createdBy", createdBy == null ? "anonymous" : createdBy,
                "createdAt", Instant.now().toString());

        var routingKey = "task.created";
        LOGGER.info("Publishing {} to {} with key {}: {}", "TaskCreated", RabbitMqConfig.TASK_EVENTS_EXCHANGE,
                routingKey, payload);
        rabbitTemplate.convertAndSend(RabbitMqConfig.TASK_EVENTS_EXCHANGE, routingKey, payload);
    }
}
