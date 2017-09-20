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

package org.sunyata.quark.console.store;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.store.BusinessComponentInstance;
import org.sunyata.quark.store.QuarkComponentInstance;
import org.sunyata.quark.store.QuarkComponentLog;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by leo on 17/3/20.
 */
public class SimpleBusinessInstanceStore
         {

    Logger logger = LoggerFactory.getLogger(SimpleBusinessInstanceStore.class);

    //    @Autowired
    BusinessMapper businessMapper;

    public BusinessMapper getBusinessMapper(SqlSession sqlSession) {
        BusinessMapper mapper = sqlSession.getMapper(BusinessMapper.class);
        return mapper;

    }
//    @Autowired
//    SqlSessionTemplate adsSqlSessionTemplate;

    SqlSessionFactory sqlSessionFactory;
    String resource = "mybatis-config.xml";

    public SqlSessionFactory getSqlSessionFactory() throws IOException {
        if (sqlSessionFactory == null) {
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        }
        return sqlSessionFactory;
    }

   // @Override
    public void create(BusinessComponentInstance instance) {
        long startTime = System.currentTimeMillis();   //获取开始时间
        try (SqlSession session = getSqlSessionFactory().openSession(false)) {
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

   // @Override
    public void syncBusinessStatus(BusinessComponentInstance instance, QuarkComponentLog quarkComponentLog) throws IOException {
        try (SqlSession session = getSqlSessionFactory().openSession(false)) {
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

    //@Override
    public void syncBusinessStatus(BusinessComponentInstance instance, List<QuarkComponentLog> quarkComponentLogs) throws IOException {

    }

   // @Override
    public void updateBusinessComponentUpdateDateTime(String serialNo, long updateDateTime) {

    }


    //@Override
    public BusinessComponentInstance load(String serialNo) throws IOException {
        SqlSession session = getSqlSessionFactory().openSession();
        BusinessComponentInstance byName = getBusinessMapper(session).findByName(serialNo);
        if (byName != null) {
            List<QuarkComponentInstance> quarkComponentInstances = getBusinessMapper(session)
                    .findAtomicComponentInstances
                            (serialNo);
            byName.setItems(quarkComponentInstances);
        }
        return byName;
    }

    //@Override
    public List<QuarkComponentInstance> findQuarkComponentInstances(String serialNo) throws IOException {
        SqlSession session = getSqlSessionFactory().openSession();
        List<QuarkComponentInstance> quarkComponentInstances = getBusinessMapper(session).findAtomicComponentInstances
                (serialNo);
        return quarkComponentInstances;
    }

   // @Override
    public List<BusinessComponentInstance> findTopNWillCompensationBusiness(Integer n) {
        return null;
    }

    //@Override
    public List<BusinessComponentInstance> findTopNWillRetryBusiness(Integer n) {
        PageHelper.startPage(0, n, "updateDateTime");
//        return businessMapper.findTopNWillRetryBusiness(n);
        return null;
    }

    //@Override
    public List<BusinessComponentInstance> findPastTenMinutesWillReBeginBusiness() {
        return null;
    }
}
