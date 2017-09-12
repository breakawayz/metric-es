package com.zhangyx.Trace;

/**
 * Trace信息
 */
public class TraceContext {
    private static final ThreadLocal<TraceContext> context = new ThreadLocal<>();
    private long cost;
    private String clientIp;
    private String url;
    private boolean fail;
    private String app;


    public static TraceContext get() {
        TraceContext c = context.get();
        if (c == null) {
            c = new TraceContext();
            context.set(c);
        }
        return c;
    }

    public static void remove() {
        context.remove();
    }

    /**
     * 属于内部方法,应用层请不要调用
     *
     * @param c
     */
    public static void _set(TraceContext c) {
        context.set(c);
    }


    public long getCost() {
        return cost;
    }

    public TraceContext setCost(long cost) {
        this.cost = cost;
        return this;
    }


    public String getClientIp() {
        return clientIp;
    }

    public TraceContext setClientIp(String clientIp) {
        this.clientIp = clientIp;
        return this;
    }


    public String getApp() {
        return app;
    }

    public TraceContext setApp(String app) {
        this.app = app;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public TraceContext setUrl(String url) {
        this.url = url;
        return this;
    }


    public boolean isFail() {
        return fail;
    }

    public TraceContext setFail(boolean fail) {
        this.fail = fail;
        return this;
    }

    public TraceContext reset() {
        this.cost = 0;
        this.fail = false;
        this.app = null;
        this.url = null;
        return this;
    }


    public TraceContext copy() {
        TraceContext n = new TraceContext();
        n.setCost(cost)
                .setFail(fail)
                .setClientIp(clientIp)
                .setApp(app)
                .setUrl(url);
        return n;
    }

    private String removeTabChar(String s) {
        if (s == null) {
            return "";
        }
        int pos = s.indexOf('\t');
        if (pos == -1) {
            return s;
        }
        int length = s.length();
        StringBuilder sb = new StringBuilder(length);
        if (pos > 0) {
            sb.append(s.substring(0, pos)).append(' ');
        }
        for (int i = pos + 1; i < length; i++) {
            char c = s.charAt(i);
            if (c == '\t') {
                sb.append(' ');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
