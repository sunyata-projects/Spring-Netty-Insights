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

package org.sunyata.quark.basic;

import org.sunyata.quark.descriptor.BusinessComponentDescriptor;
import org.sunyata.quark.descriptor.MutltipleQuarkComponentDescriptor;
import org.sunyata.quark.descriptor.QuarkComponentDescriptor;
import org.sunyata.quark.serialno.SerialNoGenerator;
import org.sunyata.quark.store.BusinessComponentInstance;
import org.sunyata.quark.store.QuarkComponentInstance;
import org.sunyata.quark.store.QuarkComponentLog;

import java.sql.Timestamp;

/**
 * Created by leo on 17/3/16.
 */
public class BusinessInstanceFactory {
    public static QuarkComponentLog createQuarkComponentLog(String businSerialNo, String serialNo, String
            businItemCode, String version, String businItemName, ProcessResultTypeEnum processResult, String notes,
                                                            String processResultString) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return new QuarkComponentLog()
                .setBusinSerialNo(businSerialNo)
                .setSerialNo(serialNo)
                .setBusinItemCode(businItemCode)
                .setVersion(version)
                .setBusinItemName(businItemName)
                .setProcessResult(processResult)
                .setNotes(notes)
                .setProcessResultString(processResultString)
                .setCreateDateTime(timestamp);
    }

    public static BusinessComponentInstance createInstance(String serialNo, String parameterString,
                                                           AbstractBusinessComponent component) throws Exception {

        BusinessComponentDescriptor businessComponentDescriptor = component
                .getBusinessComponentDescriptor();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        BusinessComponentInstance result = new BusinessComponentInstance()
                .setSerialNo(serialNo)
                .setBusinName(businessComponentDescriptor.getBisinName())
                .setBusinCode(businessComponentDescriptor.getBusinCode())
                .setDescription(businessComponentDescriptor.getDescription())
                .setVersion(businessComponentDescriptor.getVersion())
                .setCreateDateTime(timestamp)
                .setParameterString(parameterString)
                .setCanContinue(CanContinueTypeEnum.CanContinue)
                .setBusinStatus(BusinessStatusTypeEnum.Initialize)
                .setNeedToRetry(false)
                .setBusinessMode(BusinessModeTypeEnum.Normal)
                .setUpdateDateTime(timestamp);
        Flow flow = component.getFlow();
        ProcessSequencing processSequencing = flow.getProcessSequencing();
        for (MutltipleQuarkComponentDescriptor next : processSequencing) {
            for (QuarkComponentDescriptor quarkComponentDescriptor : next.getItems()) {
                QuarkComponentInstance item = new QuarkComponentInstance();
                item.setCreateDateTime(timestamp)
                        .setVersion(quarkComponentDescriptor.getVersion())
                        .setBusinItemCode(quarkComponentDescriptor.getBusinItemCode())
                        .setBusinItemName(quarkComponentDescriptor.getBusinItemName())
//                        .setCanContinue(CanContinueTypeEnum.CanContinue)
                        .setExecuteTimes(0)
                        .setProcessResult(ProcessResultTypeEnum.I)
                        .setOrderby(quarkComponentDescriptor.getOrder())
                        .setContinueType(quarkComponentDescriptor.getContinueType())
                        .setSubOrder(quarkComponentDescriptor.getSubOrder())
                        .setSerialNo(SerialNoGenerator.nextId(quarkComponentDescriptor,
                                businessComponentDescriptor,
                                serialNo))
                        .setBusinSerialNo(serialNo);
                result.getItems().add(item);
            }
        }
        return result;
    }

}
