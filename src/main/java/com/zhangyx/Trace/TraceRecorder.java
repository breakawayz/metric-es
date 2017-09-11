package com.zhangyx.Trace;


import com.google.common.collect.Lists;
import com.zhangyx.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * trace记录
 */
public class TraceRecorder implements AutoCloseable {
  private static final Logger LOG = LoggerFactory.getLogger(TraceRecorder.class);
  private static final TraceRecorder INSTANCE = new TraceRecorder();
  private String clientName;
  private String clientIp;
  private List<ContextHandler> handlers = Lists.newArrayList();

  private TraceRecorder() {
    try {
      init();
    } catch (Exception e) {
      LOG.error("init error", e);
    }
  }

  public static TraceRecorder getInstance() {
    return INSTANCE;
  }

  private void init() {
    clientIp = IpUtil.getLocalIp();
    handlers.add(new OssStatistic());

  }

  /**
   * 发送到handler独立的queue里,避免相互影响
   *
   * @param c TraceContext对象
   */
  public void post(TraceContext c) {
    c.setClientIp(clientIp);
    handlers.forEach(i -> i.handle(c));
  }

  @Override
  public void close() {
    handlers.forEach(ContextHandler::close);
  }
}
