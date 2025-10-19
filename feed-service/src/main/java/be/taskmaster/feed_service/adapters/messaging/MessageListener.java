package be.taskmaster.feed_service.adapters.messaging;

import be.taskmaster.feed_service.domain.feed.FeedItem;
import be.taskmaster.feed_service.domain.feed.FeedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);
    private final FeedRepository repo;

    public MessageListener(FeedRepository repo) {
        this.repo = repo;
    }

    @RabbitListener(queues = RabbitMqConfig.TASK_CREATED_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void onTaskCreated(Map<String, Object> payload) {
        LOGGER.info("feed_service received task.created -> {}", payload);
        var item = new FeedItem();
        item.setTaskId(((Number) payload.get("id")).longValue());
        item.setTeamId((String) payload.get("teamId"));
        item.setTitle((String) payload.get("title"));
        item.setDescription((String) payload.get("description"));
        item.setCreatedBy((String) payload.get("createdBy"));
        item.setCreatedAt(Instant.parse((String) payload.get("createdAt")));
        repo.save(item);
    }
}
