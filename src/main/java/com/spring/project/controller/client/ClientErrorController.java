package com.spring.project.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ClientErrorController {

    @GetMapping("/404")
    public String error404() {
        return "client/pages/error404";
    }

    @GetMapping("/500")
    public String error500() {
        return "client/pages/error500";
    }
}
