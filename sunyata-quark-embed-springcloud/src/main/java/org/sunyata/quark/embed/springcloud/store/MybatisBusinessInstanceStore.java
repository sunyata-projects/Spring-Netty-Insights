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

package org.sunyata.quark.embed.springcloud.store;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sunyata.quark.basic.BusinessQueryService;
import org.sunyata.quark.store.*;

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

//    @Autowired
//    @Qualifier("adsSqlSessionFactory")
//    SqlSessionFactory sqlSessionFactory;


    @Override
    public void create(BusinessComponentInstance instance) {
        long startTime = System.currentTimeMillis();   //获取开始时间
        try (SqlSession session = sessionTemplate.getSqlSessionFactory().openSession(false)) {
            logger.info("openSession:" + String.valueOf(System.currentTimeMillis() - startTime) + "ms");
            startTime = System.currentTimeMillis();   //获取开始时间
            BusinessMapper mapper = session.getMapper(BusinessMapper.class);
            logger.info("getMapper:" + String.valueOf(System.currentTimeMillis() - startTime) + "ms");
            startTime = System.currentTimeMillis();   //获取开始时间
            mapper.insertByBusinessComponent(instance);
            logger.info("insertByBusinessComponent:" + String.valueOf(System.currentTimeMillis() - startTime) + "ms");

            for (QuarkComponentInstance item : instance.getItems()) {
                startTime = System.currentTimeMillis();   //获取开始时间
                mapper.insertByAtomicComponent(item);
                logger.info("insertByAtomicComponent:" + String.valueOf(System.currentTimeMillis() - startTime) + "ms");
            }
            startTime = System.currentTimeMillis();   //获取开始时间
            session.commit();
            logger.info("commit:" + String.valueOf(System.currentTimeMillis() - startTime) + "ms");
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void writeLog(BusinessComponentInstance instance, QuarkComponentLog quarkComponentLog) {
        try (SqlSession session = sessionTemplate.getSqlSessionFactory().openSession(false)) {
            BusinessMapper mapper = session.getMapper(BusinessMapper.class);
            mapper.updateBusinessComponent(instance);
            instance.getItems().forEach(mapper::updateAtomicComponent);
            mapper.insertByComponentLog(quarkComponentLog);
            session.commit();
        } catch (Throwable throwable) {
            logger.error(ExceptionUtils.getStackTrace(throwable));
            throw throwable;
        }
    }


    @Override
    public BusinessComponentInstance load(String serialNo) {

//        SqlSession session = sqlSessionFactory.openSession();
        BusinessMapper mapper = sessionTemplate.getMapper(BusinessMapper.class);
        //BusinessMapper mapper = session.getMapper(BusinessMapper.class);
        BusinessComponentInstance byName = mapper.findBySerialNo(serialNo);
        if (byName != null) {
            List<QuarkComponentInstance> quarkComponentInstances = mapper.findAtomicComponentInstances
                    (serialNo);
            byName.setItems(quarkComponentInstances);
        }
        return byName;
    }

    @Override
    public List<QuarkComponentInstance> findQuarkComponentInstances(String serialNo) {
//        SqlSession session = serialNo.get.openSession();
        BusinessMapper mapper = sessionTemplate.getMapper(BusinessMapper.class);
        List<QuarkComponentInstance> quarkComponentInstances = mapper.findAtomicComponentInstances
                (serialNo);
        return quarkComponentInstances;
    }

    @Override
    public List<BusinessComponentInstance> findTopNWillCompensationBusiness(Integer n) {
        return null;
    }

    @Override
    public List<BusinessComponentInstance> findTopNWillRetryBusiness(Integer n) {
        PageHelper.startPage(0, n, "updateDateTime");
        //SqlSession session = sqlSessionFactory.openSession();
        BusinessMapper mapper = sessionTemplate.getMapper(BusinessMapper.class);
        return mapper.findTopNWillRetryBusiness(n);
    }
}
