package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.DashboardDTO.DashboardPreferenceRequest;
import com.coforge.hsbcdma.entity.UserDashboardPreference;
import com.coforge.hsbcdma.repository.DashboardRepositories.UserDashboardPreferenceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard/preference")
public class DashboardPreferenceController {

    @Autowired
    private UserDashboardPreferenceRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/save")
    public ResponseEntity<?> savePreference(
            @RequestBody DashboardPreferenceRequest request) {

        try {

            String userId = SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();

            UserDashboardPreference preference =
                    repository.findByUserIdAndDashboardName(
                                    userId,
                                    request.getDashboardName())
                            .orElse(null);

            if (preference == null) {
                preference = new UserDashboardPreference();
            }

            preference.setUserId(userId);
            preference.setDashboardName(
                    request.getDashboardName()
            );

            preference.setHbuIds(
                    objectMapper.writeValueAsString(request.getHbuIds())
            );

            preference.setHmIds(
                    objectMapper.writeValueAsString(request.getHmIds())
            );

            preference.setSkillIds(
                    objectMapper.writeValueAsString(request.getSkillIds())
            );

            preference.setLobIds(
                    objectMapper.writeValueAsString(request.getLobIds())
            );

            preference.setPriorityIds(
                    objectMapper.writeValueAsString(
                            request.getPriorityIds()
                    )
            );

            repository.save(preference);

            return ResponseEntity.ok("Saved");

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{dashboardName}")
    public ResponseEntity<?> getPreference(
            @PathVariable String dashboardName) {

        try {

            String userId = SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();

            Optional<UserDashboardPreference> preference =
                    repository.findByUserIdAndDashboardName(
                            userId,
                            dashboardName
                    );

            if (preference.isEmpty()) {
                return ResponseEntity.ok(null);
            }

            DashboardPreferenceRequest response =
                    new DashboardPreferenceRequest();

            response.setDashboardName(
                    preference.get().getDashboardName()
            );

            response.setHbuIds(
                    objectMapper.readValue(
                            preference.get().getHbuIds(),
                            objectMapper.getTypeFactory()
                                    .constructCollectionType(
                                            java.util.List.class,
                                            Long.class
                                    )
                    )
            );

            response.setHmIds(
                    objectMapper.readValue(
                            preference.get().getHmIds(),
                            objectMapper.getTypeFactory()
                                    .constructCollectionType(
                                            java.util.List.class,
                                            Long.class
                                    )
                    )
            );

            response.setSkillIds(
                    objectMapper.readValue(
                            preference.get().getSkillIds(),
                            objectMapper.getTypeFactory()
                                    .constructCollectionType(
                                            java.util.List.class,
                                            Long.class
                                    )
                    )
            );

            response.setLobIds(
                    objectMapper.readValue(
                            preference.get().getLobIds(),
                            objectMapper.getTypeFactory()
                                    .constructCollectionType(
                                            java.util.List.class,
                                            Long.class
                                    )
                    )
            );

            response.setPriorityIds(
                    objectMapper.readValue(
                            preference.get().getPriorityIds(),
                            objectMapper.getTypeFactory()
                                    .constructCollectionType(
                                            java.util.List.class,
                                            Long.class
                                    )
                    )
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(e.getMessage());
        }
    }

}
