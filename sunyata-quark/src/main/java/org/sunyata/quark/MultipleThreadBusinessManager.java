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

package org.sunyata.quark;

import org.sunyata.quark.thread.ExtendableThreadPoolExecutor;
import org.sunyata.quark.thread.QuarkThreadFactory;
import org.sunyata.quark.thread.TaskQueue;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by leo on 16/12/14.
 */
public class MultipleThreadBusinessManager extends AbstractBusinessManager {
    private ExtendableThreadPoolExecutor executorService;

    public MultipleThreadBusinessManager() throws Exception {
        super();
        initExecutorService();
    }

    private void initExecutorService() {
        ThreadFactory factory = new QuarkThreadFactory();
        executorService = new ExtendableThreadPoolExecutor(0, 100, 2, TimeUnit.MINUTES, new TaskQueue(), factory);
    }

    @Override
    public void create(String serialNo, String businCode, String parameterString) throws Exception {
        executorService.submit((Runnable) () -> {
            try {
                internalCreate(serialNo, businCode, parameterString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).get();
    }

    private void internalCreate(String serialNo, String businCode, String parameterString) throws Exception {
        super.create(serialNo, businCode, parameterString);
    }

    @Override
    public void run(String serialNo) throws Exception {
        executorService.submit((Runnable) () -> {
            try {
                internalRun(serialNo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    void internalRun(String serialNo) throws Exception {
        super.run(serialNo);
    }

    @Override
    public void retry(String serialNo) throws Exception {
        executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                internalRetry(serialNo);
                return null;
            }
        });
    }

    void internalRetry(String serialNo) throws Exception {
        super.retry(serialNo);
    }


    @Override
    public void compensate(String serialNo) throws Exception {
        super.compensate(serialNo);
    }
}
