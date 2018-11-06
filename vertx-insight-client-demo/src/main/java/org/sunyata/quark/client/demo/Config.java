package org.sunyata.quark.client.demo;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sunyata.quark.client.QuarkClient;
import org.sunyata.quark.client.QuarkClientImpl;

/**
 * Created by leo on 17/5/11.
 */
@Configuration
public class Config {

    @Autowired
    RabbitTemplate rabbitTemplate;



    @Bean("edy-quark-rabbit-template")
    RabbitTemplate edyQuarkRabbitTemplate() {
        return rabbitTemplate;
    }

    @Bean("ccop-share-quark-rabbit-template")
    RabbitTemplate ccopShareQuarkRabbitTemplate() {
        return rabbitTemplate;
    }

    @Bean("edy-quark-client")
    QuarkClient edyQuarkClient(@Qualifier("edy-quark-rabbit-template") RabbitTemplate edyQuarkRabbitTemplate) {
        return new QuarkClientImpl(edyQuarkRabbitTemplate, "quarkExchangelcl", "quarkQueuelcl", "quark-service");
    }

    @Bean("ccop-share-quark-client")
    QuarkClient ccopShareQuarkClient(@Qualifier("ccop-share-quark-rabbit-template") RabbitTemplate ccopShareQuarkRabbitTemplate) {
        return new QuarkClientImpl(ccopShareQuarkRabbitTemplate, "quarkExchangelcl", "quarkQueuelcl",
                "ccop-share-quark-service");
    }

}
