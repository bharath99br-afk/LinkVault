package com.linkvault.backend.link.controller;

import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.link.dto.LinkRequest;
import com.linkvault.backend.link.dto.LinkResponse;
import com.linkvault.backend.link.model.Link;
import com.linkvault.backend.link.service.LinkService;
import com.linkvault.backend.util.ApiResponseUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    // View Api & Search Api combined
    @GetMapping("/api/links")
    public ResponseEntity<ApiResponse<PageResponse<LinkResponse>>> getLinks(
            @RequestParam(required = false) String title,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        PageResponse<LinkResponse> links = linkService.getLinks(title, pageable);

        return ApiResponseUtil.success("Links Found", links);
    }

    // Create Api
    @PostMapping("/api/links")
    public ResponseEntity<ApiResponse<LinkResponse>> addLink(@Valid @RequestBody LinkRequest request) {

        LinkResponse savedLink = linkService.addLink(request);
        return ApiResponseUtil.created("Link Created Successfully", savedLink);
    }

    @DeleteMapping("/api/links/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteLink(@PathVariable Long id) {

        linkService.deleteLink(id);
        return ApiResponseUtil.success("Link Deleted Successfully", null);
    }

    // Update API
    @PutMapping("/api/links/{id}")
    public ResponseEntity<ApiResponse<LinkResponse>> updateLink(@PathVariable Long id,
            @Valid @RequestBody LinkRequest request) {
        LinkResponse updatedLink = linkService.updateLink(id, request);
        return ApiResponseUtil.success("Link Updated Successfully", updatedLink);
    }
}