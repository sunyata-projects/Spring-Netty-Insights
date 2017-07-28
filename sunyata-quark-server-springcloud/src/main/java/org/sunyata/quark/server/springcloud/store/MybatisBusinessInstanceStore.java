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

package org.sunyata.quark.server.springcloud.store;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunyata.quark.basic.BusinessQueryService;
import org.sunyata.quark.json.Json;
import org.sunyata.quark.server.springcloud.QuarkServerProperties;
import org.sunyata.quark.server.springcloud.exception.BusinessComponentConstraintViolationException;
import org.sunyata.quark.server.springcloud.store.log.QuarkLogStore;
import org.sunyata.quark.store.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

/**
 * Created by leo on 17/3/20.
 */
@Component
public class MybatisBusinessInstanceStore implements BusinessInstanceStore, BusinessInstanceLoader,
        BusinessQueryService {

    Logger logger = LoggerFactory.getLogger(MybatisBusinessInstanceStore.class);

    //    @Autowired
//    BusinessMapper businessMapper;
    @Autowired
    SqlSessionTemplate sessionTemplate;


    @Autowired
    QuarkLogStore quarkLogStore;
    @Autowired
    QuarkServerProperties quarkServerProperties;

//    @Autowired
//    @Qualifier("adsSqlSessionFactory")
//    SqlSessionFactory sqlSessionFactory;


    @Autowired
    BusinessMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(BusinessComponentInstance instance) throws Exception {
        long startTime = System.currentTimeMillis();   //获取开始时间
        try  {
            logger.debug("openSession:" + String.valueOf(System.currentTimeMillis() - startTime) + "ms");
            startTime = System.currentTimeMillis();   //获取开始时间

            logger.debug("getMapper:" + String.valueOf(System.currentTimeMillis() - startTime) + "ms");
            startTime = System.currentTimeMillis();   //获取开始时间
            mapper.insertByBusinessComponent(instance);
            mapper.insertByQuarkParameter(instance.getQuarkParameter());
            logger.debug("insertByBusinessComponent:" + String.valueOf(System.currentTimeMillis() - startTime) + "ms");

            for (QuarkComponentInstance item : instance.getItems()) {
                startTime = System.currentTimeMillis();   //获取开始时间
                mapper.insertByAtomicComponent(item);
                logger.debug("insertByAtomicComponent:" + String.valueOf(System.currentTimeMillis() - startTime) +
                        "ms");
            }
            startTime = System.currentTimeMillis();   //获取开始时间
            logger.debug("commit:" + String.valueOf(System.currentTimeMillis() - startTime) + "ms");
        } catch (Exception ex) {
            //System.out.println(ex);
            if (ex instanceof DuplicateKeyException) {
                throw new BusinessComponentConstraintViolationException(ex.getMessage());
            } else {
                throw ex;
            }
        }
    }


//    public void create(BusinessComponentInstance instance) throws Exception {
//        long startTime = System.currentTimeMillis();   //获取开始时间
//        try (SqlSession session = sessionTemplate.getSqlSessionFactory().openSession(false)) {
//            logger.debug("openSession:" + String.valueOf(System.currentTimeMillis() - startTime) + "ms");
//            startTime = System.currentTimeMillis();   //获取开始时间
//            BusinessMapper mapper = session.getMapper(BusinessMapper.class);
//            logger.debug("getMapper:" + String.valueOf(System.currentTimeMillis() - startTime) + "ms");
//            startTime = System.currentTimeMillis();   //获取开始时间
//            mapper.insertByBusinessComponent(instance);
//            mapper.insertByQuarkParameter(instance.getQuarkParameter());
//            logger.debug("insertByBusinessComponent:" + String.valueOf(System.currentTimeMillis() - startTime) + "ms");
//
//            for (QuarkComponentInstance item : instance.getItems()) {
//                startTime = System.currentTimeMillis();   //获取开始时间
//                mapper.insertByAtomicComponent(item);
//                logger.debug("insertByAtomicComponent:" + String.valueOf(System.currentTimeMillis() - startTime) +
//                        "ms");
//            }
//            startTime = System.currentTimeMillis();   //获取开始时间
//            session.commit();
//            logger.debug("commit:" + String.valueOf(System.currentTimeMillis() - startTime) + "ms");
//        } catch (Exception ex) {
//            //System.out.println(ex);
//            if (ex instanceof PersistenceException) {
//                throw new BusinessComponentConstraintViolationException(ex.getMessage());
//            } else {
//                throw ex;
//            }
//        }
//    }
    @Override
    public void writeLog(BusinessComponentInstance instance, QuarkComponentLog quarkComponentLog) {
        quarkLogStore.writeLog(instance, quarkComponentLog);
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
//            logger.info("Quark日志:{}", Json.encode(quarkComponentLog));
//            if (quarkServerProperties.isLogEnable()) {
//                mapper.insertByComponentLog(quarkComponentLog);
//            }else{
//                logger.info("Quark日志:{}", Json.encode(quarkComponentLog));
//            }
//            session.commit();
//        } catch (Throwable throwable) {
//            logger.error(ExceptionUtils.getStackTrace(throwable));
//            throw throwable;
//        }
    }

    @Override
    public void writeLog(BusinessComponentInstance instance, List<QuarkComponentLog> quarkComponentLogs) throws IOException {
        quarkLogStore.writeLog(instance,quarkComponentLogs);
    }


    @Override
    public BusinessComponentInstance load(String serialNo) {

//        SqlSession session = sqlSessionFactory.openSession();
//        BusinessMapper mapper = sessionTemplate.getMapper(BusinessMapper.class);
        //BusinessMapper mapper = session.getMapper(BusinessMapper.class);
        BusinessComponentInstance byName = mapper.findBySerialNo(serialNo);
        if (byName != null) {
            QuarkParameter quarkParameter = mapper.findQuarkParameter(serialNo, 1);
            QuarkParameter quarkParameterContext = mapper.findQuarkParameter(serialNo, 2);
            if (quarkParameter != null) {
                byName.setQuarkParameter(quarkParameter);
            }
            if (quarkParameterContext != null) {
                HashMap hashMap = Json.decodeValue(quarkParameterContext.getParameter(), HashMap.class);
                byName.setOutputParameters(hashMap);
//                byName.setQuarkParameterContext(quarkParameterContext);
            }
            List<QuarkComponentInstance> quarkComponentInstances = mapper.findAtomicComponentInstances
                    (serialNo);
            byName.setItems(quarkComponentInstances);
        }
        return byName;
    }

    @Override
    public List<QuarkComponentInstance> findQuarkComponentInstances(String serialNo) {
//        SqlSession session = serialNo.get.openSession();
        //BusinessMapper mapper = sessionTemplate.getMapper(BusinessMapper.class);
        List<QuarkComponentInstance> quarkComponentInstances = mapper.findAtomicComponentInstances
                (serialNo);
        return quarkComponentInstances;
    }

    @Override
    public void updateBusinessComponentUpdateDateTime(String serialNo, long updateDateTime) {
        try (SqlSession session = sessionTemplate.getSqlSessionFactory().openSession(true)) {
            BusinessMapper mapper = session.getMapper(BusinessMapper.class);
            mapper.updateBusinessComponentUpdateDateTime(serialNo, new Timestamp(updateDateTime));
        } catch (Throwable throwable) {
            logger.error(ExceptionUtils.getStackTrace(throwable));
            throw throwable;
        }

    }

    @Override
    public List<BusinessComponentInstance> findTopNWillCompensationBusiness(Integer n) {
        return null;
    }

    @Override
    public List<String> findTopNWillRetryBusiness(Integer n) {
        PageHelper.startPage(0, n, "updateDateTime");
        //SqlSession session = sqlSessionFactory.openSession();
        BusinessMapper mapper = sessionTemplate.getMapper(BusinessMapper.class);
        return mapper.findTopNWillRetryBusiness(n);
    }

    @Override
    public List<String> findPastTenMinutesWillReBeginBusiness() {
        PageHelper.startPage(0, 500, "updateDateTime");
        //SqlSession session = sqlSessionFactory.openSession();
        //BusinessMapper mapper = sessionTemplate.getMapper(BusinessMapper.class);
        return mapper.findPastTenMinutesWillReBeginBusiness();
    }
}
