package org.sunyata.quark.provider.springcloud;

import feign.Headers;
import feign.RequestLine;
import org.sunyata.quark.basic.QuarkNotifyInfo;
import org.sunyata.quark.provider.springcloud.controller.JsonResponseResult;

/**
 * Created by leo on 17/4/10.
 */
public interface QuarkNotifyClient {
    @RequestLine("POST")
    @Headers("Content-Type: application/json")
        //JsonResponseResult execute(String serialNo, Integer quarkIndex, String processResultString);
    JsonResponseResult execute(QuarkNotifyInfo quarkNotifyInfo);
}
