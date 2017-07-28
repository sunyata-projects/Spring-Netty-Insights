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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.basic.*;
import org.sunyata.quark.descriptor.BusinessComponentDescriptor;
import org.sunyata.quark.exception.CanNotFindAnnotationException;
import org.sunyata.quark.exception.CanNotFindComponentException;
import org.sunyata.quark.exception.CanNotFindComponentInstanceException;
import org.sunyata.quark.ioc.DefaultServiceLocatorFactory;
import org.sunyata.quark.ioc.ServiceLocator;
import org.sunyata.quark.json.Json;
import org.sunyata.quark.json.JsonObject;
import org.sunyata.quark.lock.BusinessLock;
import org.sunyata.quark.lock.BusinessLockService;
import org.sunyata.quark.notify.FastExecutor;
import org.sunyata.quark.notify.NotifyExecutor;
import org.sunyata.quark.publish.DefaultEventPublisherFactory;
import org.sunyata.quark.publish.EventPublisher;
import org.sunyata.quark.stereotype.BusinessComponent;
import org.sunyata.quark.store.*;
import org.sunyata.quark.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by leo on 16/12/14.
 */
public abstract class AbstractBusinessManager implements BusinessManager {


    Logger logger = LoggerFactory.getLogger(AbstractBusinessManager.class);

    private static ConcurrentMap<String, Class<? extends AbstractBusinessComponent>> maps = new ConcurrentHashMap<>();
    //private String scanPackage;
    private Collection<Object> names;


    protected ExecutorService executorService;

    @Override
    public void initialize(ExecutorService executor) {
        this.executorService = executor;
    }

    @Override
    public void initialize(Collection<Object> names) throws Exception {
        this.names = names;
        initializeComponent();
    }

    protected void initializeComponent() throws Exception {
        for (Object name : names) {
            Class aClass = name.getClass();
            if (aClass != null) {
                register(aClass);
            }
        }
    }

    @Override
    public void create(String serialNo, String businName, String sponsor, String relationId, String parameterString,
                       boolean autoRun) throws Exception {
        try {
            if (parameterString == null || parameterString.trim().length() == 0) {
                throw new IllegalArgumentException("创建业务组件时,参数不能为空,参数名称:parameterString");
            }
            try {
                new JsonObject(parameterString);
            } catch (Exception ex) {
                throw ex;
            }
//        long startTime = System.currentTimeMillis();   //获取开始时间
            BusinessInstanceStore bestService = ServiceLocator.getBestService(BusinessInstanceStore.class);
            AbstractBusinessComponent abstractBusinessComponent = getBusinessComponent(businName);
            if (abstractBusinessComponent == null) {
                throw new CanNotFindComponentException("创建业务组件实例失败,原因为业务组件不存在" + businName + ",创建参数为:" +
                        parameterString);

            }
            BusinessComponentInstance instance = BusinessInstanceFactory.createInstance(serialNo, sponsor, relationId,
                    parameterString, abstractBusinessComponent);
//        long endTime = System.currentTimeMillis();   //获取开始时间
//        logger.info("创建实例时间:"+String.valueOf(endTime - startTime) + "ms");
            bestService.create(instance);
            logger.info("创建业务组件实例成功,创建参数为:{}", parameterString);
            if (autoRun) {
                run(serialNo);
            }
        } catch (Exception ex) {
            logger.error("创建业务组件失败:{}", ExceptionUtils.getStackTrace(ex));
            throw ex;
        }
    }

    @Override
    public void create(String serialNo, String businName, String sponsor, String relationId, String parameterString)
            throws Exception {
        create(serialNo, businName, sponsor, relationId, parameterString, false);
    }

    public void run(String serialNo) throws Exception {
        runFastWithParameter(serialNo,true);
    }


