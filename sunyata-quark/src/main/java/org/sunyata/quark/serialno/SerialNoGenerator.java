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

package org.sunyata.quark.serialno;

import org.sunyata.quark.descriptor.BusinessComponentDescriptor;
import org.sunyata.quark.descriptor.QuarkComponentDescriptor;
import org.sunyata.quark.ioc.ServiceLocator;

/**
 * Created by leo on 17/3/22.
 */
public interface SerialNoGenerator {
    public static String nextId(QuarkComponentDescriptor quarkComponentDescriptor, BusinessComponentDescriptor
            businComponentDescriptor,
                                String businSerialNo) throws
            IllegalAccessException, InstantiationException {
        SerialNoGenerator service = null;
        try {
            service = ServiceLocator.getLocator().getService(SerialNoGenerator.class);
        } catch (Exception ex) {
            if (service == null) {
                service = new DefaultSerialNoGenerator();
            }
        }
        return service.nextSerialNo(quarkComponentDescriptor, businComponentDescriptor, businSerialNo);
    }

    String nextSerialNo(QuarkComponentDescriptor quarkComponentDescriptor, BusinessComponentDescriptor
            businComponentDescriptor,
                        String businSerialNo) throws
            IllegalAccessException, InstantiationException;
}
