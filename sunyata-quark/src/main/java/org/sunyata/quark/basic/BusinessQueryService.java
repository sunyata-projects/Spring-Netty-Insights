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

import org.sunyata.quark.store.QuarkComponentInstance;
import org.sunyata.quark.store.BusinessComponentInstance;

import java.io.IOException;
import java.util.List;

/**
 * Created by leo on 17/3/23.
 */
public interface BusinessQueryService {
    /***
     * 获取将补偿的业务
     * <p>
     * 应按时间,最时间最早的n条件
     *
     * @param n
     * @return
     */
    List<BusinessComponentInstance> findTopNWillCompensationBusiness(Integer n);


    /***
     * 获取将要重试的业务
     * 应按时间,最时间最早的n条件
     *
     * @param n
     * @return
     */
    List<BusinessComponentInstance> findTopNWillRetryBusiness(Integer n);

    List<QuarkComponentInstance> findQuarkComponentInstances(String serialNo) throws IOException;

//    BusinessComponentInstance findBusinessComponentInstance(String serialNo) throws IOException;
}
