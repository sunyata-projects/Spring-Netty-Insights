package org.sunyata.quark.provider.demo.springcloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by leo on 17/5/10.
 */
@Component
public class MyRunner implements CommandLineRunner {
    @Autowired
    RetryCallBackServiceTEst retryCallBackServiceTEst;

    @Override
    public void run(String... args) throws Exception {
        try {
            retryCallBackServiceTEst.callBack("sdfasd", "sdfasd", 33, null);
        } catch (Exception ex) {

        }
    }
}
