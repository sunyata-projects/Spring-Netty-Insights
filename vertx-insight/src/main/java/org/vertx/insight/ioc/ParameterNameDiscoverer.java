package org.vertx.insight.ioc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by leo on 16/12/10.
 */
public interface ParameterNameDiscoverer {

    /**
     * Return parameter names for this method,
     * or {@code null} if they cannot be determined.
     * @param method method to find parameter names for
     * @return an array of parameter names if the names can be resolved,
     * or {@code null} if they cannot
     */
    String[] getParameterNames(Method method);

    /**
     * Return parameter names for this constructor,
     * or {@code null} if they cannot be determined.
     * @param ctor constructor to find parameter names for
     * @return an array of parameter names if the names can be resolved,
     * or {@code null} if they cannot
     */
    String[] getParameterNames(Constructor<?> ctor);

}

