package com.linkvault.backend.controller;

import com.linkvault.backend.model.Link;
import com.linkvault.backend.service.LinkService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final LinkService linkService;

    public HelloController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("/api/hello")
    public Link hello() {
        return linkService.getDemoLink();
    }

    @GetMapping("/api/links")
    public List<Link> getLinks() {
        return linkService.getAllLinks();
    }
}