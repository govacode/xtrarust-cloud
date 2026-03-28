package com.xtrarust.cloud.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtelLogConfig {

    private final OpenTelemetry openTelemetry;

    public OtelLogConfig(OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
    }

    @PostConstruct
    public void setupLogAppender() {
        OpenTelemetryAppender.install(openTelemetry);
    }
}
