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

package org.sunyata.quark.provider.springcloud.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sunyata.quark.basic.AbstractQuarkComponent;
import org.sunyata.quark.basic.BusinessContext;
import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.provider.springcloud.RetryCallBackService;
import org.sunyata.quark.provider.springcloud.SpringContextUtilForProvider;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * Created by leo on 17/3/30.
 */
@RestController
@RequestMapping("quark")
public class QuarkController {

    Logger logger = LoggerFactory.getLogger(QuarkController.class);
    @Autowired
    RetryCallBackService retryCallBackService;

    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("GithubLookup-");
        executor.initialize();
        return executor;
    }

    @RequestMapping(value = "/run", method = RequestMethod.POST)
    public ProcessResult run(@RequestBody BusinessContext businessContext) throws Exception {
        String quarkName = null;
        try {
            quarkName = (String) businessContext.getCurrentQuarkDescriptor().getOptions().getValue
                    ("quark-name", null);
            if (quarkName == null) {
                String format = String.format("quark名称不能为空,流水号:%s", businessContext.getCurrentQuarkSerialNo());
                logger.error(format);
                return ProcessResult.e().setMessage(format);
            }
            AbstractQuarkComponent bean = (AbstractQuarkComponent) SpringContextUtilForProvider.getBean(quarkName,
                    AbstractQuarkComponent.class);
            ProcessResult run = ProcessResult.r();
            boolean async = businessContext.getCurrentQuarkDescriptor().isAsync();
//            ExecutorService executorService = Executors.newFixedThreadPool(10);
//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            },new Object()).

            if (async) {
//                getAsyncExecutor().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        ProcessResult run = null;
////                        try {
//                        try {
//                            run = bean.run(businessContext);
//                            retryCallBackService.callBack(businessContext.getQuarkServiceName(), businessContext
//                                    .getSerialNo(), businessContext.getCurrentQuarkDescriptor().getOrder(), run);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
////                        } catch (Exception e) {
////                            logger.error(ExceptionUtils.getStackTrace(e));
////                        }
//                    }
//                });
                Observable.fromCallable(new Callable<ProcessResult>() {
                    @Override
                    public ProcessResult call() throws Exception {
                        return bean.run(businessContext);
                    }
                }).observeOn(Schedulers.newThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Action1<ProcessResult>() {
                            @Override
                            public void call(ProcessResult result) {
                                try {
                                    retryCallBackService.callBack(businessContext.getQuarkServiceName(), businessContext
                                            .getSerialNo(), businessContext.getCurrentQuarkDescriptor().getOrder(), result);
                                } catch (Exception e) {
                                    logger.error(ExceptionUtils.getStackTrace(e));
                                }
                            }
                        });
                return run;
            }

            run = bean.run(businessContext);
            logger.info(String.format("业务执行完毕,流水号为:%s", businessContext.getCurrentQuarkSerialNo()));
            return run;

        } catch (BeansException beanException) {
            String logString = String.format("没有找到名称为%s的quark", quarkName);
            logger.error(logString);
            return ProcessResult.r().setMessage(logString);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            return ProcessResult.r().setMessage(ExceptionUtils.getStackTrace(ex));
        }
    }

    private static Callable<Integer> thatReturnsNumberOne() {
        return () -> {
            System.out.println("Observable thread: " + Thread.currentThread().getName());
            Thread.sleep(10000);
            return 1;
        };
    }

    private static Func1<Integer, String> numberToString() {
        return number -> {
            System.out.println("Operator thread: " + Thread.currentThread().getName());
            return String.valueOf(number);
        };
    }

    private static Action1<String> printResult() {
        return result -> {
            System.out.println("Subscriber thread: " + Thread.currentThread().getName());
            System.out.println("Result: " + result);
        };
    }

    public static void main(String[] args) throws IOException {
        Observable.fromCallable(thatReturnsNumberOne())
                .observeOn(Schedulers.newThread())      // operator on different thread
                .map(numberToString())
                //.observeOn(Schedulers.newThread())      // subscriber on different thread
                .subscribeOn(Schedulers.newThread())
                .subscribe(printResult());
        System.out.print("sdfadsf");
        System.in.read();
    }
}
