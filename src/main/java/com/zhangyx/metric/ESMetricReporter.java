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
import com.codahale.metrics.ScheduledReporter;
import com.google.common.base.Throwables;
import com.zhangyx.util.IpUtil;
import org.elasticsearch.metrics.ElasticsearchReporter;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ESMetricReporter {

    private final Optional<ElasticsearchReporter> reporter;
    private final ESReporterConfiguration esReporterConfiguration;

    public ESMetricReporter(ESReporterConfiguration esReporterConfiguration, MetricRegistry registry) {
        this.reporter = getReporter(esReporterConfiguration, registry);
        this.esReporterConfiguration = esReporterConfiguration;
    }

    private Optional<ElasticsearchReporter> getReporter(ESReporterConfiguration esReporterConfiguration, MetricRegistry registry) {
        // 获取服务器ip

        Map<String, Object> additionalFileds = new HashMap<>();
        additionalFileds.put("ip", IpUtil.getLocalIp());
        additionalFileds.put("servername", "hello");

        if (esReporterConfiguration.isEnabled()) {
            try {
                return Optional.of(ElasticsearchReporter.forRegistry(registry)
                        .hosts(esReporterConfiguration.getHostWithPort())
                        .index(esReporterConfiguration.getIndex())
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .additionalFields(additionalFileds)
                        .build());
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }
        return Optional.empty();
    }

    public void start() {
        reporter.ifPresent(elasticsearchReporter ->
                elasticsearchReporter.start(esReporterConfiguration.getPeriodInSecond(), TimeUnit.SECONDS));
    }

    @PreDestroy
    public void stop() {
        reporter.ifPresent(ScheduledReporter::stop);
    }
}
