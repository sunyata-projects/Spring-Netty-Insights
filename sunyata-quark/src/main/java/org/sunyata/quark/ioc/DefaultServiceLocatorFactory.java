package org.sunyata.quark.ioc;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Created by leo on 16/12/10.
 */
public class DefaultServiceLocatorFactory implements ServiceLocatorFactory {
    public DefaultServiceLocatorFactory() {
    }

    @Override
    public ServiceLocator getLocator() throws IllegalAccessException, InstantiationException {
        if (serviceLocatorClass == null) {
            return new DefaultServiceLocator();
        } else {
            return serviceLocatorClass.newInstance();
        }
    }

    static Class<? extends ServiceLocator> serviceLocatorClass;

    public static <T extends ServiceLocator> void setServiceLocator(Class<T> serviceLocator) {
        serviceLocatorClass = serviceLocator;
    }

    class DefaultServiceLocator implements ServiceLocator {

        @Override
        public <T> T getService(Class<T> requiredType) throws BeansException {
            ServiceLoader<T> load = ServiceLoader.load(requiredType);
            for (T next : load) {
                return next;
            }
            return null;
        }

        @Override
        public <T> Map<String, T> getServiceOfType(Class<T> type) throws BeansException {
            HashMap<String, T> mappings = new HashMap<>();
            ServiceLoader<T> load = ServiceLoader.load(type);
            for (T next : load) {
                mappings.put(next.toString(), next);
            }
            return mappings;
        }
    }
}
