package com.linkvault.backend.controller;

import com.linkvault.backend.dto.ApiResponse;
import com.linkvault.backend.dto.LinkRequest;
import com.linkvault.backend.dto.PageResponse;
import com.linkvault.backend.model.Link;
import com.linkvault.backend.service.LinkService;
import com.linkvault.backend.util.ApiResponseUtil;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.linkvault.backend.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

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

    // View Api
    @GetMapping("/api/links")
    public ResponseEntity<ApiResponse<PageResponse<Link>>> getLinks(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        PageResponse<Link> links = linkService.getAllLinks(pageable);
        return ApiResponseUtil.success("All Links", links);
    }

    // Search Api
    @GetMapping("/api/links/search")
    public ResponseEntity<ApiResponse<PageResponse<Link>>> searchByTitle(
            @RequestParam @NotBlank(message = "Search title cannot be empty") String title,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        PageResponse<Link> links = linkService.getLinkByTitle(title, pageable);
        return ApiResponseUtil.success("Links Found!!", links);
    }

    // Create Api
    @PostMapping("/api/links")
    public ResponseEntity<ApiResponse<Link>> addLink(@Valid @RequestBody LinkRequest request) {

        Link savedLink = linkService.addLink(request);
        return ApiResponseUtil.created("Link Created Successfuy", savedLink);
    }

    @DeleteMapping("/api/links/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteLink(@PathVariable Long id) {

        linkService.deleteLink(id);

        ApiResponse<Object> response = new ApiResponse<>(true, "Link Deleted Successfully", null);
        return ResponseEntity.ok(response);
    }

    // Update API
    @PutMapping("/api/links/{id}")
    public ResponseEntity<ApiResponse<Link>> updateLink(@PathVariable Long id,
            @Valid @RequestBody LinkRequest request) {
        Link updatedLink = linkService.updateLink(id, request);
        ApiResponse<Link> response = new ApiResponse<>(true, "Link Updated Successfully", updatedLink);

        return ResponseEntity.ok(response);
    }
}