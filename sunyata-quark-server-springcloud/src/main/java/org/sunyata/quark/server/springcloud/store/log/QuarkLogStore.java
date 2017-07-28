package org.sunyata.quark.server.springcloud.store.log;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunyata.quark.json.Json;
import org.sunyata.quark.server.springcloud.QuarkServerProperties;
import org.sunyata.quark.server.springcloud.store.BusinessMapper;
import org.sunyata.quark.store.BusinessComponentInstance;
import org.sunyata.quark.store.QuarkComponentInstance;
import org.sunyata.quark.store.QuarkComponentLog;
import org.sunyata.quark.store.QuarkParameter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by leo on 17/7/21.
 */
@Component
public class QuarkLogStore {
    Logger logger = LoggerFactory.getLogger(QuarkLogStore.class);
    @Autowired
    SqlSessionTemplate sessionTemplate;


    @Autowired
    BusinessMapper mapper;
    @Autowired
    QuarkServerProperties quarkServerProperties;

//    public void writeLog(BusinessComponentInstance instance, QuarkComponentLog quarkComponentLog) {
//        try (SqlSession session = sessionTemplate.getSqlSessionFactory().openSession(false)) {
//            BusinessMapper mapper = session.getMapper(BusinessMapper.class);
//            mapper.updateBusinessComponent(instance);
//            if (instance.getOutputParameters() != null && instance.getOutputParameters().size() > 0) {
//                HashMap<String, Object> outputParameters = instance.getOutputParameters();
//                if (outputParameters.size() > 0) {
//                    String encode = Json.encode(outputParameters);
//                    QuarkParameter quarkParameter = mapper.findQuarkParameter(instance.getSerialNo(), 2);
//                    if (quarkParameter == null) {
//                        quarkParameter = new QuarkParameter().setBusinessSerialNo(instance.getSerialNo())
//                                .setParameterType(2).setParameter(encode);
//                        mapper.insertByQuarkParameter(quarkParameter);
//                    } else {
//                        if (!StringUtils.equals(encode, quarkParameter.getParameter())) {
//                            quarkParameter.setParameter(encode);
//                            mapper.updateQuarkParameter(quarkParameter);
//                        }
//                    }
//                }
//            }
//            QuarkComponentInstance quarkComponentInstance = instance.getItems().stream().filter(p -> p.getSerialNo()
//                    .equals(quarkComponentLog.getSerialNo())).findFirst
//                    ().orElse(null);
//            //instance.getItems().forEach(mapper::updateAtomicComponent);
//            if (quarkComponentInstance != null) {
//                mapper.updateAtomicComponent(quarkComponentInstance);
//            }
//            if (quarkServerProperties.isLogEnable()) {
//                mapper.insertByComponentLog(quarkComponentLog);
//            } else {
//                logger.info("Quark日志:{}", Json.encode(quarkComponentLog));
//            }
//            session.commit();
//        } catch (Throwable throwable) {
//            logger.error(ExceptionUtils.getStackTrace(throwable));
//            throw throwable;
//        }
//    }

