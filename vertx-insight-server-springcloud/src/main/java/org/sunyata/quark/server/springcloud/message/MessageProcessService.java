package org.sunyata.quark.server.springcloud.message;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vertx.insight.MessageQueueService;
import org.vertx.insight.QuarkExecutor;
import org.sunyata.quark.json.Json;
import org.sunyata.quark.message.ComplexMessageInfo;
import org.sunyata.quark.message.CreateBusinessComponentMessageInfo;
import org.sunyata.quark.message.MessageInfoType;
import org.sunyata.quark.message.RunBySerialMessageInfo;
import org.sunyata.quark.store.BusinessComponentInstance;

/**
 * Created by leo on 17/5/10.
 */

@Service
public class MessageProcessService {
    final Logger logger = LoggerFactory.getLogger(MessageProcessService.class);

    @Autowired(required = true)
    QuarkExecutor businessManager;

    @Autowired
    MessageQueueService messageQueueService;


    public void process(ComplexMessageInfo jobInfo) throws Exception {
        try {
            assert jobInfo != null;
            MessageInfoType messageInfoType = MessageInfoType.from(jobInfo.getJobInfoType());
            if (messageInfoType == MessageInfoType.CreateBusinessComponent) {
                String bodyJsonString = jobInfo.getBodyJsonString();
                CreateBusinessComponentMessageInfo createBusinessComponentMessageInfo = Json.decodeValue(bodyJsonString,
                        CreateBusinessComponentMessageInfo.class);
                create(createBusinessComponentMessageInfo.getSerialNo(),
                        createBusinessComponentMessageInfo.getBusinName(),
                        createBusinessComponentMessageInfo.getSponsor(),
                        createBusinessComponentMessageInfo.getRelationId(),
                        createBusinessComponentMessageInfo.getParameterString(), createBusinessComponentMessageInfo
                                .isAutoRun());
//                if (createBusinessComponentMessageInfo.isAutoRun()) {
//                    run(createBusinessComponentMessageInfo.getSerialNo());
//                }
            } else if (messageInfoType == MessageInfoType.RunBySerialNo) {
                String bodyJsonString = jobInfo.getBodyJsonString();
                RunBySerialMessageInfo runBySerialNoMessageInfo = Json.decodeValue(bodyJsonString,
                        RunBySerialMessageInfo.class);
                run(runBySerialNoMessageInfo.getSerialNo());
            }
        } catch (Exception ex) {
            logger.error("消息处理失败:" + ExceptionUtils.getStackTrace(ex));
            throw ex;
        }
    }

    public void create(String serialNo, String businName, String sponsor, String relationId, String parameterString,
                       boolean autoRun)
            throws Exception {
        BusinessComponentInstance instance = null;
        try {
            instance = businessManager.create(serialNo, businName, sponsor, relationId,
                    parameterString, false);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            throw ex;
        }
        logger.info("落盘完成,开始执行");
        if (autoRun) {
            logger.info("开始入Queue:{}", serialNo);
            long delay = instance.getCreateDateTime().getTime() - System.currentTimeMillis();
            messageQueueService.enQueue(instance.getBusinName(),instance.getBusinName(), delay, serialNo, true);
            logger.info("入Queue完成:{}", serialNo);
        }
    }

    public void run(String serialNo) throws Exception {
        try {
            businessManager.run(serialNo);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
        }
    }

}
