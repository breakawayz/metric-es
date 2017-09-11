package com.zhangyx.Trace;

import lombok.Data;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * 封装了公共的计数器逻辑
 */
@Data
public abstract class StatCounter {
    //依次是总调用次数，失败数，流控失败数，超时数，总耗时
    private AtomicIntegerArray counters = new AtomicIntegerArray(5);
    private AtomicIntegerArray rangeCounter = new AtomicIntegerArray(6);
    private int side = 0; //统计provider或者consumer

    /**
     * 上报执行状态
     *
     * @param state -1:fail, 0:succ, 1:流控获取不到client
     * @param time  本次调用耗时
     * @param slow  是否超时
     */
    public void calculate(int state, int time, boolean slow) {
        counters.getAndIncrement(0); //增加总调用次数
        counters.addAndGet(4, time);
        if (state != 0) {
            counters.getAndIncrement(1); //增加失败数
            if (state == 1) {
                counters.getAndIncrement(2); //增加流控失败数
            }
        }
        if (slow) {
            counters.getAndIncrement(3); //增加超时次数
        }
        //增加分区间统计
        if (time <= 1) {
            rangeCounter.getAndIncrement(0);
        } else if (time <= 10) {
            rangeCounter.getAndIncrement(1);
        } else if (time <= 100) {
            rangeCounter.getAndIncrement(2);
        } else if (time <= 1000) {
            rangeCounter.getAndIncrement(3);
        } else if (time <= 10000) {
            rangeCounter.getAndIncrement(4);
        } else {
            rangeCounter.getAndIncrement(5);
        }
    }

    public int getTotalCount() {
        return counters.get(0);
    }

    public int getFailCount() {
        return counters.get(1);
    }

    public int getFlowLimitFailCount() {
        return counters.get(2);
    }

    public int getSlowCount() {
        return counters.get(3);
    }

    public int getTotalCost() {
        return counters.get(4);
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public void fillCounter(BaseStatBean e) {
        e.setTotalCount(counters.get(0));
        e.setFailCount(counters.get(1));
        e.setSlowCount(counters.get(3));
        e.setTotalCost(counters.get(4));
        e.setMs1(rangeCounter.get(0));
        e.setMs10(rangeCounter.get(1));
        e.setMs100(rangeCounter.get(2));
        e.setMs1000(rangeCounter.get(3));
        e.setMs10000(rangeCounter.get(4));
        e.setMsMore(rangeCounter.get(5));
    }
}