    @Transactional(rollbackFor = Exception.class)
    public void writeLog(BusinessComponentInstance instance, QuarkComponentLog quarkComponentLog) {
        try  {
            mapper.updateBusinessComponent(instance);
            if (instance.getOutputParameters() != null && instance.getOutputParameters().size() > 0) {
                HashMap<String, Object> outputParameters = instance.getOutputParameters();
                if (outputParameters.size() > 0) {
                    String encode = Json.encode(outputParameters);
                    QuarkParameter quarkParameter = mapper.findQuarkParameter(instance.getSerialNo(), 2);
                    if (quarkParameter == null) {
                        quarkParameter = new QuarkParameter().setBusinessSerialNo(instance.getSerialNo())
                                .setParameterType(2).setParameter(encode);
                        mapper.insertByQuarkParameter(quarkParameter);
                    } else {
                        if (!StringUtils.equals(encode, quarkParameter.getParameter())) {
                            quarkParameter.setParameter(encode);
                            mapper.updateQuarkParameter(quarkParameter);
                        }
                    }
                }
            }
            QuarkComponentInstance quarkComponentInstance = instance.getItems().stream().filter(p -> p.getSerialNo()
                    .equals(quarkComponentLog.getSerialNo())).findFirst
                    ().orElse(null);
            //instance.getItems().forEach(mapper::updateAtomicComponent);
            if (quarkComponentInstance != null) {
                mapper.updateAtomicComponent(quarkComponentInstance);
            }
            if (quarkServerProperties.isLogEnable()) {
                mapper.insertByComponentLog(quarkComponentLog);
            } else {
                logger.info("Quark日志:{}", Json.encode(quarkComponentLog));
            }
        } catch (Throwable throwable) {
            logger.error(ExceptionUtils.getStackTrace(throwable));
            throw throwable;
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public void writeLog(BusinessComponentInstance instance, List<QuarkComponentLog> quarkComponentLogs) {
        try {
            mapper.updateBusinessComponent(instance);
            if (instance.getOutputParameters() != null && instance.getOutputParameters().size() > 0) {
                HashMap<String, Object> outputParameters = instance.getOutputParameters();
                if (outputParameters.size() > 0) {
                    String encode = Json.encode(outputParameters);
                    QuarkParameter quarkParameter = mapper.findQuarkParameter(instance.getSerialNo(), 2);
                    if (quarkParameter == null) {
                        quarkParameter = new QuarkParameter().setBusinessSerialNo(instance.getSerialNo())
                                .setParameterType(2).setParameter(encode);
                        mapper.insertByQuarkParameter(quarkParameter);
                    } else {
                        if (!StringUtils.equals(encode, quarkParameter.getParameter())) {
                            quarkParameter.setParameter(encode);
                            mapper.updateQuarkParameter(quarkParameter);
                        }
                    }
                }
            }
            for (QuarkComponentLog quarkComponentLog : quarkComponentLogs) {
                QuarkComponentInstance quarkComponentInstance = instance.getItems().stream().filter(p -> p.getSerialNo()
                        .equals(quarkComponentLog.getSerialNo())).findFirst
                        ().orElse(null);
                //instance.getItems().forEach(mapper::updateAtomicComponent);
                if (quarkComponentInstance != null) {
                    mapper.updateAtomicComponent(quarkComponentInstance);
                }
                if (quarkServerProperties.isLogEnable()) {
                    mapper.insertByComponentLog(quarkComponentLog);
                } else {
                    logger.info("Quark日志:{}", Json.encode(quarkComponentLog));
                }
            }
        } catch (Throwable throwable) {
            logger.error(ExceptionUtils.getStackTrace(throwable));
            throw throwable;
        }
    }
//
//    public void writeLog(BusinessComponentInstance instance, List<QuarkComponentLog> quarkComponentLogs) {
//        try (SqlSession session = sessionTemplate.getSqlSessionFactory().openSession(false)) {
//            BusinessMapper mapper = session.getMapper(BusinessMapper.class);
//            mapper.updateBusinessComponent(instance);
//            if (instance.getOutputParameters() != null && instance.getOutputParameters().size() > 0) {
//                HashMap<String, Object> outputParameters = instance.getOutputParameters();
//                if (outputParameters.size() > 0) {
//                    String encode = Json.encode(outputParameters);
//                    QuarkParameter quarkParameter = mapper.findQuarkParameter(instance.getSerialNo(), 2);
//                    if (quarkParameter == null) {
//                        quarkParameter = new QuarkParameter().setBusinessSerialNo(instance.getSerialNo())
//                                .setParameterType(2).setParameter(encode);
//                        mapper.insertByQuarkParameter(quarkParameter);
//                    } else {
//                        if (!StringUtils.equals(encode, quarkParameter.getParameter())) {
//                            quarkParameter.setParameter(encode);
//                            mapper.updateQuarkParameter(quarkParameter);
//                        }
//                    }
//                }
//            }
//            for (QuarkComponentLog quarkComponentLog : quarkComponentLogs) {
//                QuarkComponentInstance quarkComponentInstance = instance.getItems().stream().filter(p -> p.getSerialNo()
//                        .equals(quarkComponentLog.getSerialNo())).findFirst
//                        ().orElse(null);
//                //instance.getItems().forEach(mapper::updateAtomicComponent);
//                if (quarkComponentInstance != null) {
//                    mapper.updateAtomicComponent(quarkComponentInstance);
//                }
//                if (quarkServerProperties.isLogEnable()) {
//                    mapper.insertByComponentLog(quarkComponentLog);
//                } else {
//                    logger.info("Quark日志:{}", Json.encode(quarkComponentLog));
//                }
//            }
//            session.commit();
//        } catch (Throwable throwable) {
//            logger.error(ExceptionUtils.getStackTrace(throwable));
//            throw throwable;
//        }
//    }

}