    @Override
    public void quarkNotify(String serialNo, Integer quarkIndex, ProcessResult result) throws Exception {
        logger.info("[Quark结果通知]SerialNO:" + serialNo + ",QuarkIndex:" + quarkIndex + ",Result:" + Json.encode
                (result));
        BusinessLock lock = obtainBusinessLock(serialNo);
        logger.info("[Quark结果通知]获取锁,SerianNo:" + serialNo);
        lock.acquire(3, TimeUnit.SECONDS);
        BusinessContext context = null;
        ProcessResult run = null;
        try {
            BusinessInstanceLoader bestService = ServiceLocator.getBestService(BusinessInstanceLoader.class);
            BusinessComponentInstance instance = bestService.load(serialNo);
            if (instance == null) {
                logger.error("[Quark结果通知]组件实例不存在:SerialNO:" + serialNo);
                throw new CanNotFindComponentInstanceException("businessInstance cannot be found");
            }
            instance.readOriginalHashCode();
            AbstractBusinessComponent abstractBusinessComponent = getBusinessComponent(instance.getBusinName());
            if (abstractBusinessComponent == null) {
                logger.error("[Quark结果通知]组件不存在,Name:" + instance.getBusinName());
                throw new CanNotFindComponentException("没有找到组件");
            }

            context = BusinessContext.getContext(serialNo, abstractBusinessComponent, instance);
            context.setPrimary(true);
            context.setBusinessMode(instance.getBusinessMode());
            context.setManualComponentIndex(quarkIndex);
            context.setQuarkNotifyProcessResult(result);
            run = new NotifyExecutor().run(context);
        } finally {
            lock.release();
        }
        publishContinue(run, context);
    }

    public ProcessResult runByManual(String serialNo, int quarkIndex, String parameterString) throws
            Exception {
        BusinessLock lock = obtainBusinessLock(serialNo);
        logger.info("获取锁,SerianNo:" + serialNo);
        lock.acquire();
        ProcessResult run = ProcessResult.r();
        BusinessContext context = null;
        try {
            BusinessInstanceLoader bestService = ServiceLocator.getBestService(BusinessInstanceLoader.class);
            BusinessComponentInstance instance = bestService.load(serialNo);
            if (instance == null) {
                throw new CanNotFindComponentInstanceException("组件实例不存在,SerialNO:" + serialNo);
            }
            if (!StringUtils.isEmpty(parameterString)) {
                HashMap hashMap = Json.decodeValue(parameterString, HashMap.class);
                for (Object key : hashMap.keySet()) {
                    instance.setOutputParameter(String.valueOf(key), hashMap.getOrDefault(key, null));
                }
            }


            instance.readOriginalHashCode();
            AbstractBusinessComponent abstractBusinessComponent = getBusinessComponent(instance.getBusinName());
            if (abstractBusinessComponent == null) {
                throw new CanNotFindComponentInstanceException("组件实例不存在,SerialNO:" + serialNo);
            }

            context = BusinessContext.getContext(serialNo, abstractBusinessComponent, instance);
            context.setManualComponentIndex(quarkIndex);
            context.setPrimary(true);
            context.setBusinessMode(instance.getBusinessMode());
            run = abstractBusinessComponent.getExecutor().run(context);
            return run;
        } finally {
            lock.release();
        }
    }

    private void runFastWithParameter(String serialNo, boolean isPrimary) throws Exception {
        logger.debug("quark业务组件开始执行,SerianNo:" + serialNo);
        BusinessLock lock = obtainBusinessLock(serialNo);
        logger.debug("quark业务组件开始执行,获取锁,SerianNo:" + serialNo);
        lock.acquire();
        logger.debug("quark业务组件开始执行,获取锁成功,SerianNo:" + serialNo);

        BusinessComponentInstance instance = null;
        BusinessContext context = null;
        List<ProcessResult> results = new ArrayList<>();
        try {
            BusinessInstanceLoader bestService = ServiceLocator.getBestService(BusinessInstanceLoader.class);
            instance = bestService.load(serialNo);
            if (instance.getCanContinue() == CanContinueTypeEnum.CanNotContinue) {
                return;
            }
//            if(instance.isNeedToRetry()&&isPrimary){
//                return;
//            }
            AbstractBusinessComponent abstractBusinessComponent = getBusinessComponent(instance.getBusinName());
            context = BusinessContext.getContext(serialNo, abstractBusinessComponent, instance);
            context.setPrimary(isPrimary);
            context.setBusinessMode(instance.getBusinessMode());
            int stepCount = context.getInstance().getItems().size();
            int i = 0;
            while (true) {
                ProcessResult run = new FastExecutor().run(context);
                if (run.getProcessResultType() == ProcessResultTypeEnum.N) {//此模式不能继续
                    if (isPrimary != context.isPrimary()) {//已经被改变过,不用再改变,会引起死循环
                        break;
                    }
                    //如果辅助线程,则换成主线程
                    //如果是主线程为什么不切换到辅助线程是因为重试需要一个时间等待,所以只有辅助时才切换
                    if (!isPrimary) {
                        context.setPrimary(!isPrimary);
                        continue;
                    }
                }
                results.add(run);
                saveInstanceOutputParameters(context.getInstance(), run);
                i++;
                logger.debug("quark执行第{}步", i);

                if (instance.getCanContinue() != CanContinueTypeEnum.CanContinue) {//业务不能继续
                    logger.debug("业务不能继续,循环Break");
                    break;
                }

                if (run.getProcessResultType() == ProcessResult.r().getProcessResultType()) {//如果返回存疑
                    if (!isPrimary) {//如果是重试模式
                        logger.debug("返回存疑并且重试模式,循环Break");
                        break;
                    }
                }
                QuarkComponentInstance quarkComponentInstance = context.getBusinessComponent().getFlow()
                        .selectQuarkComponentInstance(context);
                if (quarkComponentInstance == null) {//当前模式无法继续
                    logger.debug("quarkComponentInstance为空,循环Break");
                    break;
                }
                if (i > stepCount) {//保险起见,避免无限循环
                    logger.debug("超过最大循环次数,循环Break");
                    break;
                }
            }
        } finally {
            //写日志
            logger.debug("开始写日志,日志条数:{}", results.size());
            if (results.size() > 0) {
                writeLog(context, results);
            }
            logger.debug("开始释放锁");
            lock.release();
        }

    }



