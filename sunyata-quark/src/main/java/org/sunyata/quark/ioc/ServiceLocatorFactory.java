package org.sunyata.quark.ioc;

/**
 * Created by leo on 16/12/10.
 */
public interface ServiceLocatorFactory {


    ServiceLocator getLocator() throws IllegalAccessException, InstantiationException;

}
