package com.example.opentelemetry_overview.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    private static final String OTLP_ENDPOINT = "http://localhost:4317";

    @Bean
    public OpenTelemetry openTelemetry() {

        Resource resource = Resource.getDefault().toBuilder()
                .put("ResourceAttributes.SERVICE_NAME", "spring-service")
                .build();

        // ---------- Traces ----------
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(OTLP_ENDPOINT)
                .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
                .setResource(resource)
                .build();

        // ---------- Metrics ----------
        OtlpGrpcMetricExporter metricExporter = OtlpGrpcMetricExporter.builder()
                .setEndpoint(OTLP_ENDPOINT)
                .build();

        SdkMeterProvider meterProvider = SdkMeterProvider.builder()
                .registerMetricReader(PeriodicMetricReader.builder(metricExporter).build())
                .setResource(resource)
                .build();

        // ---------- Logs ----------
        OtlpGrpcLogRecordExporter logExporter = OtlpGrpcLogRecordExporter.builder()
                .setEndpoint(OTLP_ENDPOINT)
                .build();

        SdkLoggerProvider loggerProvider = SdkLoggerProvider.builder()
                .addLogRecordProcessor(BatchLogRecordProcessor.builder(logExporter).build())
                .setResource(resource)
                .build();

        ContextPropagators propagators = getContextPropagators();

        // ---------- Build SDK ----------
        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setMeterProvider(meterProvider)
                .setLoggerProvider(loggerProvider)
                .setPropagators(propagators)
                .buildAndRegisterGlobal();
    }

    private ContextPropagators getContextPropagators() {
        TextMapPropagator textMapPropagator = W3CTraceContextPropagator.getInstance(); // if w3c trace Propagation needed
        W3CBaggagePropagator baggagePropagator = W3CBaggagePropagator.getInstance(); // if baggage Propagation needed

        return ContextPropagators.create(
                TextMapPropagator.composite(B3Propagator.injectingMultiHeaders())
        );
    }
}
