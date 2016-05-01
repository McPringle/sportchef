/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2016 Marcus Fihlon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.sportchef.metrics;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.lang.management.ManagementFactory;
import java.util.Map.Entry;

@Singleton
@Startup
public class MetricsJvmConfiguration {

    @Inject
    private MetricRegistry metricRegistry;

    @PostConstruct
    private void registerMetrics() {
        registerAll("jvm.gc", new GarbageCollectorMetricSet());
        registerAll("jvm.buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
        registerAll("jvm.memory", new MemoryUsageGaugeSet());
        registerAll("jvm.threads", new ThreadStatesGaugeSet());
        metricRegistry.register("jvm.fileDescriptorCountRatio", new FileDescriptorRatioGauge());
    }

    private void registerAll(@NotNull final String prefix, @NotNull final MetricSet metricSet) {
        for (final Entry<String, Metric> entry : metricSet.getMetrics().entrySet()) {
            if (entry.getValue() instanceof MetricSet) {
                registerAll(prefix + "." + entry.getKey(), (MetricSet) entry.getValue());
            } else {
                metricRegistry.register(prefix + "." + entry.getKey(), entry.getValue());
            }
        }
    }

}
