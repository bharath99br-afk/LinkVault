package com.linkvault.backend.offer.controller;

import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.offer.dto.OfferRequest;
import com.linkvault.backend.offer.dto.OfferResponse;
import com.linkvault.backend.offer.service.OfferService;
import com.linkvault.backend.util.ApiResponseUtil;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.linkvault.backend.offer.dto.OfferIngestionRequest;
import com.linkvault.backend.offer.service.OfferIngestionService;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

        private final OfferService offerService;
        private final OfferIngestionService offerIngestionService;

        public OfferController(OfferService offerService,
                        OfferIngestionService offerIngestionService) {
                this.offerService = offerService;
                this.offerIngestionService = offerIngestionService;
        }

        @GetMapping
        public ResponseEntity<ApiResponse<PageResponse<OfferResponse>>> getOffers(
                        @RequestParam(required = false) String title,
                        @PageableDefault(page = 0, size = 10) Pageable pageable) {

                PageResponse<OfferResponse> offers = offerService.getOffers(title, pageable);

                return ApiResponseUtil.success(
                                "Offers Found",
                                offers);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<OfferResponse>> getOffer(
                        @PathVariable Long id) {

                OfferResponse offer = offerService.getOffer(id);

                return ApiResponseUtil.success(
                                "Offer Found",
                                offer);
        }

        @PostMapping
        public ResponseEntity<ApiResponse<OfferResponse>> addOffer(
                        @Valid @RequestBody OfferRequest request) {

                OfferResponse offer = offerService.addOffer(request);

                return ApiResponseUtil.created(
                                "Offer Created Successfully",
                                offer);
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<OfferResponse>> updateOffer(
                        @PathVariable Long id,
                        @Valid @RequestBody OfferRequest request) {

                OfferResponse offer = offerService.updateOffer(id, request);

                return ApiResponseUtil.success(
                                "Offer Updated Successfully",
                                offer);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Object>> deleteOffer(
                        @PathVariable Long id) {

                offerService.deleteOffer(id);

                return ApiResponseUtil.success(
                                "Offer Deleted Successfully",
                                null);
        }

        @PostMapping("/ingest")
        public ResponseEntity<ApiResponse<OfferResponse>> ingestOffer(
                        @Valid @RequestBody OfferIngestionRequest request) {

                OfferResponse offer = offerIngestionService.ingestOffer(request);

                return ApiResponseUtil.created(
                                "Offer Ingested Successfully",
                                offer);
        }
}