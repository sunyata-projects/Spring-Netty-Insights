package org.sunyata.quark.client.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by leo on 17/5/10.
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = {"body", "queueNameKey", "exchangeNameKey"})
public class ComplexMessageInfo<T> extends BaseMessageInfo {
    private Integer jobInfoType;

    public String getBodyJsonString() {
        return bodyJsonString;
    }

    public ComplexMessageInfo setBodyJsonString(String bodyJsonString) {
        this.bodyJsonString = bodyJsonString;
        return this;
    }

    private String bodyJsonString;
    private T body;

    public Integer getJobInfoType() {
        return jobInfoType;
    }

    public ComplexMessageInfo setJobInfoType(MessageInfoType jobInfoType) {
        this.jobInfoType = jobInfoType.getValue();
        return this;
    }

    public T getBody() {
        return body;
    }

    public ComplexMessageInfo setBody(T body) {
        this.body = body;
        return this;
    }



//    @Override
//    public String getQueueNameKey() {
//        return "rabbit.queue.mixQueue";
//    }
}


