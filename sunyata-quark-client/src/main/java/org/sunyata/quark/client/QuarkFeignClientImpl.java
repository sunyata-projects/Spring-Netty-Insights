package org.sunyata.quark.client;

/**
 * Created by leo on 17/5/11.
 */
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

import feign.*;
import feign.codec.ErrorDecoder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignContext;
import org.springframework.util.StringUtils;
import org.sunyata.quark.client.dto.BusinessComponentDescriptor;
import org.sunyata.quark.client.dto.BusinessComponentInstance;
import org.sunyata.quark.client.message.JacksonDecoder;
import org.sunyata.quark.client.message.JacksonEncoder;

import java.util.List;
import java.util.Map;

/**
 * Created by leo on 16/12/14.
 */
public class QuarkFeignClientImpl implements QuarkFeignClient {
    private final String name;
    private org.slf4j.Logger logger = LoggerFactory.getLogger(QuarkFeignClientImpl.class);

    public QuarkFeignClientImpl(String name) {
        this.name = name;
    }
//
//    @Value("${spring.application.name}")
//    public String quarkServiceName;


//    private String url;
//
//    public String getPath() {
//        return path;
//    }
//
//    public QuarkFeignClientImpl setPath(String path) {
//        this.path = path;
//        return this;
//    }
//
//    private String path;
//
//    public String getName() {
//        return name;
//    }
//
//    public QuarkFeignClientImpl setName(String name) {
//        this.name = name;
//        return this;
//    }
//
//    private String name;
//
//
//    public String getUrl() {
//        return url;
//    }
//
//    public QuarkFeignClientImpl setUrl(String url) {
//        this.url = url;
//        return this;
//    }

    public QuarkFeignClient getObject(String path) throws Exception {
        try {
            FeignContext context = SpringContextUtilForClient.getBean(FeignContext.class);
            Feign.Builder builder = feign(context, name);
            String url = "http://" + name;
            url += cleanPath(path);
            return loadBalance(builder, context, name, url);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            throw ex;
        } finally {
        }
    }

    protected <T> QuarkFeignClient loadBalance(Feign.Builder builder, FeignContext context, String name, String url) {
        Client client = getOptional(context, Client.class, name);
        if (client != null) {
            builder.client(client);
            return builder.target(QuarkFeignClient.class, url);
        }

        throw new IllegalStateException(
                "No Feign Client for loadBalancing defined. Did you forget to include spring-cloud-starter-ribbon?");
    }

    private String cleanPath(String p) {
        String path = p.trim();
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

    protected <T> T get(FeignContext context, Class<T> type, String name) {
        T instance = context.getInstance(name, type);
        if (instance == null) {
            throw new IllegalStateException("No bean found of type " + type + " for "
                    + name);
        }
        return instance;
    }

    protected Feign.Builder feign(FeignContext context, String name) {
        Feign.Builder builder = get(context, Feign.Builder.class, name)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder());
        //.contract(get(context, Contract.class));
        // optional values
        Logger.Level level = getOptional(context, Logger.Level.class, name);
        if (level != null) {
            builder.logLevel(level);
        }
        Retryer retryer = getOptional(context, Retryer.class, name);
        if (retryer != null) {
            builder.retryer(retryer);
        }
        ErrorDecoder errorDecoder = getOptional(context, ErrorDecoder.class, name);
        if (errorDecoder != null) {
            builder.errorDecoder(errorDecoder);
        }
        Request.Options options = getOptional(context, Request.Options.class, name);
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

    protected <T> T getOptional(FeignContext context, Class<T> type, String name) {
        return context.getInstance(name, type);
    }

    @Override
    public JsonResponseResult create(@Param("serianNo") String serialNo,
                                     @Param("businName") String businName,
                                     @Param("sponsor") String sponsor,
                                     @Param("relationId") String relationId,
                                     @Param("parameterString") String parameterString,
                                     @Param("autoRun") boolean autoRun) throws Exception {
        QuarkFeignClient object = getObject("business/create");
        return object.create(serialNo, businName, sponsor, relationId, parameterString, autoRun);
    }

    @Override
    public JsonResponseResult<List<BusinessComponentDescriptor>> components() throws Exception {
        return getObject("business/components").components();
    }

    @Override
    public JsonResponseResult run(@Param(value = "serialNo") String serialNo) throws Exception {
        return getObject("business/run/" + serialNo).run(serialNo);
    }

    @Override
    public JsonResponseResult runByManual(@Param(value = "serialNo") String serialNo, @Param(value = "quarkIndex")
    Integer quarkIndex, @Param(value = "parameterString") String parameters) throws Exception {
        return getObject("business/runByManual").runByManual(serialNo, quarkIndex, parameters);
    }

    @Override
    public JsonResponseResult<BusinessComponentInstance> instance(@Param(value = "serialNo") String serialNo) throws
            Exception {
        return getObject("business/instance").instance(serialNo);
    }
}

