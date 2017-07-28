/*
 *
 *
 *  * Copyright (c) 2017 Leo Lee(lichl.1980@163.com).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy
 *  * of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *
 */

package org.sunyata.quark.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.sunyata.quark.client.dto.BusinessComponentDescriptor;
import org.sunyata.quark.client.dto.BusinessComponentInstance;
import org.sunyata.quark.client.json.Json;
import org.sunyata.quark.client.message.ComplexMessageInfo;
import org.sunyata.quark.client.message.CreateBusinessComponentMessageInfo;
import org.sunyata.quark.client.message.MessageInfoType;
import org.sunyata.quark.client.message.RunBySerialMessageInfo;

import java.util.HashMap;
import java.util.List;

/**
 * Created by leo on 17/4/1.
 */
//@Component
//@Configuration
public class QuarkClientImpl implements QuarkClient {
    final Logger logger = LoggerFactory.getLogger(QuarkClientImpl.class);

    //@Autowired(required = false)
    QuarkFeignClient quarkFeignClient;
    //@Autowired
    RabbitTemplate rabbitTemplate;

    //    @Value("${quark.rabbit.queue:quarkQueue}")
    public String rabbitQueue;
    //
//    @Value("${quark.rabbit.exchange:quarkExchange}")
    public String rabbitExchange;

    public QuarkClientImpl(RabbitTemplate rabbitTemplate, String exchangeName, String queueName, String serviceName) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitExchange = exchangeName;
        this.rabbitQueue = queueName;
        quarkFeignClient = new QuarkFeignClientImpl(serviceName);
    }

    @Override
    public JsonResponseResult create(String serialNo, String businName, String sponsor, String relationId, String
            parameterString, boolean autoRun) throws Exception {
        return quarkFeignClient.create(serialNo, businName, sponsor, relationId, parameterString, autoRun);
    }

    @Override
    public JsonResponseResult create(String serialNo, String businName, String parameterString, boolean autoRun)
            throws Exception {
        return quarkFeignClient.create(serialNo, businName, null, null, parameterString, autoRun);
    }

    @Override
    public void createAsync(String serialNo, String businName, String parameterString, boolean autoRun) {
        ComplexMessageInfo messageInfo = new ComplexMessageInfo();
        messageInfo.setJobInfoType(MessageInfoType.CreateBusinessComponent);
        messageInfo.setBodyJsonString(Json.encode(new CreateBusinessComponentMessageInfo().setAutoRun(autoRun)
                .setBusinName(businName).setSerialNo(serialNo).setParameterString(parameterString)));
        String messageInfoString = Json.encode(messageInfo);
        try {
            rabbitTemplate.convertAndSend(rabbitExchange, rabbitQueue, messageInfoString);
        } catch (AmqpException aex) {
            logger.info(String.format("[job发送异常][%s]_%s_%s", MessageInfoType.CreateBusinessComponent.getValue(),
                    MessageInfoType.CreateBusinessComponent, messageInfoString));
            throw aex;
        }
    }

    @Override
    public JsonResponseResult create(String serialNo, String businName, HashMap<String, Object> parameters, boolean
            autoRun) throws Exception {
        String encode = Json.encode(parameters);
        return quarkFeignClient.create(serialNo, businName, null, null, encode, autoRun);
    }

    @Override
    public JsonResponseResult<List<BusinessComponentDescriptor>> components() throws Exception {
        return quarkFeignClient.components();
    }

    @Override
    public JsonResponseResult run(String serialNo) throws Exception {
        return quarkFeignClient.run(serialNo);
    }

    @Override
    public void runAsync(String serialNo) {
        ComplexMessageInfo messageInfo = new ComplexMessageInfo();
        messageInfo.setJobInfoType(MessageInfoType.RunBySerialNo);
        messageInfo.setBodyJsonString(Json.encode(new RunBySerialMessageInfo().setSerialNo(serialNo)));
        String messageInfoString = Json.encode(messageInfo);
        try {
            rabbitTemplate.convertAndSend(rabbitExchange, rabbitQueue, messageInfoString);
        } catch (AmqpException aex) {
            logger.info(String.format("[job发送异常][%s]_%s_%s", MessageInfoType.RunBySerialNo.getValue(),
                    MessageInfoType.RunBySerialNo, messageInfoString));
            throw aex;
        }
    }

    @Override
    public JsonResponseResult runByManual(String serialNo, Integer quarkIndex, String parameterString) throws
            Exception {
        return quarkFeignClient.runByManual(serialNo, quarkIndex, parameterString);
    }

    @Override
    public JsonResponseResult<BusinessComponentInstance> instance(String serialNo) throws Exception {
        return quarkFeignClient.instance(serialNo);
    }
}
