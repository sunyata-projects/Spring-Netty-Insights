package org.sunyata.quark.notify;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.basic.BusinessContext;
import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.basic.ProcessResultTypeEnum;
import org.sunyata.quark.descriptor.QuarkComponentDescriptor;
import org.sunyata.quark.executor.AbstractExecutor;
import org.sunyata.quark.json.Json;
import org.sunyata.quark.store.BusinessComponentInstance;
import org.sunyata.quark.store.QuarkComponentInstance;

/**
 * Created by leo on 17/5/9.
 */
public class NotifyExecutor extends AbstractExecutor {
    Logger logger = LoggerFactory.getLogger(NotifyExecutor.class);

    @Override
    public ProcessResult run(BusinessContext businessContext) throws Exception {
        ProcessResult result = ProcessResult.r();
        //BusinessLock lock = obtainBusinessLock(businessContext.getSerialNo());
        //logger.info("获取锁,SerianNo:" + businessContext.getSerialNo());
        //lock.acquire();
        //logger.info("获取锁成功,SerianNo:" + businessContext.getSerialNo());
        try {
            result = execute(businessContext);
            return result;
        } catch (Throwable ex) {
            logger.error("an error occurred while executing business component,SerialNo:" + businessContext.getSerialNo
                    () + ",error message:" +
                    ExceptionUtils.getStackTrace
                    (ex));
            throw ex;
        } finally {
            //lock.release();
            //this.publishContinue(result, businessContext);
        }
    }

    protected ProcessResult execute(BusinessContext businessContext) throws Exception {
        ProcessResult result = ProcessResult.r();//未知
        //boolean syncBusinessStatusFlag = true;
        try {
            BusinessComponentInstance instance = businessContext.getInstance();
            QuarkComponentInstance quarkComponentInstance = instance.getItems().stream().filter(p -> p.getOrderby()
                    == businessContext.getManualComponentIndex())
                    .findFirst().orElse(null);
            result = businessContext.getQuarkNotifyProcessResult();
            if (quarkComponentInstance == null) {
                throw new Exception("通知时失败,从实例中获取Quark实例为空,通知内容:" + Json.encode(result));
            }
            if(quarkComponentInstance.getProcessResult()==ProcessResultTypeEnum.S){
                //此业务步骤已经成功,则不应该进行任何处理
                //syncBusinessStatusFlag = false;
                return result;
            }
            //上一步的状态不是I
            QuarkComponentInstance previousQuarkComponentInstance = instance.getItems().stream().filter(p -> p
                    .getOrderby()
                    == businessContext.getManualComponentIndex() - 1)
                    .findFirst().orElse(null);
            if (previousQuarkComponentInstance != null && previousQuarkComponentInstance.getProcessResult() ==
                    ProcessResultTypeEnum.I) {
                throw new Exception("通知时失败,此业务还没有执行到此步骤,有可能是业务执行超时,业务状态没有落盘造成的,通知内容:" + Json.encode(result));
            }
            result = businessContext.getQuarkNotifyProcessResult();
            result.setQuarkComponentInstance(quarkComponentInstance);
            QuarkComponentDescriptor quarkComponentDescriptor = businessContext.getBusinessComponent().getFlow()
                    .getQuarkComponentDescriptor(quarkComponentInstance
                            .getQuarkName(), quarkComponentInstance.getOrderby(), quarkComponentInstance.getSubOrder());
            result.setQuarkComponentDescriptor(quarkComponentDescriptor);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            throw ex;
        } finally {
            try {
                if (result.getQuarkComponentInstance() != null) {
                    businessContext.getBusinessComponent().stateSync(businessContext, result);
                    syncBusinessStatus(businessContext, result);
                } else {
                    logger.error("此业务不能继续");
                }
            } catch (Exception ex) {
                //todo 写库失败后,要写入日志文件 lcl
                throw ex;
            }
        }
        return result;
    }
}
