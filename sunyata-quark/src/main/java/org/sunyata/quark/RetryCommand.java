package org.sunyata.quark;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.basic.ProcessResult;

/**
 * Created by leo on 17/7/27.
 */
public class RetryCommand extends HystrixCommand {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(RetryCommand.class);
    private BusinessManager businessManager;
    private String serialNo;

    public RetryCommand(BusinessManager businessManager,String serialNo) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"),100000);
        this.businessManager = businessManager;
        this.serialNo = serialNo;
    }

    /**
     * 这个方法里面封装了正常的逻辑，我们可以传入正常的业务逻辑
     *
     * @return
     * @throws Exception
     */
    @Override
    protected Object  run() throws Exception {
       businessManager.retry(this.serialNo);
        return null;
    }

    /**
     * 这个方法中定义了出现异常时, 默认返回的值(相当于服务的降级)。
     *
     * @return
     */
    @Override
    protected ProcessResult getFallback() {
        return ProcessResult.r();
    }

}
