package org.sunyata.quark.server.springcloud;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sunyata.quark.json.Json;
import org.sunyata.quark.message.ComplexMessageInfo;
import org.sunyata.quark.message.MessageInfoType;
import org.sunyata.quark.message.RunBySerialMessageInfo;
import org.sunyata.quark.server.springcloud.exception.MQConnectionException;

/**
 * Created by leo on 17/9/20.
 */
@Component
public class QuarkSelfCheckService {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    DirectExchange exchange;
    @Autowired
    Queue queue;

    public void checkRabbitmq() throws MQConnectionException {
        ComplexMessageInfo messageInfo = new ComplexMessageInfo();
        messageInfo.setJobInfoType(MessageInfoType.RunBySerialNo);
        messageInfo.setBodyJsonString(Json.encode(new RunBySerialMessageInfo().setSerialNo("-1")));
        String messageInfoString = Json.encode(messageInfo);
        try {
            rabbitTemplate.convertAndSend(exchange.getName(), queue.getName(), messageInfoString);
        } catch (AmqpException aex) {
            String host = rabbitTemplate.getConnectionFactory().getHost();
            int port = rabbitTemplate.getConnectionFactory().getPort();
            throw new MQConnectionException("连接MQ异常:" + host + ":" + port);
        }
    }
}
