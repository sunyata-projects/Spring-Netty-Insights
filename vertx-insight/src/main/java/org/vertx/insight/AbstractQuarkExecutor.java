package org.vertx.insight;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.basic.*;
import org.sunyata.quark.exception.CanNotFindComponentException;
import org.sunyata.quark.exception.CanNotFindComponentInstanceException;
import org.sunyata.quark.ioc.ServiceLocator;
import org.sunyata.quark.json.Json;
import org.sunyata.quark.json.JsonObject;
import org.sunyata.quark.lock.BusinessLock;
import org.sunyata.quark.lock.BusinessLockService;
import org.sunyata.quark.notify.FastExecutor;
import org.sunyata.quark.notify.NotifyExecutor;
import org.sunyata.quark.publish.EventPublisher;
import org.sunyata.quark.store.*;
import org.sunyata.quark.util.DateUtils;
import org.sunyata.quark.util.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by leo on 17/8/2.
 */
public class AbstractQuarkExecutor implements QuarkExecutor {
    Logger logger = LoggerFactory.getLogger(AbstractQuarkExecutor.class);
    private BusinessManager businessManager;
    private MessageQueueService messageQueueService;

    public AbstractQuarkExecutor(String serverId) {
        this.serverId = serverId;
    }

    String serverId;

    public MessageQueueService getMessageQueueService() throws InstantiationException, IllegalAccessException {
        if (messageQueueService == null) {
            messageQueueService = ServiceLocator.getBestService(MessageQueueService.class);
        }
        return messageQueueService;
    }

    public BusinessManager getBusinessManager() throws InstantiationException, IllegalAccessException {
        if (businessManager == null) {
            businessManager = ServiceLocator.getBestService(BusinessManager.class);
        }
        return businessManager;
    }


