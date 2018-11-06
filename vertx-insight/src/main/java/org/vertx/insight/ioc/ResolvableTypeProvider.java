package org.vertx.insight.ioc;

/**
 * Created by leo on 16/12/10.
 */
public interface ResolvableTypeProvider {

    /**
     * Return the {@link ResolvableType} describing this instance
     * (or {@code null} if some sort of default should be applied instead).
     */
    ResolvableType getResolvableType();

}

