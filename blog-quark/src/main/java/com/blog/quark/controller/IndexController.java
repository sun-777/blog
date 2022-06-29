package com.blog.quark.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController implements BaseController {

    @GetMapping({"/index", "/quark"})
    public String index() {
        return "/static/index.html";
    }
}
