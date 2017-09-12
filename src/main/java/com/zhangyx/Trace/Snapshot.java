package com.zhangyx.Trace;

import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;

/**
 * n分钟内的rpc调用统计快照
 * <p>
 */
public class Snapshot {
    private final long stamp = System.currentTimeMillis();
    private final ConcurrentMap<String, ServiceStatCounter> service = Maps.newConcurrentMap();

    public long getStamp() {
        return stamp;
    }

    public ConcurrentMap<String, ServiceStatCounter> getService() {
        return service;
    }

    public void add(TraceContext c) {
        String serverName = c.getApp();
        if (serverName == null) {
            return;
        }

        // 统计service
        ServiceStatCounter serviceStatCounter = service.get(serverName);
        if (serviceStatCounter == null) {
            serviceStatCounter = new ServiceStatCounter(serverName);
            ServiceStatCounter real = service.putIfAbsent(serverName, serviceStatCounter);
            if (real != null) {
                serviceStatCounter = real;
            }
        }
        int state = c.isFail() ? -1 : 0;
        int cost = (int) c.getCost();
        boolean isSlow = cost >= 500;
        serviceStatCounter.calculate(state, cost, isSlow);
    }
}
