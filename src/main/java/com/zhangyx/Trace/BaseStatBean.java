package com.zhangyx.Trace;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 计数器相关代码
 */
@Data
@NoArgsConstructor
public abstract class BaseStatBean implements Serializable {
    protected int totalCount = 0;
    protected int failCount = 0;
    protected int slowCount = 0;
    protected long totalCost = 0;
    protected int ms1 = 0;
    protected int ms10 = 0;
    protected int ms100 = 0;
    protected int ms1000 = 0;
    protected int ms10000 = 0;
    protected int msMore = 0;

    public BaseStatBean(int totalCount, int failCount, int slowCount, long totalCost) {
        this.totalCount = totalCount;
        this.failCount = failCount;
        this.slowCount = slowCount;
        this.totalCost = totalCost;
    }

    public String getAverageCost() {
        if (totalCount == 0 || totalCost == 0) {
            return "0";
        }
        return formatNumber(totalCost * 1.0 / totalCount);
    }

    private String getPercent(int top, int bottom) {
        if (bottom == 0 || top == 0) {
            return "0";
        }

        return formatNumber(top * 100.0 / bottom);
    }

    private String formatNumber(double number) {
        String ret = String.format("%.2f", number);
        if (ret.endsWith(".00")) {
            ret = ret.substring(0, ret.length() - 3);
        }
        return ret;
    }
}
