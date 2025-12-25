package com.example.opentelemetry_overview.service;

import com.example.opentelemetry_overview.controller.MainController;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MainService {

    Logger LOG = LoggerFactory.getLogger(MainController.class);

    @Autowired
    Tracer tracer;

    public void processReq() {
        Span span = tracer.spanBuilder("processReq").setAttribute("test","test").startSpan();
        try(Scope scope = span.makeCurrent()) {
            LOG.info("Hello");
            // application code here
        }
        span.end();
    }
}
