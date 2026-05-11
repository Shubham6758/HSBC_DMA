package com.coforge.hsbcdma.audit.History;

import com.coforge.hsbcdma.audit.AuditHistory;
import com.coforge.hsbcdma.audit.AuditHistoryRepository;
import com.coforge.hsbcdma.audit.core.DiffNameEnricher;
import com.coforge.hsbcdma.dto.DemandsDTO.PageResponse;
import com.coforge.hsbcdma.entity.User;
import com.coforge.hsbcdma.repository.ProfileRepositories.ProfileRepository;
import com.coforge.hsbcdma.repository.UserAccountRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {

    private final AuditHistoryRepository auditRepo;
    private final ObjectMapper objectMapper;
    private final DiffNameEnricher diffNameEnricher;

    private final UserAccountRepository userAccountRepository;
    private final ProfileRepository profileRepository;

    public PageResponse getDemandHistory(Long demandId, int page, int size, boolean includeDiff) {

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 200)
        );

        Page<AuditHistory> rows = auditRepo.findDemandTimeline(demandId, pageable);

        Page<HistoryItemDTO> mapped = rows.map(h -> {
            Map<String, Object> raw = includeDiff ? parseDiff(h.getDiff()) : Collections.emptyMap();
            Map<String, Object> enriched = includeDiff ? diffNameEnricher.enrich(raw) : Collections.emptyMap();

            return HistoryItemDTO.builder()
                    .auditId(h.getId())
                    .entityType(h.getEntityType())
                    .entityId(h.getEntityId())
                    .demandId(h.getDemandId())
                    .profileId(h.getProfileId())
                    .onboardingId(h.getOnboardingId())
                    .profileTrackerId(h.getProfileTrackerId())
                    .action(h.getAction())
//                    .changedByUserId(h.getChangedByUserId())
                    .changedByUserId(buildChangedUser(h.getChangedByUserId()))
                    .changedAt(h.getChangedAt())
                    .title(buildTitleFromAction(h.getAction()))
                    .profileInfoDTO(buildProfileInfo(h.getProfileId()))
                    .diff(enriched)
                    .build();
        });

        return new PageResponse(
                mapped.getContent(),
                mapped.isEmpty(),
                mapped.isFirst(),
                mapped.isLast(),
                mapped.getSize(),
                mapped.getTotalElements(),
                mapped.getTotalPages()
        );
    }



    public PageResponse getProfileHistory(Long profileId, int page, int size, boolean includeDiff) {

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 200)
        );

        Page<AuditHistory> rows = auditRepo.findProfileTimeline(profileId, pageable);

        Page<HistoryItemDTO> mapped = rows.map(h -> {
            Map<String, Object> raw = includeDiff ? parseDiff(h.getDiff()) : Collections.emptyMap();
            Map<String, Object> enriched = includeDiff ? diffNameEnricher.enrich(raw) : Collections.emptyMap();

            return HistoryItemDTO.builder()
                    .auditId(h.getId())
                    .entityType(h.getEntityType())
                    .entityId(h.getEntityId())

                    .profileId(h.getProfileId())
                    .demandId(h.getDemandId())
                    .profileTrackerId(h.getProfileTrackerId())
                    .onboardingId(h.getOnboardingId())

                    .action(h.getAction())
                    .changedByUserId(buildChangedUser(h.getChangedByUserId()))
                    .changedAt(h.getChangedAt())
                    .title(buildTitleFromAction(h.getAction()))
                    .profileInfoDTO(buildProfileInfo(h.getProfileId()))
                    .diff(enriched)
                    .build();
        });


        return new PageResponse(
                mapped.getContent(),
                mapped.isEmpty(),
                mapped.isFirst(),
                mapped.isLast(),
                mapped.getSize(),
                mapped.getTotalElements(),
                mapped.getTotalPages()
        );


    }


    // build changedBy user object
    private HistoryItemDTO.changedUserDTO buildChangedUser(String userId) {
        if (userId == null || userId.isBlank()) return null;

        String name = userAccountRepository.findByUserId(userId)
                .map(User::getName)
                .orElse(null);

        return new HistoryItemDTO.changedUserDTO(userId, name);
    }

    // build profile info object
    private HistoryItemDTO.ProfileInfoDTO buildProfileInfo(Long profileId) {
        if (profileId == null) return null;

        return profileRepository.findById(profileId)
                .map(p -> new HistoryItemDTO.ProfileInfoDTO(
                        p.getId(),
                        p.getCandidateName(),
                        p.getEmailId()
                ))
                .orElse(new HistoryItemDTO.ProfileInfoDTO(profileId, null, null));
    }


    private String buildTitleFromAction(AuditHistory.Action action) {

        switch (action) {
            // Attach / Detach
            case ATTACH:
                return "Attached";

            // Updates
            case UPDATE:
                return "Record updated";

            case UPDATE_DEMAND:
                return "Demand updated";

            case UPDATE_PROFILE:
                return "Profile updated";

            case UPDATE_PROFILE_TRACKER:
                return "ProfileTracker updated";

            case UPDATE_ONBOARDING:
                return "Onboarding updated";

            // Create / Delete
            case CREATE:
                return "Record created";

            default:
                // Fallback: "UPDATE_DEMAND" -> "Update demand"
                String s = action.name().toLowerCase().replace('_', ' ');
                return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }

    }

    private Map<String, Object> parseDiff(String json) {
        if (json == null || json.isBlank()) return Collections.emptyMap();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            return Map.of("_raw", json, "_error", "Invalid diff JSON");
        }
    }
}
