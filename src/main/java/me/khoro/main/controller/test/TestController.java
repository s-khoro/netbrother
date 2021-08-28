package me.khoro.main.controller.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Profile(value = "test")
@RestController
@RequestMapping("test")
@RequiredArgsConstructor
public class TestController {


    @GetMapping
    public String test() {
        log.info("Test get mapping requested");
        return "Test";
    }

    private final Tracer tracer;

    @GetMapping("sleuthTest")    public String sleuthTest() {
        log.info("Sleuth 1 message");
        tracer.withSpan(tracer.nextSpan()); // gens new span id
        log.info("Sleuth 2 message");
        return "sleuthTest";
    }
}
