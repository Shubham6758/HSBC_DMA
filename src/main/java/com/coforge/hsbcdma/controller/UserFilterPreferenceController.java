package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.UserFilterPreferenceDTO.UserFilterPreferencesRequest;
import com.coforge.hsbcdma.dto.UserFilterPreferenceDTO.UserFilterPreferencesResponse;
import com.coforge.hsbcdma.entity.ColumnPreference.UserFilterPreference;
import com.coforge.hsbcdma.repository.ColumnRepository.UserFilterPreferenceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-preferences")
@RequiredArgsConstructor
public class UserFilterPreferenceController {

    private final UserFilterPreferenceRepository userFilterPreferenceRepository;
    private final ObjectMapper objectMapper;

//    @PostMapping("/lob/save")
//    public ResponseEntity<?> saveLobFilter(
//            @RequestBody List<Long> lobIds) {
//
//        try {
//
//            String loggedInUser =
//                    SecurityContextHolder.getContext()
//                            .getAuthentication()
//                            .getName();
//
//            Long userId = Long.valueOf(loggedInUser);
//
//            UserFilterPreference preference =
//                    userFilterPreferenceRepository.findByUserIdAndFilterName(
//                                    userId,
//                                    "LOB")
//                            .orElse(new UserFilterPreference());
//
//            preference.setUserId(userId);
//            preference.setFilterName("LOB");
//            preference.setFilterValue(
//                    objectMapper.writeValueAsString(lobIds)
//            );
//
//            userFilterPreferenceRepository.save(preference);
//
//            return ResponseEntity.ok("LOB filter saved successfully");
//
//        } catch (Exception e) {
//
//            return ResponseEntity.internalServerError()
//                    .body(e.getMessage());
//        }
//    }
//
//    @GetMapping("/lob")
//    public ResponseEntity<?> getLobFilter() {
//
//        try {
//
//            String loggedInUser =
//                    SecurityContextHolder.getContext()
//                            .getAuthentication()
//                            .getName();
//
//            Long userId = Long.valueOf(loggedInUser);
//
//            Optional<UserFilterPreference> optional =
//                    userFilterPreferenceRepository.findByUserIdAndFilterName(
//                            userId,
//                            "LOB");
//
//            if (optional.isEmpty()) {
//                return ResponseEntity.ok(Collections.emptyList());
//            }
//
//            List<Long> lobIds = objectMapper.readValue(
//                    optional.get().getFilterValue(),
//                    new com.fasterxml.jackson.core.type.TypeReference<List<Long>>() {}
//            );
//
//            return ResponseEntity.ok(lobIds);
//
//        } catch (Exception e) {
//
//            return ResponseEntity.internalServerError()
//                    .body(e.getMessage());\[

//        }
//    }

    @Transactional
    @PostMapping("/save")
    public ResponseEntity<?> savePreferences(
            @RequestBody UserFilterPreferencesRequest request) {

        try {

            Long userId = Long.valueOf(
                    SecurityContextHolder.getContext()
                            .getAuthentication()
                            .getName());

            // Remove all existing preferences for user
            userFilterPreferenceRepository.deleteByUserId(userId);

            // Force delete execution before inserts
            userFilterPreferenceRepository.flush();

            // Save new preferences
            for (Map.Entry<String, List<String>> entry :
                    request.getPreferences().entrySet()) {

                UserFilterPreference pref =
                        new UserFilterPreference();

                pref.setUserId(userId);
                pref.setFilterName(entry.getKey());

                pref.setFilterValue(
                        objectMapper.writeValueAsString(
                                entry.getValue()));

                userFilterPreferenceRepository.save(pref);
            }

            return ResponseEntity.ok(
                    "Preferences saved successfully");

        } catch (Exception ex) {

            ex.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body(ex.getMessage());
        }
    }


    @GetMapping("/preferences")
    public ResponseEntity<?> getPreferences() {

        try {

            Long userId = Long.valueOf(
                    SecurityContextHolder.getContext()
                            .getAuthentication()
                            .getName());

            List<UserFilterPreference> preferences =
                    userFilterPreferenceRepository
                            .findByUserId(userId);

            Map<String, List<String>> responseMap =
                    new HashMap<>();

            for (UserFilterPreference preference : preferences) {

                List<String> values =
                        objectMapper.readValue(
                                preference.getFilterValue(),
                                new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {
                                });

                responseMap.put(
                        preference.getFilterName(),
                        values);
            }

            UserFilterPreferencesResponse response =
                    new UserFilterPreferencesResponse();

            response.setPreferences(responseMap);

            return ResponseEntity.ok(response);

        } catch (Exception ex) {

            return ResponseEntity.internalServerError()
                    .body(ex.getMessage());
        }
    }

    @Transactional
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearPreferences() {

        try {

            Long userId = Long.valueOf(
                    SecurityContextHolder.getContext()
                            .getAuthentication()
                            .getName());

            userFilterPreferenceRepository.deleteByUserId(userId);

            return ResponseEntity.ok(
                    "All filter preferences cleared successfully");

        } catch (Exception ex) {

            return ResponseEntity.internalServerError()
                    .body(ex.getMessage());
        }
    }

}
