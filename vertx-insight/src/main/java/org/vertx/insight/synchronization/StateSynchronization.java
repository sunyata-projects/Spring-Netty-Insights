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

package org.vertx.insight.synchronization;

import org.sunyata.quark.basic.AbstractBusinessComponent;
import org.sunyata.quark.stereotype.BusinessComponent;
import org.sunyata.quark.basic.BusinessContext;
import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.ioc.ServiceLocator;

/**
 * Created by leo on 17/3/22.
 */
public interface StateSynchronization {
    public static StateSynchronization getSynchronizer(Class<? extends AbstractBusinessComponent>
                                                                 businessComponentClazz) throws
            IllegalAccessException, InstantiationException {
        BusinessComponent annotation = businessComponentClazz.getAnnotation(BusinessComponent.class);
        StateSynchronization service = null;
        try {
            service = ServiceLocator.getLocator().getService(annotation.synchronizer());
        } catch (Exception ex) {

        }
        if (service == null) {
            service = new DefaultStateSynchronization();
        }
        return service;
    }

    void stateSync(AbstractBusinessComponent businessComponent, BusinessContext businessContext, ProcessResult
            processResult) throws Exception;
}
