package com.zhangyx.controller;

import com.zhangyx.Trace.TraceContext;
import com.zhangyx.metric.DropWizardMetricFactory;
import com.zhangyx.metric.TimeMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class UserController {

    @Autowired
    private DropWizardMetricFactory dropWizardMetricFactory;


    @RequestMapping("/hello/{myName}")
    String index(@PathVariable String myName) {
        TraceContext context = TraceContext.get();
        context.setServerName("metric-es");
        context.setUrl("/hello");
        int sleepMillins = new Random().nextInt(500) + 1;
        try {
            try {
                Thread.sleep(sleepMillins);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello " + myName + "!!!";
        } finally {
            context.setFail(false);
            context.setCost(sleepMillins);
        }
    }
}
