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

package org.sunyata.quark.embed.springcloud;

import feign.*;
import feign.Logger;
import feign.codec.ErrorDecoder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.*;
import org.springframework.beans.BeansException;
import org.springframework.cloud.netflix.feign.FeignContext;
import org.springframework.cloud.netflix.feign.ribbon.LoadBalancerFeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.sunyata.quark.basic.*;
import org.sunyata.quark.embed.springcloud.feign.QuarkRemoteClient;
import org.sunyata.quark.stereotype.QuarkComponent;

import java.util.Map;

/**
 * Created by leo on 16/12/14.
 */
@Component
@QuarkComponent(quarkName = "RemoteQuarkComponent", quarkFriendlyName = "RemoteQuarkComponent", version = "1.0")
public class RemoteQuarkComponent extends AbstractQuarkComponent<RemoteQuarkParameterInfo> implements
        ApplicationContextAware {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(RemoteQuarkComponent.class);
    private ApplicationContext applicationContext;
    private String url;

    public String getPath() {
        return path;
    }

    public RemoteQuarkComponent setPath(String path) {
        this.path = path;
        return this;
    }

    private String path;

    public String getName() {
        return name;
    }

    public RemoteQuarkComponent setName(String name) {
        this.name = name;
        return this;
    }

    private String name;

    @Override
    public RemoteQuarkParameterInfo getParameterInfo(BusinessContext context) throws Exception {
        RemoteQuarkParameterInfo quarkParameterInfo = new RemoteQuarkParameterInfo();
        quarkParameterInfo.setBusinessContext(context);
        return quarkParameterInfo;
    }


    public String getUrl() {
        return url;
    }

    public RemoteQuarkComponent setUrl(String url) {
        this.url = url;
        return this;
    }

    public ProcessResult execute(RemoteQuarkParameterInfo parameterInfo) throws Exception {
        AbstractBusinessComponent businessComponent = parameterInfo.getBusinessContext().getBusinessComponent();

        try {
            FeignContext context = applicationContext.getBean(FeignContext.class);
            QuarkComponentOptions options = parameterInfo.getBusinessContext().getCurrentQuarkDescriptor().getOptions();
            this.setName((String) options.getValue("name", null));
            this.setPath((String) options.getValue("path", null));
            this.setUrl((String) options.getValue("url", null));
            if (this.getName() == null) {
                throw new Exception("服务名称不能为空");
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
                QuarkRemoteClient quarkRemoteClient = loadBalance(builder, context, url);
                ProcessResult result = quarkRemoteClient.execute(parameterInfo.getBusinessContext()
                        .generateSerializableContext());
                return result;
            }

            if (StringUtils.hasText(this.url) && !this.url.startsWith("http")) {
                this.url = "http://" + this.url;
            }
            String url = this.url + cleanPath();
            Client client = getOptional(context, Client.class);
            if (client != null) {
                if (client instanceof LoadBalancerFeignClient) {
                    // not lod balancing because we have a url,
                    // but ribbon is on the classpath, so unwrap
                    client = ((LoadBalancerFeignClient) client).getDelegate();
                }
                builder.client(client);
            }
            QuarkRemoteClient quarkRemoteClient = builder.target(QuarkRemoteClient.class, url);
            ProcessResult result = quarkRemoteClient.execute(parameterInfo.getBusinessContext()
                    .generateSerializableContext());
            return result;
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            throw ex;
        } finally {
        }
    }

    protected <T> QuarkRemoteClient loadBalance(Feign.Builder builder, FeignContext context, String url) {
        Client client = getOptional(context, Client.class);
        if (client != null) {
            builder.client(client);
            return builder.target(QuarkRemoteClient.class, url);
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

    protected <T> T get(FeignContext context, Class<T> type) {
        T instance = context.getInstance(this.name, type);
        if (instance == null) {
            throw new IllegalStateException("No bean found of type " + type + " for "
                    + this.name);
        }
        return instance;
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

    protected <T> T getOptional(FeignContext context, Class<T> type) {
        return context.getInstance(this.name, type);
    }

    public ProcessResult compensate(RemoteQuarkParameterInfo parameterInfo) {
        return null;
    }


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

}
