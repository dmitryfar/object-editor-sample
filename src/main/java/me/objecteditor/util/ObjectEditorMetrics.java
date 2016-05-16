package me.objecteditor.util;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;

@Component
public class ObjectEditorMetrics {
  private final Logger logger = LoggerFactory.getLogger(ObjectEditorMetrics.class);

  private static volatile ObjectEditorMetrics instance;

  public static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();
  // private final Meter requests = METRIC_REGISTRY.meter("requests");

  @PostConstruct
  private void init() {
//    ConsoleReporter reporter = ConsoleReporter.forRegistry(METRIC_REGISTRY)
//        .convertRatesTo(TimeUnit.SECONDS)
//        .convertDurationsTo(TimeUnit.MILLISECONDS)
//        .build();
//    reporter.start(60, TimeUnit.SECONDS);

    final JmxReporter jmxReporter = JmxReporter.forRegistry(METRIC_REGISTRY).build();
    jmxReporter.start();
  }

  public static ObjectEditorMetrics getInstance() {
    ObjectEditorMetrics localInstance = instance;
    if (localInstance == null) {
      synchronized (ObjectEditorMetrics.class) {
        localInstance = instance;
        if (localInstance == null) {
          instance = localInstance = new ObjectEditorMetrics();
        }
      }
    }
    return localInstance;
  }
}
