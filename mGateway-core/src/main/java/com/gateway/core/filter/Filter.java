package com.gateway.core.filter;


import com.gateway.core.context.GatewayContext;


/**
 * @author yu
 * @description 过滤器顶层接口，具体子类实现过滤器功能
 * @date 2024-04-01
 */
public interface Filter {
    /**
     * 执行过滤器
     */
    void doFilter(GatewayContext ctx) throws Exception;

    /**
     * 获取过滤器执行顺序
     */
    default int getOrder() {
        FilterAspect aspect = this.getClass().getAnnotation(FilterAspect.class);
        if (aspect != null) {
            return aspect.order();
        }
        return Integer.MAX_VALUE;
    }
}
