package com.linkvault.backend.controller;

import com.linkvault.backend.model.Link;
import com.linkvault.backend.service.LinkService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
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

    // @GetMapping("/api/links")
    // public List<Link> getLinks() {
    // return linkService.getAllLinks();
    // }

    @PostMapping("/api/links")
    public String addLink(@RequestBody Link link) {

        linkService.addLink(link);

        return "Link Added Successfully!";
    }

    @DeleteMapping("/api/links/{id}")
    public String deleteLink(@PathVariable Long id) {

        boolean deleted = linkService.deleteLink(id);

        if (deleted) {
            return "Link Deleted Successfully!";
        }

        return "Link Not Found!";
    }

    @PutMapping("/api/links/{id}")
    public String updateLink(@PathVariable Long id, @RequestBody Link updatedLink) {
        boolean updated = linkService.updateLink(id, updatedLink);

        if (updated) {
            return "Link Updated Successfully";
        }
        return "Link not Updated";
    }
}