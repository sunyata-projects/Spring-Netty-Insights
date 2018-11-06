package org.sunyata.quark.provider.demo.springcloud;

import org.springframework.remoting.RemoteAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.sunyata.quark.basic.ProcessResult;

/**
 * Created by leo on 17/5/10.
 */
@Service
public class RetryCallBackServiceTEst {

    @Retryable(value = {Exception.class}, maxAttempts = 10, backoff = @Backoff(delay = 5000l, multiplier
            = 1))
    public void callBack(String quarkServiceName, String serialNo, Integer order, ProcessResult run) throws Exception {
        System.out.print(quarkServiceName);
        throw new Exception("sdfasdfa");
    }


    @Recover
    public void recover(RemoteAccessException e) {
        System.out.println(e.getMessage());
        System.out.println("recover....");
    }


}