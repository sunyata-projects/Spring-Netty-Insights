package org.vertx.insight.ioc;


import org.sunyata.quark.util.ClassUtils;
import org.sunyata.quark.util.StringUtils;

@SuppressWarnings("serial")
public class NoSuchBeanDefinitionException extends BeansException {

    private String beanName;

    private ResolvableType resolvableType;


    /**
     * Create a new {@code NoSuchBeanDefinitionException}.
     * @param name the name of the missing bean
     */
    public NoSuchBeanDefinitionException(String name) {
        super("No bean named '" + name + "' available");
        this.beanName = name;
    }

    /**
     * Create a new {@code NoSuchBeanDefinitionException}.
     * @param name the name of the missing bean
     * @param message detailed message describing the problem
     */
    public NoSuchBeanDefinitionException(String name, String message) {
        super("No bean named '" + name + "' available: " + message);
        this.beanName = name;
    }

    /**
     * Create a new {@code NoSuchBeanDefinitionException}.
     * @param type required type of the missing bean
     */
    public NoSuchBeanDefinitionException(Class<?> type) {
        this(ResolvableType.forClass(type));
    }

    /**
     * Create a new {@code NoSuchBeanDefinitionException}.
     * @param type required type of the missing bean
     * @param message detailed message describing the problem
     */
    public NoSuchBeanDefinitionException(Class<?> type, String message) {
        this(ResolvableType.forClass(type), message);
    }

    /**
     * Create a new {@code NoSuchBeanDefinitionException}.
     * @param type full type declaration of the missing bean
     * @since 4.3.4
     */
    public NoSuchBeanDefinitionException(ResolvableType type) {
        super("No qualifying bean of type '" + type + "' available");
        this.resolvableType = type;
    }

    /**
     * Create a new {@code NoSuchBeanDefinitionException}.
     * @param type full type declaration of the missing bean
     * @param message detailed message describing the problem
     * @since 4.3.4
     */
    public NoSuchBeanDefinitionException(ResolvableType type, String message) {
        super("No qualifying bean of type '" + type + "' available: " + message);
        this.resolvableType = type;
    }

    /**
     * Create a new {@code NoSuchBeanDefinitionException}.
     * @param type required type of the missing bean
     * @param dependencyDescription a description of the originating dependency
     * @param message detailed message describing the problem
     * @deprecated as of 4.3.4, in favor of {@link #NoSuchBeanDefinitionException(ResolvableType, String)}
     */
    @Deprecated
    public NoSuchBeanDefinitionException(Class<?> type, String dependencyDescription, String message) {
        super("No qualifying bean" + (!StringUtils.hasLength(dependencyDescription) ?
                " of type '" + ClassUtils.getQualifiedName(type) + "'" : "") + " found for dependency" +
                (StringUtils.hasLength(dependencyDescription) ? " [" + dependencyDescription + "]" : "") +
                ": " + message);
        this.resolvableType = ResolvableType.forClass(type);
    }


    /**
     * Return the name of the missing bean, if it was a lookup <em>by name</em> that failed.
     */
    public String getBeanName() {
        return this.beanName;
    }

    /**
     * Return the required type of the missing bean, if it was a lookup <em>by type</em>
     * that failed.
     */
    public Class<?> getBeanType() {
        return (this.resolvableType != null ? this.resolvableType.getRawClass() : null);
    }

    /**
     * Return the required {@link ResolvableType} of the missing bean, if it was a lookup
     * <em>by type</em> that failed.
     * @since 4.3.4
     */
    public ResolvableType getResolvableType() {
        return this.resolvableType;
    }


    public int getNumberOfBeansFound() {
        return 0;
    }

}