    @Override
    public void retry() throws Exception {
        BusinessQueryService bestService = ServiceLocator.getBestService(BusinessQueryService.class);
        List<String> topNWillRetryBusiness = bestService.findTopNWillRetryBusiness(500);
        logger.info("业务组件实例重试数量:{}", topNWillRetryBusiness.size());
        BusinessInstanceStore businessInstanceStore = ServiceLocator.getBestService(BusinessInstanceStore.class);
        for (String serialNo : topNWillRetryBusiness) {
            logger.debug("重试业务更新流水号为{}的业务组件最后时间", serialNo);
            businessInstanceStore.updateBusinessComponentUpdateDateTime(serialNo, System.currentTimeMillis());
            logger.info("开始重试,业务组件流水号:{},重试中...", serialNo);
            new RetryCommand(this, serialNo).queue();
            //retry(serialNo);

        }
    }

    @Override
    public void retry(String serialNo) throws Exception {
        logger.info("[Quark业务重试]SerianNo:" + serialNo);
        runFastWithParameter(serialNo, false);
        logger.info("[Quark业务重试完成]SerianNo:" + serialNo);
    }

    @Override
    public void reBegin() throws Exception {
        BusinessQueryService bestService = ServiceLocator.getBestService(BusinessQueryService.class);
        List<String> topNWillRetryBusiness = bestService.findPastTenMinutesWillReBeginBusiness();
        logger.info("业务组件实例重新开始数量:{}", topNWillRetryBusiness.size());
        BusinessInstanceStore businessInstanceStore = ServiceLocator.getBestService(BusinessInstanceStore.class);
        for (String serialNo : topNWillRetryBusiness) {
            logger.info("重开job更新流水号为{}业务组件最后时间", serialNo);
            businessInstanceStore.updateBusinessComponentUpdateDateTime(serialNo, System.currentTimeMillis());
            new ReRunCommand(this, serialNo).queue();
        }
    }

