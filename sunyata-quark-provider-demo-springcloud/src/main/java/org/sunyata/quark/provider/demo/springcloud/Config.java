package org.sunyata.quark.provider.demo.springcloud;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sunyata.quark.client.QuarkClient;
import org.sunyata.quark.client.QuarkClientImpl;

/**
 * Created by leo on 17/7/31.
 */
@Configuration
public class Config {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Bean()
    RabbitTemplate rabbitTemplate() {
        return rabbitTemplate;
    }

    @Bean("edy-quark-client")
    QuarkClient quarkClient(RabbitTemplate rabbitTemplate) {
        return new QuarkClientImpl(rabbitTemplate, "quarkExchangelcl", "quarkQueuelcl", "quark-service");
    }

}
