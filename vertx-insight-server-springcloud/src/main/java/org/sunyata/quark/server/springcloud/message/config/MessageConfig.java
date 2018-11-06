package org.sunyata.quark.server.springcloud.message.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sunyata.quark.executor.Executor;
import org.sunyata.quark.server.springcloud.message.QuarkMessageListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by leo on 17/5/8.
 */
@Configuration
@ConditionalOnProperty(value = "quark.rabbit.enabled", matchIfMissing = false)
public class MessageConfig {

    Logger logger = LoggerFactory.getLogger(MessageConfig.class);
    @Value("${quark.rabbit.queue:quarkQueue}")
    public String rabbitQueueReceive;

    @Value("${quark.rabbit.exchange:quarkExchange}")
    public String rabbitExchangeReceive;


    @Value("${quark.rabbit.connection.host}")
    public String host;

    @Value("${quark.rabbit.connection.port}")
    public String port;
    @Value("${quark.rabbit.connection.vhost}")
    public String vHost;
    @Value("${quark.rabbit.connection.user}")
    public String user;
    @Value("${quark.rabbit.connection.password}")
    public String password;



    @Value("${quark.rabbit.maxConcurrentConsumer:16}")
    public Integer maxConcurrentConsumer;

    @Bean
    Queue queue() {
        return new Queue(rabbitQueueReceive, false);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(rabbitExchangeReceive);
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(rabbitQueueReceive);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    ConnectionFactory connectionFactory() {
        CachingConnectionFactory result = new CachingConnectionFactory();
        result.setHost(host);
        result.setPort(Integer.parseInt(port));
        result.setVirtualHost(vHost);
        result.setUsername(user);
        result.setPassword(password);
        result.setConnectionTimeout(6000);

        return result;
    }

    @Bean("concurrentConsumersTaskExecutor")
    ExecutorService concurrentConsumersTaskExecutor()
    {
        return Executors.newFixedThreadPool(maxConcurrentConsumer);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter, ConsumerTagStrategy
                                                     consumerTagStrategy,@Qualifier("concurrentConsumersTaskExecutor") ExecutorService concurrentConsumersTaskExecutor) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(rabbitQueueReceive);
        container.setMessageListener(listenerAdapter);
        logger.info("quark.maxConcurrentConsumer = {}",maxConcurrentConsumer);
        container.setMaxConcurrentConsumers(maxConcurrentConsumer);
        container.setConcurrentConsumers(maxConcurrentConsumer);
        container.setTaskExecutor(concurrentConsumersTaskExecutor);
        container.setConsumerTagStrategy(consumerTagStrategy);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return container;
    }

    @Bean
    ConsumerTagStrategy consumerTagStrategy() {
        return s -> {
            return "consumer_" + Thread.currentThread().getName() + "_" + s;
//            return consumerName + "_" + s;
        };
    }

    @Bean
    MessageListenerAdapter listenerAdapter(QuarkMessageListener receiver) {
        return new MessageListenerAdapter(receiver, "onMessage");
    }
}
