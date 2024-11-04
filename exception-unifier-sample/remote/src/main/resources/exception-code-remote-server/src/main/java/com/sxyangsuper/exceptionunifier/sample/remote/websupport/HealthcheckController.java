package com.sxyangsuper.exceptionunifier.sample.remote.websupport;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("healthcheck")
@RestController
public class HealthcheckController {

    @GetMapping
    public String healthcheck() {
        return "OK";
    }
}
