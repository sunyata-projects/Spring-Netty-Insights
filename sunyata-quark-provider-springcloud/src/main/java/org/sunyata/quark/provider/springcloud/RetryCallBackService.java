package org.sunyata.quark.provider.springcloud;

import feign.*;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.FeignContext;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.basic.QuarkNotifyInfo;
import org.sunyata.quark.provider.springcloud.controller.JsonResponseResult;

import java.util.Map;
import java.util.Objects;

/**
 * Created by leo on 17/5/9.
 */
@Service
public class RetryCallBackService {
    @Autowired
    FeignContext feignContext;
    private String name;
    private String path;
    private String url;

    @Retryable(value = {Exception.class}, maxAttempts = 10, backoff = @Backoff(delay = 5000l, multiplier
            = 2))
    public void callBack(String quarkServiceName, String serialNo, Integer order, ProcessResult run) throws Exception {
//        System.out.println("do something...");
//        throw new RemoteAccessException("RemoteAccessException....");
        try {
            FeignContext context = feignContext;

            this.setName(quarkServiceName);
            this.setPath("business/notify");
            //this.setUrl((String) options.getValue("url", null));
            if (this.getName() == null) {
                throw new IllegalArgumentException("服务名称不能为空");
            }
            Feign.Builder builder = feign(context);
            if (!StringUtils.hasText(this.url)) {
                String url;
                if (!this.name.startsWith("http")) {
                    url = "http://" + this.name;
                } else {
                    url = this.name;
                }
                url += cleanPath();
                QuarkNotifyClient quarkRemoteClient = loadBalance(builder, context, url);
                JsonResponseResult execute = quarkRemoteClient.execute(new QuarkNotifyInfo().setSerialNo(serialNo)
                        .setQuarkIndex(order)
                        .setProcessResult(run));
                if (!Objects.equals(execute.getCode(), JsonResponseResult.Success().getCode())) {
                    throw new Exception(execute.getMsg());
                }
            } else {
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    protected <T> QuarkNotifyClient loadBalance(Feign.Builder builder, FeignContext context, String url) {
        Client client = getOptional(context, Client.class);
        if (client != null) {
            builder.client(client);
            return builder.target(QuarkNotifyClient.class, url);
        }

        throw new IllegalStateException(
                "No Feign Client for loadBalancing defined. Did you forget to include spring-cloud-starter-ribbon?");
    }

    private String cleanPath() {
        String path = this.path.trim();
        if (StringUtils.hasLength(path)) {
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
        }
        return path;
    }

    @Recover
    public void recover(RemoteAccessException e) {
        System.out.println(e.getMessage());
        System.out.println("recover....");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    protected <T> T get(FeignContext context, Class<T> type) {
        T instance = context.getInstance(this.name, type);
        if (instance == null) {
            throw new IllegalStateException("No bean found of type " + type + " for "
                    + this.name);
        }
        return instance;
    }

    protected <T> T getOptional(FeignContext context, Class<T> type) {
        return context.getInstance(this.name, type);
    }

    protected Feign.Builder feign(FeignContext context) {
        Feign.Builder builder = get(context, Feign.Builder.class)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder());
        //.contract(get(context, Contract.class));
        // optional values
        Logger.Level level = getOptional(context, Logger.Level.class);
        if (level != null) {
            builder.logLevel(level);
        }
        Retryer retryer = getOptional(context, Retryer.class);
        if (retryer != null) {
            builder.retryer(retryer);
        }
        ErrorDecoder errorDecoder = getOptional(context, ErrorDecoder.class);
        if (errorDecoder != null) {
            builder.errorDecoder(errorDecoder);
        }
        Request.Options options = getOptional(context, Request.Options.class);
        if (options != null) {
            builder.options(options);
        }
        Map<String, RequestInterceptor> requestInterceptors = context.getInstances(
                this.name, RequestInterceptor.class);
        if (requestInterceptors != null) {
            builder.requestInterceptors(requestInterceptors.values());
        }


//        if (decode404) {
//            builder.decode404();
//        }

        return builder;
    }


}
