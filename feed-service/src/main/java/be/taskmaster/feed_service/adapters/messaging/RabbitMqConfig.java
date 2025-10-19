package be.taskmaster.feed_service.adapters.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class RabbitMqConfig {

    public static final String TASK_EVENTS_EXCHANGE = "x.task-events";
    public static final String TASK_CREATED_QUEUE = "q.feed-service.task-created";
    public static final String TASK_CREATED_KEY = "task.created";

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(Jackson2JsonMessageConverter converter,
            CachingConnectionFactory cf) {
        var tpl = new RabbitTemplate(cf);
        tpl.setMessageConverter(converter);
        return tpl;
    }

    @Bean
    public RetryOperationsInterceptor retryInterceptor() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(1000, 2.0, 8000)
                .build();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer cfg,
            CachingConnectionFactory cf) {
        var f = new SimpleRabbitListenerContainerFactory();
        cfg.configure(f, cf);
        f.setAdviceChain(retryInterceptor());
        return f;
    }

    @Bean
    public Declarables feedDeclarables() {
        return new Declarables(
                new DirectExchange(TASK_EVENTS_EXCHANGE, true, false),
                QueueBuilder.durable(TASK_CREATED_QUEUE).build(),
                new Binding(
                        TASK_CREATED_QUEUE,
                        Binding.DestinationType.QUEUE,
                        TASK_EVENTS_EXCHANGE,
                        TASK_CREATED_KEY,
                        null));
    }
}
