package com.zhangyx.Trace;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zhangyx.esclient.EsClient;
import com.zhangyx.util.IpUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 每分钟统计,并上报到消息总线
 */
@Slf4j
public class OssStatistic implements Runnable, ContextHandler {

    private Snapshot current = new Snapshot();
    private ScheduledExecutorService executor;

    public OssStatistic() {
        ThreadFactory tf = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("oss-stat-%d").build();
        executor = Executors.newSingleThreadScheduledExecutor(tf);
        executor.scheduleAtFixedRate(this, 1, 1, TimeUnit.MINUTES);
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdownNow));
    }

    /**
     * 获取上次的快照，并重置一个新的
     *
     * @return 上次的快照
     */
    public Snapshot getAndReset() {
        Snapshot old = current;
        current = new Snapshot();
        return old;
    }

    /**
     * 每分钟上报统计信息
     */
    @Override
    public void run() {
        Snapshot s = getAndReset();

        try {
            reportService(s);
        } catch (Exception e) {
            log.info("cannot calculate {} service", s.getService().size(), e);
        }
    }

    /**
     * 上报service调用统计
     *
     * @param s 当前快照
     */
    private void reportService(Snapshot s) {
        ConcurrentMap<String, ServiceStatCounter> service = s.getService();
        if (service.isEmpty()) {
            return;
        }
        long stamp = s.getStamp();
        String caller = IpUtil.getLocalIp();

        for (Map.Entry<String, ServiceStatCounter> entry : service.entrySet()) {
            ServiceStatCounter cnt = entry.getValue();
            String serviceName = entry.getKey();
            String module = cnt.getModule();
            if (cnt.getTotalCost() == 0) {
                log.warn("skip when cost=0, module={}, method={}", module);
                continue;
            }
            try {
                //此处上传数据
                Map<String, Object> map = cnt.fillCounter().toMap();
                map.put("ip", caller);
                map.put("url", serviceName);
                EsClient.sendDate(map);
                System.out.println(cnt);
            } catch (Exception e) {
                log.error("cannot calculate service, module={}", module, e);
            }
        }
    }

    @Override
    public void handle(TraceContext c) {
        current.add(c);
    }

    @Override
    public void close() {
        try {
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("{} was interrupted, now exit", Thread.currentThread().getName(), e);
            Throwables.propagate(e);
        }
    }
}
