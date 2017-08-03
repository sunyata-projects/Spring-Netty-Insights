package org.sunyata.quark;

import com.netflix.hystrix.exception.HystrixTimeoutException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.basic.ProcessResult;

import java.util.concurrent.RejectedExecutionException;

/**
 * Created by leo on 17/7/27.
 */
public class NotifyRunCommand extends QuarkCommand {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(NotifyRunCommand.class);
    private Integer quarkIndex;
    private ProcessResult result;

    public NotifyRunCommand(String businName, String quarkName, String serialNo, Integer quarkIndex, ProcessResult
            result,QuarkCommandConfig config) {
        super(businName,quarkName,serialNo,config);
        this.quarkIndex = quarkIndex;

        this.result = result;
    }

    /**
     * 这个方法里面封装了正常的逻辑，我们可以传入正常的业务逻辑
     *
     * @return
     * @throws Exception
     */
    @Override
    protected Object run() throws Exception {
        quarkExecutor.quarkNotify(this.serialNo, quarkIndex, result);
        return null;
    }


    @Override
    protected ProcessResult getFallback() {
        logger.error("Notify FallBack:{}", serialNo);
        Throwable executionException = getExecutionException();
        if (executionException instanceof RejectedExecutionException) {
            messageQueueService.enQueue(businName, quarkName, 30000, serialNo, true);//延时30秒钟最低
        } else if (executionException instanceof RuntimeException) {// short-circuited
            //messageQueueService.enQueue(3 * 60 * 1000, serialNo, true);//延时30秒钟最低
        } else if (executionException instanceof HystrixTimeoutException) {
            messageQueueService.enQueue(businName, quarkName, 30000, serialNo, true);//延时30秒钟最低
        } else {
            logger.error("发生异常,未做处理:{}", serialNo);
        }
        logger.error("Notify Exceptions:{}", ExceptionUtils.getStackTrace(getExecutionException()));
        return ProcessResult.r();

    }

    /**
     * 这个方法中定义了出现异常时, 默认返回的值(相当于服务的降级)。
     *
     * @return
     */
//    @Override
//    protected ProcessResult getFallback() {
//        logger.error("Notify FallBack:{}", serialNo);
//        Throwable executionException = getExecutionException();
//        if (executionException instanceof RejectedExecutionException) {
//            messageQueueService.enQueue(this.businName, 30000, serialNo, quarkIndex, result);//延时30秒钟最低
//        } else if (executionException instanceof RuntimeException) {// short-circuited
//            //messageQueueService.enQueue(30000, serialNo,quarkIndex,result);//延时30秒钟最低
//        } else if (executionException instanceof HystrixTimeoutException) {
//            messageQueueService.enQueue(this.businName, 30000, serialNo, quarkIndex, result);//延时30秒钟最低
//        } else {
//            logger.error("Retry 发生异常,未做处理:{}", serialNo);
//        }
//        logger.error("Notify FallBack Exceptions:{}", ExceptionUtils.getStackTrace(getExecutionException()));
//        return ProcessResult.r();
//    }

}
