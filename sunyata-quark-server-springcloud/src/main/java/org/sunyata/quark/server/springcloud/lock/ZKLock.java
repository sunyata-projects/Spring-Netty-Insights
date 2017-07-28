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

package org.sunyata.quark.server.springcloud.lock;

import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.sunyata.quark.lock.BusinessLock;

import java.util.concurrent.TimeUnit;

/**
 * Created by leo on 17/3/29.
 */
public class ZKLock implements BusinessLock {
    private InterProcessLock processLock;

    public ZKLock(InterProcessLock processLock) {
        this.processLock = processLock;
    }

    @Override
    public void acquire() throws Exception {
        processLock.acquire();
    }

    @Override
    public boolean acquire(long l, TimeUnit timeUnit) throws Exception {
        return processLock.acquire(l, timeUnit);
    }

    @Override
    public void release() throws Exception {
        processLock.release();
    }

}
