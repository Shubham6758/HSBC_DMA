package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.DemandsDTO.PageResponse;
import com.coforge.hsbcdma.dto.OnboardingDTO.EditOnboardingRequest;
import com.coforge.hsbcdma.dto.OnboardingDTO.OnboardingFilterRequest;
import com.coforge.hsbcdma.dto.OnboardingDTO.OnboardingResponseDTO;
import com.coforge.hsbcdma.service.OnboardingServices.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/onboarding")
@RequiredArgsConstructor
public class OnboardingController extends BaseController{

    private final OnboardingService onboardingService;

    @GetMapping
    public PageResponse getAllOnboardings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"id"));

        Page<OnboardingResponseDTO> result =
                onboardingService.getAllOnboardings(pageable);

        return new PageResponse(
                result.getContent(),
                result.isEmpty(),
                result.isFirst(),
                result.isLast(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @PutMapping("/{onboardingId}/edit")
    public ResponseEntity<?> editOnboarding(
            @PathVariable Long onboardingId,
            @RequestBody EditOnboardingRequest request
    ) {
        onboardingService.editOnboarding(onboardingId, request);
        //        return ResponseEntity.ok("Onboarding updated successfully");
        return success("Onboarding updated successfully");
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(
            @RequestBody OnboardingFilterRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse response = onboardingService.search(filter, page, size);
        return success(response);
    }

    @GetMapping("/dropdowns")
    public ResponseEntity<?> getDropdowns() {
        return success(onboardingService.getOnboardingDropdowns());
    }

}