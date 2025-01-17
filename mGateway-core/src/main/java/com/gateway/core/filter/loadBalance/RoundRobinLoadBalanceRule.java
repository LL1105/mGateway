package com.gateway.core.filter.loadBalance;

import com.gateway.common.config.DynamicConfigManager;
import com.gateway.common.config.Rule;
import com.gateway.common.config.ServiceInstance;
import com.gateway.common.enums.ResponseCode;
import com.gateway.common.exception.ResponseException;
import com.gateway.core.context.GatewayContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yu
 * @description 轮询——负载均衡策略
 * @date 2024-04-02
 */
@Slf4j
public class RoundRobinLoadBalanceRule implements LoadBalanceRule{
    private static Map<String, RoundRobinLoadBalanceRule> loadBalanceRuleMap = new ConcurrentHashMap<>();

    private AtomicInteger position = new AtomicInteger(1);

    private String serviceId;

    public RoundRobinLoadBalanceRule(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * 根据服务 ID 获取负载均衡策略
     */
    public static RoundRobinLoadBalanceRule getInstance(String serviceId) {
        RoundRobinLoadBalanceRule rule = loadBalanceRuleMap.get(serviceId);
        if (rule == null) {
            rule = new RoundRobinLoadBalanceRule(serviceId);
            loadBalanceRuleMap.put(serviceId, rule);
        }
        return rule;
    }


    /**
     * 通过上下文参数获取服务实例
     */
    @Override
    public ServiceInstance choose(GatewayContext ctx, boolean gray) {
        // 获取上下文 Rule 对象
        Rule rule = ctx.getRules();
        return chooseByServiceId(rule.getServiceId(), gray);
    }

    /**
     * 通过服务ID拿到对应的服务实例
     */
    @Override
    public ServiceInstance chooseByServiceId(String serviceId, boolean gray) {
        // 根据服务ID获取服务实例集合
        Set<ServiceInstance> serviceSets = DynamicConfigManager.getInstance().getServiceInstanceByUniqueId(serviceId, gray);
        if (CollectionUtils.isEmpty(serviceSets)) {
            log.warn("serviceId {} don't match any serviceInstance", serviceId);
            throw new ResponseException(ResponseCode.SERVICE_INVOKER_NOT_FOUND);
        }
        List<ServiceInstance> serviceLists = new ArrayList<>(serviceSets);
        int pos = Math.abs(position.incrementAndGet());
        return serviceLists.get(pos % serviceLists.size());
    }
}
