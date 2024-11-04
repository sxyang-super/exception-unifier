package com.sxyangsuper.exceptionunifier.sample.remote.websupport;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("prefix")
@RestController
public class ExceptionCodePrefixController {

    @GetMapping
    public String get(@RequestParam String moduleId) {
        if (moduleId.equals("io.github.sxyang-super.exception-unifier-sample")) {
            return "SAMPLE-REMOTE";
        }
        throw new RuntimeException(String.format("unrecognized module id %s", moduleId));
    }
}
