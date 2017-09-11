package com.zhangyx.Trace;

public class ServiceStatCounter extends StatCounter {
  private final String module;

  public ServiceStatCounter(String module) {
    super();
    this.module = module;
  }

  public String getModule() {
    return module;
  }

}
