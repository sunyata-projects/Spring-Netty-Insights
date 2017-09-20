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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunyata.quark.json.Json;
import org.sunyata.quark.server.springcloud.QuarkServerProperties;
import org.sunyata.quark.server.springcloud.exception.BusinessComponentConstraintViolationException;
import org.sunyata.quark.server.springcloud.store.log.QuarkComponentLogStore;
import org.sunyata.quark.store.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

/**
 * Created by leo on 17/3/20.
 */
@Component
public class MybatisBusinessInstanceStore implements BusinessInstanceStore, BusinessQueryService {

    Logger logger = LoggerFactory.getLogger(MybatisBusinessInstanceStore.class);

    @Autowired
    QuarkComponentLogStore quarkComponentLogStore;

    @Autowired
    QuarkServerProperties quarkServerProperties;

    @Autowired
    BusinessMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(BusinessComponentInstance instance) throws Exception {
        try {
//            String serialNo = mapper.findByBusinNameAndRelationId(instance);
            //if (StringUtils.isEmpty(serialNo)) {
            mapper.insertByBusinessComponent(instance);
            mapper.insertByQuarkParameter(instance.getQuarkParameter());
            for (QuarkComponentInstance item : instance.getItems()) {
                mapper.insertByQuarkComponent(item);
            }
            //}
        } catch (Exception ex) {
            if (ex instanceof DuplicateKeyException) {
                throw new BusinessComponentConstraintViolationException(ex.getMessage());
            } else {
                throw ex;
            }
        }
    }


    @Override
    public void syncBusinessStatus(BusinessComponentInstance instance, QuarkComponentLog quarkComponentLog) {
        quarkComponentLogStore.syncBusinessStatus(instance, quarkComponentLog);
    }

    @Override
    public void syncBusinessStatus(BusinessComponentInstance instance, List<QuarkComponentLog> quarkComponentLogs)
            throws IOException {
        quarkComponentLogStore.syncBusinessStatus(instance, quarkComponentLogs);
    }

    @Override
    public void syncBusinessStatus(BusinessComponentInstance instance) {
        quarkComponentLogStore.syncBusinessStatus(instance);

    }


    @Override
    public BusinessComponentInstance load(String serialNo) {
        BusinessComponentInstance businessComponentInstance = mapper.findBySerialNo(serialNo);
        if (businessComponentInstance != null) {
            QuarkParameter quarkParameter = mapper.findQuarkParameter(serialNo, 1);
            QuarkParameter quarkParameterContext = mapper.findQuarkParameter(serialNo, 2);
            if (quarkParameter != null) {
                businessComponentInstance.setQuarkParameter(quarkParameter);
            }
            if (quarkParameterContext != null) {
                HashMap hashMap = Json.decodeValue(quarkParameterContext.getParameter(), HashMap.class);
                businessComponentInstance.setOutputParameters(hashMap);
            }
            List<QuarkComponentInstance> quarkComponentInstances = mapper.findQuarkComponentInstances
                    (serialNo);
            businessComponentInstance.setItems(quarkComponentInstances);
        }
        return businessComponentInstance;
    }

    @Override
    public List<QuarkComponentInstance> findQuarkComponentInstances(String serialNo) {
        List<QuarkComponentInstance> quarkComponentInstances = mapper.findQuarkComponentInstances
                (serialNo);
        return quarkComponentInstances;
    }

    @Override
    public void updateBusinessComponentUpdateDateTime(String serialNo, long updateDateTime) {
        try {
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
    public List<BusinessComponentInstance> findTopNWillRetryBusiness(Integer n) {
        PageHelper.startPage(0, n, "updateDateTime");
        return mapper.findTopNWillRetryBusiness(n);
    }

    @Override
    public List<BusinessComponentInstance> findPastTenMinutesWillReBeginBusiness() {
        PageHelper.startPage(0, 500, "updateDateTime");
        return mapper.findPastTenMinutesWillReBeginBusiness();
    }
}
