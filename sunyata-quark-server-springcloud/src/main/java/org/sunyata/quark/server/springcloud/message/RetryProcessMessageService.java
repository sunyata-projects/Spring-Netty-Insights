//package org.sunyata.quark.server.springcloud.message;
//
//import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.retry.annotation.Backoff;
//import org.springframework.retry.annotation.Retryable;
//import org.springframework.stereotype.Service;
//import org.sunyata.quark.BusinessManager;
//import org.sunyata.quark.json.Json;
//import org.sunyata.quark.message.ComplexMessageInfo;
//import org.sunyata.quark.message.CreateBusinessComponentMessageInfo;
//import org.sunyata.quark.message.MessageInfoType;
//import org.sunyata.quark.message.RunBySerialMessageInfo;
//
///**
// * Created by leo on 17/5/10.
// */
//
//@Service
//public class RetryProcessMessageService {
//    final Logger logger = LoggerFactory.getLogger(RetryProcessMessageService.class);
//
//    @Autowired(required = true)
//    @Qualifier("asyncBusinessManager")
//    BusinessManager asyncBusinessManager;
//
//    @Autowired(required = true)
//    BusinessManager businessManager;
//
//    @Retryable(value = {Exception.class}, maxAttempts = 5, backoff = @Backoff(delay = 10000l, multiplier = 2))
//    public void process(ComplexMessageInfo jobInfo) throws Exception {
//        try {
//            assert jobInfo != null;
//            MessageInfoType messageInfoType = MessageInfoType.from(jobInfo.getJobInfoType());
//            if (messageInfoType == MessageInfoType.CreateBusinessComponent) {
//                String bodyJsonString = jobInfo.getBodyJsonString();
//                CreateBusinessComponentMessageInfo createBusinessComponentMessageInfo = Json.decodeValue(bodyJsonString,
//                        CreateBusinessComponentMessageInfo.class);
//                create(createBusinessComponentMessageInfo.getSerialNo(),
//                        createBusinessComponentMessageInfo.getBusinName(),
//                        createBusinessComponentMessageInfo.getSponsor(),
//                        createBusinessComponentMessageInfo.getRelationId(),
//                        createBusinessComponentMessageInfo.getParameterString(), createBusinessComponentMessageInfo
//                                .isAutoRun());
////                if (createBusinessComponentMessageInfo.isAutoRun()) {
////                    run(createBusinessComponentMessageInfo.getSerialNo());
////                }
//            } else if (messageInfoType == MessageInfoType.RunBySerialNo) {
//                String bodyJsonString = jobInfo.getBodyJsonString();
//                RunBySerialMessageInfo runBySerialNoMessageInfo = Json.decodeValue(bodyJsonString,
//                        RunBySerialMessageInfo.class);
//                run(runBySerialNoMessageInfo.getSerialNo());
//            }
//        } catch (Exception ex) {
//            logger.error("消息处理失败:" + ExceptionUtils.getStackTrace(ex));
//            throw ex;
//        }
//    }
//
//    public void create(String serialNo, String businName, String sponsor, String relationId, String parameterString,
//                       boolean autoRun)
//            throws Exception {
//        try {
//            businessManager.create(serialNo, businName, sponsor, relationId, parameterString, autoRun);
//            if (autoRun) {
//                asyncBusinessManager.run(serialNo);
//            }
//        } catch (Exception ex) {
//            logger.error(ExceptionUtils.getStackTrace(ex));
//        }
//    }
//
//    public void run(String serialNo) throws Exception {
//        try {
//            asyncBusinessManager.run(serialNo);
//        } catch (Exception ex) {
//            logger.error(ExceptionUtils.getStackTrace(ex));
//        }
//    }
//
//}
