/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package com.zhangyx.metric;

import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.naming.ConfigurationException;

@Component
public class DropWizardMetricFactory implements MetricFactory {

    @Autowired
    private MetricRegistry metricRegistry;

    @Autowired
    private DropWizardJVMMetrics dropWizardJVMMetrics;

    private ESMetricReporter esMetricReporter;

    @PostConstruct
    private void init() {
        ESReporterConfiguration.Builder builder =
                ESReporterConfiguration.builder().enabled().onHost("172.20.17.135", 9200).periodInSecond(10L);
        esMetricReporter = new ESMetricReporter(builder.build(), metricRegistry);
        try {
            start();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        // 启动jvm
        dropWizardJVMMetrics.start();
    }

    @Override
    public Metric generate(String name) {
        return new DropWizardMetric(metricRegistry.counter(name));
    }

    @Override
    public TimeMetric timer(String name) {
        return new DropWizardTimeMetric(name, metricRegistry.timer(name).time());
    }

    public void start() throws ConfigurationException {
        esMetricReporter.start();
    }

    @PreDestroy
    public void stop() {
        esMetricReporter.stop();
    }

}
