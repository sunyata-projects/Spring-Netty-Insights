package org.sunyata.quark;

import com.netflix.hystrix.*;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.basic.ProcessResult;

import java.util.concurrent.RejectedExecutionException;

/**
 * Created by leo on 17/7/27.
 */
public class QuarkCommand extends HystrixCommand {
    protected org.slf4j.Logger logger = LoggerFactory.getLogger(QuarkCommand.class);
    protected String businName;
    protected String quarkName;
    protected MessageQueueService messageQueueService;
    protected QuarkExecutor businessManager;
    protected String serialNo;

    public QuarkCommand setBusinessManager(QuarkExecutor businessManager) {
        this.businessManager = businessManager;
        return this;
    }


    public QuarkCommand setMessageQueueService(MessageQueueService messageQueueService) {
        this.messageQueueService = messageQueueService;
        return this;
    }

    public QuarkCommand(String businName, String quarkName, String serialNo, QuarkCommandConfig quarkCommandConfig) {
//        super(HystrixCommandGroupKey.Factory.asKey("RetryGroup"),100000);

        super(
                Setter
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey(quarkName + "G"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey(quarkName))
                        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(quarkName + "P"))
                        .andCommandPropertiesDefaults(HystrixCommandProperties.defaultSetter()
                                .withExecutionIsolationStrategy(
                                        HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                                .withCircuitBreakerEnabled(quarkCommandConfig.isHystrixCommandCircuitBreakerEnable())
                                .withCircuitBreakerRequestVolumeThreshold(quarkCommandConfig
                                        .getHystrixCommandCircuitBreakerRequestVolumeThreshold())
                                .withExecutionTimeoutEnabled(quarkCommandConfig
                                        .isHystrixCommandExecutionTimeoutEnable())
                                .withExecutionTimeoutInMilliseconds(quarkCommandConfig
                                        .getHystrixCommandExecutionTimeoutInMilliseconds())
                                .withFallbackEnabled(true)
                        )
                        .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.defaultSetter()
                                .withCoreSize(quarkCommandConfig.getHystrixCommandThreadPoolCoreSize())
                                .withMaxQueueSize(10000)
                                .withQueueSizeRejectionThreshold(10000))
        );
        if (!quarkName.substring(0, 1).equals("M") && !quarkName.substring(0, 1).equals("N")) {
            logger.error("quarkName:{}", quarkName);
        }
        this.businName = businName;
        this.quarkName = quarkName;
        this.serialNo = serialNo;
    }

    /**
     * 这个方法里面封装了正常的逻辑，我们可以传入正常的业务逻辑
     *
     * @return
     * @throws Exception
     */
    @Override
    protected Object run() throws Exception {
        try {
            //long now = System.currentTimeMillis();
            businessManager.run(this.serialNo);
            return null;
        } catch (Exception ex) {
            logger.error("ERROR:{}", ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }

    /**
     * 这个方法中定义了出现异常时, 默认返回的值(相当于服务的降级)。
     *
     * @return
     */
    @Override
    protected ProcessResult getFallback() {
        logger.error("Retry FallBack:{}", serialNo);
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
        logger.error("FallBack Exceptions:{}", ExceptionUtils.getStackTrace(getExecutionException()));
        return ProcessResult.r();

    }

}