    @Override
    public BusinessComponentInstance create(String serialNo, String businName, String sponsor, String relationId,
                                            String parameterString,
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
            BusinessInstanceStore bestService = ServiceLocator.getBestService(BusinessInstanceStore.class);
            AbstractBusinessComponent abstractBusinessComponent = getBusinessManager().getBusinessComponent(businName);
            if (abstractBusinessComponent == null) {
                throw new CanNotFindComponentException("创建业务组件实例失败,原因为业务组件不存在" + businName + ",创建参数为:" +
                        parameterString);

            }
            BusinessComponentInstance instance = BusinessInstanceFactory.createInstance(serialNo, sponsor, relationId,
                    parameterString, abstractBusinessComponent);
            instance.setServerId(serverId);
            bestService.create(instance);
            logger.info("创建业务组件实例成功,创建参数为:{}", parameterString);
            if (autoRun) {
                run(serialNo);
            }
            return instance;
        } catch (Exception ex) {
            logger.error("创建业务组件失败:{}", ExceptionUtils.getStackTrace(ex));
            throw ex;
        }
    }

    @Override
    public BusinessComponentInstance create(String serialNo, String businName, String sponsor, String relationId,
                                            String parameterString) throws Exception {
        return create(serialNo, businName, sponsor, relationId, parameterString, false);
    }

    public void run(String serialNo) throws Exception {
        runFastWithParameter(serialNo, true);
    }


    @Override
    public void quarkNotify(String serialNo, Integer quarkIndex, ProcessResult result) throws Exception {
        logger.debug("Receive Quark Provider Message: SerialNO:" + serialNo + ",QuarkIndex:" + quarkIndex + "," +
                "Result:" + Json.encode(result));
        BusinessLock lock = obtainBusinessLock(serialNo);
        boolean acquire = lock.acquire(3, TimeUnit.SECONDS);
        if (!acquire) {
            logger.debug("could not acquire the lock ,serialNO:{}", serialNo);
            return;
        }
        BusinessContext context = null;
        ProcessResult run = null;
        try {
            BusinessInstanceStore bestService = ServiceLocator.getBestService(BusinessInstanceStore.class);
            BusinessComponentInstance instance = bestService.load(serialNo);
            if (instance == null) {
                logger.error("business instance {} is not exist", serialNo);
                throw new CanNotFindComponentInstanceException("businessInstance cannot be found");
            }
            instance.readOriginalHashCode();
            AbstractBusinessComponent abstractBusinessComponent = getBusinessManager().getBusinessComponent(instance
                    .getBusinName());
            if (abstractBusinessComponent == null) {
                logger.error("business component {} is not exist", instance.getBusinName());
                throw new CanNotFindComponentException("can not find business component");
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
        boolean acquire = lock.acquire(3, TimeUnit.SECONDS);
        if (!acquire) {
            logger.debug("could not acquire the lock ,serialNO:{}", serialNo);
            return ProcessResult.r().setMessage("could not acquire the lock");
        }
        ProcessResult run = ProcessResult.r();
        BusinessContext context = null;
        try {
            BusinessInstanceStore bestService = ServiceLocator.getBestService(BusinessInstanceStore.class);
            BusinessComponentInstance instance = bestService.load(serialNo);
            if (instance == null) {
                logger.error("business instance {} is not exist", serialNo);
                throw new CanNotFindComponentInstanceException("business instance is not exist");
            }
            if (!StringUtils.isEmpty(parameterString)) {
                HashMap hashMap = Json.decodeValue(parameterString, HashMap.class);
                for (Object key : hashMap.keySet()) {
                    instance.setOutputParameter(String.valueOf(key), hashMap.getOrDefault(key, null));
                }
            }
            instance.readOriginalHashCode();
            AbstractBusinessComponent abstractBusinessComponent = getBusinessManager().getBusinessComponent(instance
                    .getBusinName());
            if (abstractBusinessComponent == null) {
                throw new CanNotFindComponentInstanceException("can not find business component,SerialNO:" + serialNo);
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
        BusinessLock lock = obtainBusinessLock(serialNo);
        boolean acquire = lock.acquire(3, TimeUnit.SECONDS);
        if (!acquire) {
            logger.debug("could not acquire the lock ,serialNO:{}", serialNo);
            return;
        }
        BusinessComponentInstance instance = null;
        BusinessContext context = null;
        List<ProcessResult> results = new ArrayList<>();
        try {
            BusinessInstanceStore bestService = ServiceLocator.getBestService(BusinessInstanceStore.class);
            instance = bestService.load(serialNo);
            if (instance.getCanContinue() == CanContinueTypeEnum.CanNotContinue) {
                return;
            }
            AbstractBusinessComponent abstractBusinessComponent = getBusinessManager().getBusinessComponent(instance
                    .getBusinName());
            context = BusinessContext.getContext(serialNo, abstractBusinessComponent, instance);
            context.setPrimary(isPrimary);
            context.setBusinessMode(instance.getBusinessMode());
            int stepCount = context.getInstance().getItems().size();
            int i = 0;
            while (true) {
                ProcessResult run = new FastExecutor().run(context);
                if (run.getProcessResultType() == ProcessResultTypeEnum.N) {//此模式不能继续
                    if (isPrimary != context.isPrimary()) {//已经被改变过,不用再改变,会引起死循环
                        logger.debug("The property isPrimary is changed,break");
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
                logger.debug("The current execution to step {}", i);

                if (instance.getCanContinue() != CanContinueTypeEnum.CanContinue) {//业务不能继续
                    logger.debug("can not continue,Break");
                    break;
                }

                if (run.getProcessResultType() == ProcessResult.r().getProcessResultType()) {//如果返回存疑
                    if (!isPrimary) {//如果是重试模式
//                        logger.debug("返回存疑并且重试模式,循环Break");
                        break;
                    }
                }
                QuarkComponentInstance quarkComponentInstance = context.getBusinessComponent().getFlow()
                        .selectQuarkComponentInstance(context);
                if (quarkComponentInstance == null) {//当前模式无法继续
//                    logger.debug("quarkComponentInstance为空,循环Break");
                    break;
                }
                if (i > stepCount) {//保险起见,避免无限循环
//                    logger.debug("超过最大循环次数,循环Break");
                    break;
                }
            }
        } finally {
            try {
                //写日志
                logger.debug("start synchronization business state,logs count:{}", results.size());
                if (results.size() > 0) {
                    syncBusinessStatus(context, results);
                }
            } catch (Exception ex) {
                logger.error("An error occurred when writing log:{}", ExceptionUtils.getStackTrace(ex));
            }
            lock.release();
        }
    }


    @Override
    public void retryByServerId(String serverId) throws Exception {
        BusinessQueryService bestService = ServiceLocator.getBestService(BusinessQueryService.class);
        List<BusinessComponentInstance> topNWillRetryBusiness = bestService.findTopNWillRetryBusiness(serverId, 200);
        logger.info("Business component instance number of retries for {}", topNWillRetryBusiness.size());
        BusinessInstanceStore businessInstanceStore = ServiceLocator.getBestService(BusinessInstanceStore.class);
        for (BusinessComponentInstance instance : topNWillRetryBusiness) {
//            logger.debug("重试业务更新流水号为{}的业务组件最后时间", instance.getSerialNo());
            businessInstanceStore.updateBusinessComponentUpdateDateTime(instance.getSerialNo(), System
                    .currentTimeMillis());
            logger.info("retry to execute the business component:{}...", instance.getSerialNo());
            long delay = instance.getCreateDateTime().getTime() - System.currentTimeMillis();
            getMessageQueueService().enQueue(instance.getBusinName(), instance.getBusinName(), delay, instance
                            .getSerialNo(),
                    false);
        }
    }

    @Override
    public void retry(String serialNo) throws Exception {
        runFastWithParameter(serialNo, false);
    }

    @Override
    public void reBeginByServerId(String serverId) throws Exception {
        BusinessQueryService bestService = ServiceLocator.getBestService(BusinessQueryService.class);
        List<BusinessComponentInstance> topNWillRetryBusiness = bestService.findPastTenMinutesWillReBeginBusiness(serverId);
        logger.info("Business component instance number of restart for {}", topNWillRetryBusiness.size());
        BusinessInstanceStore businessInstanceStore = ServiceLocator.getBestService(BusinessInstanceStore.class);
        for (BusinessComponentInstance instance : topNWillRetryBusiness) {
            logger.info("restart to execute the business component:{}...", instance.getSerialNo());
            businessInstanceStore.updateBusinessComponentUpdateDateTime(instance.getSerialNo(), System
                    .currentTimeMillis());
            long delay = instance.getCreateDateTime().getTime() - System.currentTimeMillis();
            getMessageQueueService().enQueue(instance.getBusinName(), instance.getBusinName(), delay, instance
                    .getSerialNo
                            (), true);
        }
    }

    public void compensate(String serialNo) throws Exception {
        BusinessInstanceStore bestService = ServiceLocator.getBestService(BusinessInstanceStore.class);
        BusinessComponentInstance instance = bestService.load(serialNo);
        if (instance.getBusinessMode() != BusinessModeTypeEnum.Compensation) {
            throw new Exception("此业务不可补偿");
        }
        instance.readOriginalHashCode();
        AbstractBusinessComponent abstractBusinessComponent = getBusinessManager().getBusinessComponent(instance
                .getBusinName());
        if (abstractBusinessComponent == null) {
            throw new CanNotFindComponentInstanceException("组件实例不存在,SerialNO:" + serialNo);
        }

        BusinessContext context = BusinessContext.getContext(serialNo, abstractBusinessComponent, instance);

        abstractBusinessComponent.getExecutor().run(context);
    }

    protected void syncBusinessStatus(BusinessContext businessContext, List<ProcessResult> results) throws
            InstantiationException,
            IllegalAccessException, IOException, ParseException {
        BusinessInstanceStore businessInstanceStore = ServiceLocator.getBestService(BusinessInstanceStore
                .class);
        List<QuarkComponentLog> logs = new ArrayList<>();
        for (ProcessResult result : results) {
            QuarkComponentInstance quarkComponentInstance = result.getQuarkComponentInstance();
            if (quarkComponentInstance == null) {
                continue;
            }

            QuarkComponentLog quarkComponentLog = BusinessInstanceFactory.createQuarkComponentLog(
                    quarkComponentInstance.getBusinSerialNo(),
                    quarkComponentInstance.getSerialNo(),
                    quarkComponentInstance.getQuarkName(),
                    quarkComponentInstance.getVersion(),
                    quarkComponentInstance.getQuarkFriendlyName(),
                    result.getProcessResultType(),
                    Thread.currentThread().getName(),
                    result.getMessage(),
                    DateUtils.longToString(result.getBeginMillis(), "yyyy-MM-dd HH:mm:ss"),
                    String.valueOf(result.getTotalMillis()));
            logs.add(quarkComponentLog);
        }
//        ProcessResult processResult = results.stream()
//                .filter(p -> p.getQuarkComponentInstance() != null)
//                .sorted((o1, o2) -> {
//                    if (Objects.equals(o1.getQuarkComponentInstance().getOrderby(), o2.getQuarkComponentInstance()
//                            .getOrderby())) {
//                        return o1.getQuarkComponentInstance().getSubOrder() - o2.getQuarkComponentInstance()
//                                .getSubOrder();
//                    } else {
//                        return o1.getQuarkComponentInstance().getOrderby() - o2.getQuarkComponentInstance()
//                                .getOrderby();
//                    }
//                }).findFirst().orElse(null);
//        int priority = calculatPriority(processResult, processResult.getQuarkComponentInstance()
//                .getExecuteTimes());
//        businessContext.getInstance().setPriority(priority);

        businessInstanceStore.syncBusinessStatus(businessContext.getInstance(), logs);
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
    }

    ExecutorService executorService = Executors.newFixedThreadPool(5);

    protected void publishContinue(ProcessResult result, BusinessContext businessContext) throws Exception {

        //publish next quarkComponent
//        if (result.getQuarkComponentInstance() != null) {
//            if (businessContext.getInstance().getCanContinue() == CanContinueTypeEnum.CanContinue) {
//                try {
//                    EventPublisher.getPublisher().publish(businessContexnot.getInstance().getBusinName(),
//                            businessContext
//                                    .getInstance().getSerialNo());
//                } catch (Exception e) {
//                    logger.error(ExceptionUtils.getStackTrace(e));
//                }
//            }
//        }
        this.executorService.execute((Runnable) () -> {
            if (result.getQuarkComponentInstance() != null) {
                if (businessContext.getInstance().getCanContinue() == CanContinueTypeEnum.CanContinue) {
                    try {
                        EventPublisher.getPublisher().publish(businessContext.getInstance().getBusinName(),
                                businessContext
                                        .getInstance().getSerialNo());
                    } catch (Exception e) {
                        logger.error(ExceptionUtils.getStackTrace(e));
                    }
                }
            }
        });
    }
}
