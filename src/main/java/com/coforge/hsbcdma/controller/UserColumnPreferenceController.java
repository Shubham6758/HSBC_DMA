package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.ColumnPreferenceDTO.ColumnPreferenceRequest;
import com.coforge.hsbcdma.entity.ColumnPreference.UserColumnPreference;
import com.coforge.hsbcdma.repository.ColumnRepository.UserColumnPreferenceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-column-preference")
public class UserColumnPreferenceController {

    private final UserColumnPreferenceRepository repository;
    private final ObjectMapper objectMapper;

    @PostMapping("/save")
    public ResponseEntity<?> savePreference(
            @RequestBody ColumnPreferenceRequest request) {

        try {

            // Get logged-in user id from your JWT/security context
            String userId = SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getName();

            UserColumnPreference preference =
                    repository.findByUserIdAndPageName(
                                    userId,
                                    request.getPageName())
                            .orElse(null);

            if (preference == null) {
                preference = new UserColumnPreference();
            }

            preference.setUserId(userId);
            preference.setPageName(request.getPageName());
            preference.setSelectedColumns(
                    objectMapper.writeValueAsString(
                            request.getSelectedColumns()
                    )
            );

            repository.save(preference);

            return ResponseEntity.ok("Preference saved successfully");

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{pageName}")
    public ResponseEntity<?> getPreference(
            @PathVariable String pageName) {

        try {

            String userId = SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getName();

            Optional<UserColumnPreference> optional =
                    repository.findByUserIdAndPageName(
                            userId,
                            pageName);

            if (optional.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<String> columns = objectMapper.readValue(
                    optional.get().getSelectedColumns(),
                    List.class
            );

            return ResponseEntity.ok(columns);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(e.getMessage());
        }
    }
}