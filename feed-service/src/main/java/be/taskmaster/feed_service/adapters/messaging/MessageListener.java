package be.taskmaster.feed_service.adapters.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    @RabbitListener(queues = RabbitMqConfig.TASK_CREATED_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void onTaskCreated(Map<String, Object> payload) {
        LOGGER.info("feed_service received task.created -> {}", payload);
    }
}
