package com.zhangyx.Trace;

/**
 * 处理TraceContext
 */
public interface ContextHandler extends AutoCloseable {
  void handle(TraceContext c);

  void close();
}