    public void compensate(String serialNo) throws Exception {
        BusinessInstanceLoader bestService = ServiceLocator.getBestService(BusinessInstanceLoader.class);
        BusinessComponentInstance instance = bestService.load(serialNo);
        if (instance.getBusinessMode() != BusinessModeTypeEnum.Compensation) {
            throw new Exception("此业务不可补偿");
        }
        instance.readOriginalHashCode();
        AbstractBusinessComponent abstractBusinessComponent = getBusinessComponent(instance.getBusinName());
        if (abstractBusinessComponent == null) {
            throw new CanNotFindComponentInstanceException("组件实例不存在,SerialNO:" + serialNo);
        }

        BusinessContext context = BusinessContext.getContext(serialNo, abstractBusinessComponent, instance);

        abstractBusinessComponent.getExecutor().run(context);
    }
    protected void writeLog(BusinessContext businessContext, List<ProcessResult> results) throws
            InstantiationException,
            IllegalAccessException, IOException {
        BusinessInstanceStore businessInstanceStore = ServiceLocator.getBestService(BusinessInstanceStore
                .class);
        List<QuarkComponentLog> logs = new ArrayList<>();
        for (ProcessResult result : results) {
            QuarkComponentInstance quarkComponentInstance = result.getQuarkComponentInstance();
//            HashMap<String, Object> outputParameterMaps = result.getOutputParameterMaps();
//            if (outputParameterMaps != null) {
//                businessContext.getInstance().setOutputParameters(outputParameterMaps);
//            }
            QuarkComponentLog quarkComponentLog = BusinessInstanceFactory.createQuarkComponentLog(
                    quarkComponentInstance.getBusinSerialNo(),
                    quarkComponentInstance.getSerialNo(),
                    quarkComponentInstance.getQuarkName(),
                    quarkComponentInstance.getVersion(),
                    quarkComponentInstance.getQuarkFriendlyName(),
                    result.getProcessResultType(),
                    "",
                    result.getMessage(), String.valueOf(result.getTotalMillis()));
            logs.add(quarkComponentLog);
        }
        businessInstanceStore.writeLog(businessContext.getInstance(), logs);
    }
    private AbstractBusinessComponent getBusinessComponent(String businName) throws IllegalAccessException,
            InstantiationException {
        Class<? extends AbstractBusinessComponent> orDefault = maps.getOrDefault(businName, null);
        if (orDefault != null) {
            AbstractBusinessComponent service = ServiceLocator.getLocator().getService(maps.getOrDefault(businName,
                    null));
            return service;
        }
        return null;
    }

    @Override
    public <T extends AbstractBusinessComponent> void register(Class<T> businessComponentClazz) throws Exception {
        BusinessComponent annotation = businessComponentClazz.getAnnotation(BusinessComponent.class);
        if (annotation == null) {
            throw new CanNotFindAnnotationException("业务组件没有定义标注");
        }
        if (maps.getOrDefault(annotation.businName(), null) == null) {
            logger.info("注册组件:{}", businessComponentClazz.getName());
            maps.put(annotation.businName(), businessComponentClazz);
        }
    }


    @Override
    public <T extends ServiceLocator> void setServiceLocator(Class<T> serviceLocator) {
        DefaultServiceLocatorFactory.setServiceLocator(serviceLocator);
    }

    @Override
    public <T extends EventPublisher> void setEventPublisher(Class<T> eventPublisherClass) {
        DefaultEventPublisherFactory.setEventPublisher(eventPublisherClass);
    }

    @Override
    public List<BusinessComponentDescriptor> getComponents() throws Exception {
        List<BusinessComponentDescriptor> results = new ArrayList<>();
        for (Class<? extends AbstractBusinessComponent> cls : maps.values()) {
            AbstractBusinessComponent service = ServiceLocator.getLocator().getService(cls);
            results.add(service.getBusinessComponentDescriptor());
        }
        return results;
    }

    protected BusinessLock obtainBusinessLock(String path) throws Exception {
        BusinessLockService bestService = ServiceLocator.getBestService(BusinessLockService.class);
        return bestService.getLock("lock_" + path);
    }

    private void saveInstanceOutputParameters(BusinessComponentInstance instance, ProcessResult run) {
        HashMap<String, Object> outputParameterMaps = run.getOutputParameterMaps();
        if (outputParameterMaps != null && outputParameterMaps.size() > 0) {
            HashMap<String, Object> outputParameters = instance.getOutputParameters();
            if (outputParameters == null) {
                outputParameters = new HashMap<>();
            }
            for (String key : outputParameterMaps.keySet()) {
                outputParameters.put(key, outputParameterMaps.getOrDefault(key, ""));
            }
            instance.setOutputParameters(outputParameters);
        }
    } protected void publishContinue(ProcessResult result, BusinessContext businessContext) throws Exception {
        //publish next quarkComponent
        this.executorService.execute((Runnable) () -> {
            if (result.getQuarkComponentInstance() != null) {
                if (businessContext.getInstance().getCanContinue() == CanContinueTypeEnum.CanContinue) {
                    try {
                        EventPublisher.getPublisher().publish(businessContext.getSerialNo());
                    } catch (Exception e) {
                        logger.error(ExceptionUtils.getStackTrace(e));
                    }
                }
            }
        });

    }

}
