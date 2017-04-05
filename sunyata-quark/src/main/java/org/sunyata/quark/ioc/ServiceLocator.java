package org.sunyata.quark.ioc;

import java.util.Map;
import java.util.ServiceLoader;

/**
 * Created by leo on 16/12/9.
 */
public interface ServiceLocator {

    static ServiceLocatorFactory serviceLocatorFactory = null;

    static ServiceLocatorFactory getServiceLocatorFactory() {
        if (serviceLocatorFactory == null) {
            ServiceLoader<ServiceLocatorFactory> item = ServiceLoader.load(ServiceLocatorFactory.class);
            for (ServiceLocatorFactory next : item) {
                if (next instanceof DefaultServiceLocatorFactory) {
                    continue;
                }
                return next;
            }
            return new DefaultServiceLocatorFactory();
        }
        return serviceLocatorFactory;
    }

    static ServiceLocator getLocator() throws InstantiationException, IllegalAccessException {
        return getServiceLocatorFactory().getLocator();
    }

    static <T> T getBestService(Class<T> requiredType) throws IllegalAccessException, InstantiationException {
        Map<String, T> serviceOfType = getLocator().getServiceOfType(requiredType);
        return serviceOfType.values().stream().findFirst().orElse(null);
    }

    /**
     * 获取实现此类型的组件
     * @param requiredType
     * @param <T>
     * @return 组件实例
     * @throws BeansException
     */
    <T> T getService(Class<T> requiredType) throws BeansException, IllegalAccessException, InstantiationException;

    /**
     * 获取实现此类型的所有组件集合
     * @param type
     * @param <T>
     * @return 实现此类型的组件集合
     * @throws BeansException
     */
    <T> Map<String, T> getServiceOfType(Class<T> type) throws BeansException, IllegalAccessException,
            InstantiationException;


}
