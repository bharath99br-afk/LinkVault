package com.linkvault.backend.controller;

import com.linkvault.backend.dto.ApiResponse;
import com.linkvault.backend.model.Link;
import com.linkvault.backend.service.LinkService;
import jakarta.validation.Valid;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

// @GetMapping("/api/hello")
// public Link hello() {
//     return linkService.getDemoLink();
// }

@RestController
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("/api/links")
    public ResponseEntity<ApiResponse<List<Link>>> getLinks() {
        List<Link> links = linkService.getAllLinks();
        ApiResponse<List<Link>> response = new ApiResponse<>(true, "All Links", links);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/links/search")
    public ResponseEntity<Link> searchByTitle(@RequestParam String title) {
        return linkService.getLinkByTitle(title)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/links")
    public ResponseEntity<ApiResponse<Link>> addLink(@Valid @RequestBody Link link) {

        Link savedLink = linkService.addLink(link);
        ApiResponse<Link> response = new ApiResponse<>(
                true,
                "Link Created Successfully",
                savedLink);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // @DeleteMapping("/api/links/{id}")
    // public String deleteLink(@PathVariable Long id) {

    // boolean deleted = linkService.deleteLink(id);

    // if (deleted) {
    // return "Link Deleted Successfully!";
    // }

    // return "Link Not Found!";
    // }

    // @PutMapping("/api/links/{id}")
    // public String updateLink(@PathVariable Long id, @RequestBody Link
    // updatedLink) {
    // boolean updated = linkService.updateLink(id, updatedLink);

    // if (updated) {
    // return "Link Updated Successfully";
    // }
    // return "Link not Updated";
    // }
}