package com.spring.project.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/error")
public class AdminErrorController {

    @GetMapping("/404")
    public String error404() {
        return "admin/pages/error404";
    }

    @GetMapping("/500")
    public String error500() {
        return "admin/pages/error500";
    }
}
