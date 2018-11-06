/*
 *
 *
 *  * Copyright (c) 2017 Leo Lee(lichl.1980@163.com).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy
 *  * of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *
 */

package org.sunyata.quark.server.springcloud;

import com.netflix.hystrix.*;
import feign.*;
import feign.codec.ErrorDecoder;
import feign.hystrix.HystrixFeign;
import feign.hystrix.SetterFactory;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.feign.FeignContext;
import org.springframework.cloud.netflix.feign.ribbon.LoadBalancerFeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.sunyata.quark.basic.AbstractQuarkComponent;
import org.sunyata.quark.basic.BusinessContext;
import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.basic.QuarkComponentOptions;
import org.sunyata.quark.server.springcloud.feign.QuarkRemoteClient;
import org.sunyata.quark.stereotype.QuarkComponent;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by leo on 16/12/14.
 */
@Component
@QuarkComponent(quarkName = "RemoteQuarkComponent", quarkFriendlyName = "RemoteQuarkComponent", version = "1.0")
public class RemoteQuarkComponent extends AbstractQuarkComponent<RemoteQuarkParameterInfo> implements
        ApplicationContextAware {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(RemoteQuarkComponent.class);

    @Value("${spring.application.name}")
    public String quarkServiceName;

    private ApplicationContext applicationContext;
//    private String url;
//
//    public String getPath() {
//        return path;
//    }

//    public RemoteQuarkComponent setPath(String path) {
//        this.path = path;
//        return this;
//    }
//
//    private String path;

//    public String getName() {
//        return name;
//    }
//
//    public RemoteQuarkComponent setName(String name) {
//        this.name = name;
//        return this;
//    }
//
//    private String name;

    @Override
    public RemoteQuarkParameterInfo getParameterInfo(BusinessContext context) throws Exception {
        RemoteQuarkParameterInfo quarkParameterInfo = new RemoteQuarkParameterInfo();
        quarkParameterInfo.setBusinessContext(context);
        return quarkParameterInfo;
    }


//    public String getUrl() {
//        return url;
//    }
//
//    public RemoteQuarkComponent setUrl(String url) {
//        this.url = url;
//        return this;
//    }

    public ProcessResult execute(RemoteQuarkParameterInfo parameterInfo) throws Exception {
        //return new HystrixCommandWrapper(quarkServiceName,applicationContext,parameterInfo).execute();
        String name = parameterInfo.getServiceName();
        String url = parameterInfo.getUrl();
        String path = parameterInfo.getPath();
        String quarkName = null;
        if (org.apache.commons.lang.StringUtils.isEmpty(name)) {
            QuarkComponentOptions options = parameterInfo.getBusinessContext().getCurrentQuarkDescriptor().getOptions();
            quarkName = (String) options.getValue("quark-name", null);
            name = (String) options.getValue("name", null);
            path = (String) options.getValue("path", null);
            url = (String) options.getValue("url", null);
        }

//        logger.info("quarkProvider name:{},path:{},url:{}", name, path, url);
        logger.info("current service name:{},target quark provider service name:{},target quark name:{}",
                quarkServiceName,
                name,
                quarkName);
        try {
            FeignContext context = applicationContext.getBean(FeignContext.class);
//            logger.debug("获取FeignContext实例完成");
            parameterInfo.getBusinessContext().setQuarkServiceName(quarkServiceName);
            if (org.apache.commons.lang.StringUtils.isEmpty(name)) {
                throw new IllegalArgumentException("The service name cannot be empty");
            }

            //Feign.Builder builder = feign(context, name);
            Feign.Builder builder = getBuilder(quarkName);
            if (!StringUtils.hasText(url)) {
                if (!name.startsWith("http")) {
                    url = "http://" + name;
                } else {
                    url = name;
                }
                url += cleanPath(path);
                Client client = getOptional(context, name, Client.class);
                //QuarkRemoteClient quarkRemoteClient = HystrixFeign.builder().client(client).target(QuarkRemoteClient
                        //.class, url);
                QuarkRemoteClient quarkRemoteClient = loadBalance(builder, name, context, url);
                ProcessResult result = quarkRemoteClient.execute(parameterInfo.getBusinessContext()
                        .generateSerializableContext());
                return result;
            }

            if (StringUtils.hasText(url) && !url.startsWith("http")) {
                url = "http://" + url;
            }
            url = url + cleanPath(path);
            Client client = getOptional(context, name, Client.class);
            if (client != null) {
                if (client instanceof LoadBalancerFeignClient) {
                    // not lod balancing because we have a url,
                    // but ribbon is on the classpath, so unwrap
                    client = ((LoadBalancerFeignClient) client).getDelegate();
                }
                builder.client(client);
            }

            //HystrixFeign.builder().client(new RibbonClient()

            QuarkRemoteClient quarkRemoteClient = builder.target(QuarkRemoteClient.class, url);
            ProcessResult result = quarkRemoteClient.execute(parameterInfo.getBusinessContext()
                    .generateSerializableContext());
            return result;
        } catch (Exception ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            String msg = "An error occurred while invoking remote provider,current service name:" +
                    quarkServiceName + ",target quark service name" +
                    name + ",target quark name:" + quarkName;
            stackTrace = msg + "-----" + stackTrace;
            logger.error(stackTrace);
            //throw new RemoteExecuteException(stackTrace);
            throw ex;
        } finally {
        }
    }

    Feign.Builder getBuilder(String quarkName) {
        HystrixFeign.Builder builder = HystrixFeign.builder().setterFactory(new SetterFactory() {
            @Override
            public HystrixCommand.Setter create(Target<?> target, Method method) {
                return HystrixCommand.Setter
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey(quarkName + "G"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey(quarkName))
                        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(quarkName + "P"))
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

    protected <T> QuarkRemoteClient loadBalance(Feign.Builder builder, String name, FeignContext context, String url) {
        Client client = getOptional(context, name, Client.class);
        if (client != null) {
            logger.debug("Feign Client:{}", client.getClass().getName());
            builder.client(client);
            return builder.target(QuarkRemoteClient.class, url);
        }

        throw new IllegalStateException(
                "No Feign Client for loadBalancing defined. Did you forget to include spring-cloud-starter-ribbon?");
    }

    private String cleanPath(String path) {
//        String path = this.path.trim();
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

    protected <T> T get(FeignContext context, String name, Class<T> type) {
        T instance = context.getInstance(name, type);
        if (instance == null) {
            throw new IllegalStateException("No bean found of type " + type + " for "
                    + name);
        }
        return instance;
    }

    protected Feign.Builder feign(FeignContext context, String name) {
        Feign.Builder builder = get(context, name, Feign.Builder.class)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder());
        //.contract(get(context, Contract.class));
        // optional values
        Logger.Level level = getOptional(context, name, Logger.Level.class);
        if (level != null) {
            builder.logLevel(level);
        }
        Retryer retryer = getOptional(context, name, Retryer.class);
        if (retryer != null) {
            builder.retryer(retryer);
        }
        ErrorDecoder errorDecoder = getOptional(context, name, ErrorDecoder.class);
        if (errorDecoder != null) {
            builder.errorDecoder(errorDecoder);
        }
        Request.Options options = getOptional(context, name, Request.Options.class);
        if (options != null) {
            builder.options(options);
        }
        Map<String, RequestInterceptor> requestInterceptors = context.getInstances(
                name, RequestInterceptor.class);
        if (requestInterceptors != null) {
            builder.requestInterceptors(requestInterceptors.values());
        }


//        if (decode404) {
//            builder.decode404();
//        }

        return builder;
    }

    protected <T> T getOptional(FeignContext context, String name, Class<T> type) {
        return context.getInstance(name, type);
    }

    public ProcessResult compensate(RemoteQuarkParameterInfo parameterInfo) {
        return null;
    }


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

}
