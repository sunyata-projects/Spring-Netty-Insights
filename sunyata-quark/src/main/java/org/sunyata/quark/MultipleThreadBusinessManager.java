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

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.sunyata.quark.basic.ProcessResult;

/**
 * Created by leo on 16/12/14.
 */
public class MultipleThreadBusinessManager extends AbstractBusinessManager {
    //private ExtendableThreadPoolExecutor executorService;
    //private ExecutorService executorService;

//    public MultipleThreadBusinessManager setMaximumPoolSize(int maximumPoolSize) {
//        //this.maximumPoolSize = maximumPoolSize;
//        return this;
//    }

    //private int maximumPoolSize = 16;

    public MultipleThreadBusinessManager() throws Exception {

        super();
    }


//    public void initialize(ExecutorService executor) {
//        //executorService = Executors.newFixedThreadPool(maximumPoolSize);
//        executorService = executor;
//        //ThreadFactory factory = new QuarkThreadFactory();
////        executorService = new ExtendableThreadPoolExecutor(0, maximumPoolSize, 2, TimeUnit.MINUTES, new TaskQueue(),
////                factory);
//    }

    @Override
    public void create(String serialNo, String businName, String sponsor, String relationId, String parameterString)
            throws Exception {
        executorService.submit((Runnable) () -> {
            try {
                internalCreate(serialNo, businName, sponsor, relationId, parameterString);
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }).get();
    }

    @Override
    public void quarkNotify(String serialNo, Integer quarkIndex, ProcessResult result) throws Exception {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    internalQuarkNotify(serialNo, quarkIndex, result);
                } catch (Exception e) {
                    logger.error(ExceptionUtils.getStackTrace(e));
                }
            }
        });

    }

    void internalQuarkNotify(String serialNo, int quarkIndex, ProcessResult result) throws Exception {
        super.quarkNotify(serialNo, quarkIndex, result);
    }

    @Override
    public void create(String serialNo, String businName, String sponsor, String relationId, String parameterString,
                       boolean autoRun) throws Exception {
        executorService.execute((Runnable) () -> {
            try {
                internalCreate(serialNo, businName, sponsor, relationId, parameterString, autoRun);
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        });
    }

    private void internalCreate(String serialNo, String businName, String sponsor, String relationId, String
            parameterString) throws
            Exception {
        super.create(serialNo, businName, sponsor, relationId, parameterString);
    }

    private void internalCreate(String serialNo, String businName, String sponsor, String relationId, String
            parameterString, boolean isAutoRun) throws
            Exception {
        super.create(serialNo, businName, sponsor, relationId, parameterString, isAutoRun);
    }

    @Override
    public void run(String serialNo) throws Exception {
//        executorService.execute(() -> {
//            try {
//                internalRun(serialNo);
//            } catch (Exception e) {
//                logger.error(ExceptionUtils.getStackTrace(e));
//            }
//        });
        new PublicRunCommand(this,serialNo).queue();
    }



    public void internalRun(String serialNo) throws Exception {
        //logger.info("current thread(internalRun):{}", Thread.currentThread().getName());
        super.run(serialNo);
    }
//
//    @Override
//    public void retry() throws Exception {
//        BusinessQueryService bestService = ServiceLocator.getBestService(BusinessQueryService.class);
//        List<String> topNWillRetryBusiness = bestService.findTopNWillRetryBusiness(500);
//        logger.info("业务组件实例重试数量:{}", topNWillRetryBusiness.size());
//        BusinessInstanceStore businessInstanceStore = ServiceLocator.getBestService(BusinessInstanceStore.class);
//        for (String serialNo : topNWillRetryBusiness) {
//            logger.debug("重试业务更新流水号为{}的业务组件最后时间", serialNo);
//            businessInstanceStore.updateBusinessComponentUpdateDateTime(serialNo, System.currentTimeMillis());
//            logger.info("开始重试,业务组件流水号:{},重试中...", serialNo);
//            retry(serialNo);
//
//        }
//    }

//    @Override
//    public void reBegin() throws Exception {
//        BusinessQueryService bestService = ServiceLocator.getBestService(BusinessQueryService.class);
//        List<String> topNWillRetryBusiness = bestService.findPastTenMinutesWillReBeginBusiness();
//        logger.info("业务组件实例重新开始数量:{}", topNWillRetryBusiness.size());
//        BusinessInstanceStore businessInstanceStore = ServiceLocator.getBestService(BusinessInstanceStore.class);
//        for (String serialNo : topNWillRetryBusiness) {
//            logger.info("重开job更新流水号为{}业务组件最后时间", serialNo);
//            businessInstanceStore.updateBusinessComponentUpdateDateTime(serialNo, System.currentTimeMillis());
//            run(serialNo);
//
//        }
//
//    }

//    @Override
//    public void retry(String serialNo) throws Exception {
//        new RetryCommand(this,serialNo);
////        executorService.execute(() -> {
////            try {
////                internalRetry(serialNo);
////            } catch (Exception e) {
////                logger.error(ExceptionUtils.getStackTrace(e));
////            }
////        });
//    }

    void internalRetry(String serialNo) throws Exception {
        super.retry(serialNo);
    }


    @Override
    public void compensate(String serialNo) throws Exception {
        super.compensate(serialNo);
    }
}
