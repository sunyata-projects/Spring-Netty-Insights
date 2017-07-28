package org.sunyata.quark.notify;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.basic.BusinessContext;
import org.sunyata.quark.basic.ProcessResult;
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
            logger.info("开始执行,SerianNo:" + businessContext.getSerialNo());
            result = execute(businessContext);
            logger.info("执行完毕,SerianNo:" + businessContext.getSerialNo() + ",Result:" + Json.encode(result));
            return result;
        } catch (Throwable ex) {
            logger.error("出错,SerialNo:" + businessContext.getSerialNo() + ",错误信息:" + ExceptionUtils.getStackTrace(ex));
            throw ex;
        } finally {
            //lock.release();
            //this.publishContinue(result, businessContext);
        }
    }

    protected ProcessResult execute(BusinessContext businessContext) throws Exception {
        ProcessResult result = ProcessResult.r();//未知
        try {
            BusinessComponentInstance instance = businessContext.getInstance();
            QuarkComponentInstance quarkComponentInstance = instance.getItems().stream().filter(p -> p.getOrderby()
                    == businessContext.getManualComponentIndex())
                    .findFirst().orElse(null);
            result = businessContext.getQuarkNotifyProcessResult();
            if (quarkComponentInstance == null) {
                throw new Exception("通知时失败,从实例中获取Quark实例为空,通知内容:" + Json.encode(result));
            }

            result.setQuarkComponentInstance(quarkComponentInstance);
            QuarkComponentDescriptor quarkComponentDescriptor = businessContext.getBusinessComponent().getFlow()
                    .getQuarkComponentDescriptor(quarkComponentInstance
                            .getQuarkName(), quarkComponentInstance.getOrderby(), quarkComponentInstance.getSubOrder());
            result.setQuarkComponentDescriptor(quarkComponentDescriptor);
//            if (businessContext.getQuarkNotifyProcessResult().getProcessResultType() == ProcessResultTypeEnum.S) {
//
//
//            } else if (businessContext.getQuarkNotifyProcessResult().getProcessResultType() == ProcessResultTypeEnum
//                    .R) {
//
//            } else if (businessContext.getQuarkNotifyProcessResult().getProcessResultType() == ProcessResultTypeEnum
//                    .E) {
//
//            }
            // run
            //result = businessContext.getBusinessComponent().run(businessContext);
        } catch (Exception ex) {
//            result.setMessage(ExceptionUtils.getStackTrace(ex));
            logger.error(ExceptionUtils.getStackTrace(ex));
        } finally {
            try {
                if (result.getQuarkComponentInstance() != null) {
                    logger.info("Quark通知更新库内容,SerialNO:" + businessContext.getSerialNo());
                    businessContext.getBusinessComponent().stateSync(businessContext, result);
                    writeLog(businessContext, result);
                    logger.info("Quark通知更新库内容完毕");
                } else {
                    logger.error("此业务不能继续");
                }
            } catch (Exception ex) {
                //todo 写库失败后,要写入日志文件 lcl
                throw ex;
            }
            /*finally {
                // unlock serialNo
                lock.release();
            }*/
        }
        return result;
    }
}
