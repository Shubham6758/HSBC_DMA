package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.OnboardingOLDDTO;
import com.coforge.hsbcdma.mapper.EntityDtoMapper;
import com.coforge.hsbcdma.repository.OnboardingOldRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OnboardingController exposes onboarding list endpoint under /api/v1/onboarding.
 */
@RestController
@RequestMapping("/api/v1/onboarding")
public class OnboardingOLDController {
    private final OnboardingOldRepository onboardingOldRepository;
    public OnboardingOLDController(OnboardingOldRepository onboardingOldRepository) { this.onboardingOldRepository = onboardingOldRepository; }

    /**
     * List onboarding records.
     * Created by: Pooja.I
     */
    @GetMapping
    public ResponseEntity<List<OnboardingOLDDTO>> list() {
        List<OnboardingOLDDTO> result = onboardingOldRepository.findAll().stream()
                .map(EntityDtoMapper::toOnboardingDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
