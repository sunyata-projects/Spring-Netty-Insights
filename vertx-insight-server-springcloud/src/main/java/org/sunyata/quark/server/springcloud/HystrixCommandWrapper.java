package org.sunyata.quark.server.springcloud;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import feign.*;
import feign.codec.ErrorDecoder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignContext;
import org.springframework.cloud.netflix.feign.ribbon.LoadBalancerFeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.basic.QuarkComponentOptions;
import org.sunyata.quark.exception.RemoteExecuteException;
import org.sunyata.quark.server.springcloud.feign.QuarkRemoteClient;

import java.util.Map;

/**
 * Created by leo on 17/7/27.
 */
public class HystrixCommandWrapper extends HystrixCommand<ProcessResult> {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(RemoteQuarkComponent.class);
    private String quarkServiceName;
    private ApplicationContext applicationContext;
    private RemoteQuarkParameterInfo parameterInfo;

    public HystrixCommandWrapper(String quarkServiceName, ApplicationContext applicationContext,
                                 RemoteQuarkParameterInfo info) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"), 100000);
        this.quarkServiceName = quarkServiceName;
        this.applicationContext = applicationContext;
        this.parameterInfo = info;
    }

    /**
     * 这个方法里面封装了正常的逻辑，我们可以传入正常的业务逻辑
     *
     * @return
     * @throws Exception
     */
    @Override
    protected ProcessResult run() throws Exception {
        //AbstractBusinessComponent businessComponent = parameterInfo.getBusinessContext().getBusinessComponent();

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
        logger.info("current service name:{},target quark provider service name :{},target quark name:{}",
                quarkServiceName, name, quarkName);
        try {
//            logger.debug("获取FeignContext实例......");
            FeignContext context = applicationContext.getBean(FeignContext.class);
//            logger.debug("获取FeignContext实例完成");
            parameterInfo.getBusinessContext().setQuarkServiceName(quarkServiceName);
//            this.setName((String) options.getValue("name", null));
//            this.setPath((String) options.getValue("path", null));
//            this.setUrl((String) options.getValue("url", null));
            if (org.apache.commons.lang.StringUtils.isEmpty(name)) {
                throw new IllegalArgumentException("The service name cannot be empty");
            }

//            logger.debug("获取FeignBuilder实例,服务名称:{}......", name);
            Feign.Builder builder = feign(context, name);
//            logger.debug("获取FeignBuilder实例,服务名称:{}完成", name);
            if (!StringUtils.hasText(url)) {
                if (!name.startsWith("http")) {
                    url = "http://" + name;
                } else {
                    url = name;
                }
                url += cleanPath(path);
//                logger.debug("quarkProvider Url:{}", url);
//                logger.debug("生成QuarkRemoteClient......");
                QuarkRemoteClient quarkRemoteClient = loadBalance(builder, name, context, url);
//                logger.debug("生成QuarkRemoteClient完成,{}", quarkRemoteClient.getClass().getName());
//                logger.debug("调用远远程服务......");
                ProcessResult result = quarkRemoteClient.execute(parameterInfo.getBusinessContext()
                        .generateSerializableContext());
//                logger.debug("调用远远程服务完成");
//                logger.info("调用远程quarkProvider返回结果:{}", Json.encode(result));
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
            throw new RemoteExecuteException(stackTrace);
        } finally {
        }
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

    /**
     * 这个方法中定义了出现异常时, 默认返回的值(相当于服务的降级)。
     *
     * @return
     */
    @Override
    protected ProcessResult getFallback() {
        return ProcessResult.r();
    }

}
