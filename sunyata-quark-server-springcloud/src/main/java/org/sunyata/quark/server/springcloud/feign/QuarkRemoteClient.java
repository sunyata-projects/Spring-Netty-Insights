package org.sunyata.quark.server.springcloud.feign;

import feign.Headers;
import feign.RequestLine;
import org.sunyata.quark.basic.BusinessSerializableContext;
import org.sunyata.quark.basic.ProcessResult;

/**
 * Created by leo on 17/4/10.
 */
public interface QuarkRemoteClient {
    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    ProcessResult execute(BusinessSerializableContext businessContext);
}
