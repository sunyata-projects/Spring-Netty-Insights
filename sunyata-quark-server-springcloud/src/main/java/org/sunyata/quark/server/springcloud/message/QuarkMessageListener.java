package org.sunyata.quark.server.springcloud.message;

import com.rabbitmq.client.Channel;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sunyata.quark.json.Json;
import org.sunyata.quark.message.ComplexMessageInfo;
import org.sunyata.quark.server.springcloud.exception.BusinessComponentConstraintViolationException;

/**
 * Created by leo on 17/5/8.
 */
@Component("quarkMessageListener")
public class QuarkMessageListener implements ChannelAwareMessageListener {
    final Logger logger = LoggerFactory.getLogger(QuarkMessageListener.class);
    @Autowired
    MessageProcessService processService;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        logger.info("消息来啦:" + msg);
        ComplexMessageInfo jobInfo = null;
        try {
            jobInfo = Json.decodeValue(msg, ComplexMessageInfo.class);
            logger.info("job任务序列化成功:" + msg);
        } catch (Exception e) {//序列化失败,任务被抛弃
            logger.error("消息序列化失败:{}", ExceptionUtils.getStackTrace(e));
            //channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            //channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
        try {
            processService.process(jobInfo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (BusinessComponentConstraintViolationException ex) {
            logger.error("业务流水号冲突" + ExceptionUtils.getFullStackTrace(ex));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception ex) {
            logger.error("处理新消息时发生错误:" + ExceptionUtils.getFullStackTrace(ex));
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
