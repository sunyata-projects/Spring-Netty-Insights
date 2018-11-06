package org.sunyata.quark.client;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by leo on 17/5/10.
 */


@Configuration
@ConditionalOnProperty(value = "quark.rabbit.enabled", matchIfMissing = false)
public class MessageConfig {

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

}