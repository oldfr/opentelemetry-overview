package com.example.opentelemetry_overview.controller;

import com.example.opentelemetry_overview.service.MainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class MainController {

    Logger log = LoggerFactory.getLogger(MainController.class);

    @Autowired
    MainService mainService;

    @GetMapping
    public String getTest() {
        log.info("testing...");
        return "done";
    }

    @PostMapping
    public String createTest() {
        log.info("creating test...");
        mainService.processReq();
        return "done";
    }
}
