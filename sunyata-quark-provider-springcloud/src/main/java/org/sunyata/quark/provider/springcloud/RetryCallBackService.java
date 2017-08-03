package org.sunyata.quark.provider.springcloud;

import com.netflix.hystrix.*;
import feign.Client;
import feign.Feign;
import feign.Target;
import feign.hystrix.HystrixFeign;
import feign.hystrix.SetterFactory;
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

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created by leo on 17/5/9.
 */
@Service
public class RetryCallBackService {
    @Autowired
    FeignContext feignContext;
//    private String path;
//    private String url;

    @Retryable(value = {Exception.class}, maxAttempts = 10, backoff = @Backoff(delay = 8000l, multiplier
            = 2))
    public void callBack(String quarkServiceName, String serialNo, Integer order, ProcessResult run) throws Exception {
        try {
            FeignContext context = feignContext;

            String path = "business/notify";
            if (quarkServiceName == null) {
                throw new IllegalArgumentException("the service name cannot be empty");
            }
            Feign.Builder builder = getBuilder(quarkServiceName); //feign(context);
            String url;
            if (!quarkServiceName.startsWith("http")) {
                url = "http://" + quarkServiceName;
            } else {
                url = quarkServiceName;
            }
            url += cleanPath(path);
            QuarkNotifyClient quarkRemoteClient = loadBalance(builder, context, url,quarkServiceName);
            JsonResponseResult execute = quarkRemoteClient.execute(new QuarkNotifyInfo().setSerialNo(serialNo)
                    .setQuarkIndex(order)
                    .setProcessResult(run));
            if (!Objects.equals(execute.getCode(), JsonResponseResult.Success().getCode())) {
                throw new Exception(execute.getMsg());
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    Feign.Builder getBuilder(String serviceName) {
        HystrixFeign.Builder builder = HystrixFeign.builder().setterFactory(new SetterFactory() {
            @Override
            public HystrixCommand.Setter create(Target<?> target, Method method) {
                return HystrixCommand.Setter
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey(serviceName + "G"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey(serviceName))
                        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(serviceName + "P"))
                        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                .withExecutionIsolationStrategy(
                                        HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                                .withCircuitBreakerEnabled(true)
                                .withCircuitBreakerRequestVolumeThreshold(200)
                                .withExecutionTimeoutEnabled(true)
                                .withExecutionTimeoutInMilliseconds(100000)
                                .withFallbackEnabled(true)
                        )
                        .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.defaultSetter()
                                .withCoreSize(100).withMaxQueueSize(10000).withQueueSizeRejectionThreshold(10000));
            }
        });
        builder.encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder());
        return builder;
    }

    protected <T> QuarkNotifyClient loadBalance(Feign.Builder builder, FeignContext context, String url, String name) {
        Client client = getOptional(context, Client.class, name);
        if (client != null) {
            builder.client(client);
            return builder.target(QuarkNotifyClient.class, url);
        }

        throw new IllegalStateException(
                "No Feign Client for loadBalancing defined. Did you forget to include spring-cloud-starter-ribbon?");
    }

    private String cleanPath(String fpath) {
        String path = fpath.trim();
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

//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getName() {
//        return name;
//    }

    protected <T> T get(FeignContext context, Class<T> type, String name) {
        T instance = context.getInstance(name, type);
        if (instance == null) {
            throw new IllegalStateException("No bean found of type " + type + " for " + name);
        }
        return instance;
    }

    protected <T> T getOptional(FeignContext context, Class<T> type, String name) {
        return context.getInstance(name, type);
    }

//    protected Feign.Builder feign(FeignContext context) {
//        Feign.Builder builder = get(context, Feign.Builder.class)
//                .encoder(new JacksonEncoder())
//                .decoder(new JacksonDecoder());
//        //.contract(get(context, Contract.class));
//        // optional values
//        Logger.Level level = getOptional(context, Logger.Level.class);
//        if (level != null) {
//            builder.logLevel(level);
//        }
//        Retryer retryer = getOptional(context, Retryer.class);
//        if (retryer != null) {
//            builder.retryer(retryer);
//        }
//        ErrorDecoder errorDecoder = getOptional(context, ErrorDecoder.class);
//        if (errorDecoder != null) {
//            builder.errorDecoder(errorDecoder);
//        }
//        Request.Options options = getOptional(context, Request.Options.class);
//        if (options != null) {
//            builder.options(options);
//        }
//        Map<String, RequestInterceptor> requestInterceptors = context.getInstances(
//                this.name, RequestInterceptor.class);
//        if (requestInterceptors != null) {
//            builder.requestInterceptors(requestInterceptors.values());
//        }
//
//
////        if (decode404) {
////            builder.decode404();
////        }
//
//        return builder;
//    }


}
