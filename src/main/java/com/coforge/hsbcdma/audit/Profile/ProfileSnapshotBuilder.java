package com.coforge.hsbcdma.audit.Profile;

import com.coforge.hsbcdma.audit.core.SnapshotBuilder;
import com.coforge.hsbcdma.entity.Profile;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
@Component
public class ProfileSnapshotBuilder implements SnapshotBuilder<Profile> {

    @Override
    public Map<String, Object> snapshot(Profile p) {
        Map<String, Object> m = new LinkedHashMap<>();

        m.put("candidateName", p.getCandidateName());
        m.put("emailId", p.getEmailId());
        m.put("empId", p.getEmpId());
        m.put("phoneNumber", p.getPhoneNumber());
        m.put("isActive", p.getIsActive());
        m.put("experience", p.getExperience());
        m.put("summary", p.getSummary());
        m.put("fileName", p.getFileName());
        m.put("panNumber", p.getPanNumber());

        // dropdown refs: store id + name safely
        m.put("skillClusterId", p.getSkillCluster() != null ? p.getSkillCluster().getId() : null);
        m.put("skillCluster", p.getSkillCluster() != null ? p.getSkillCluster().getSkillCluster() : null);

        m.put("locationId", p.getLocation() != null ? p.getLocation().getId() : null);
        m.put("location", p.getLocation() != null ? p.getLocation().getName() : null);

        m.put("hbuId", p.getHbu() != null ? p.getHbu().getId() : null);
        m.put("hbu", p.getHbu() != null ? p.getHbu().getHbu() : null);

        m.put("externalInternalId", p.getExternalInternal() != null ? p.getExternalInternal().getId() : null);
        m.put("externalInternal", p.getExternalInternal() != null ? p.getExternalInternal().getName() : null);

        m.put("countryId", p.getCountry() != null ? p.getCountry().getId() : null);
        m.put("country", p.getCountry() != null ? p.getCountry().getName() : null);

        m.put("profileStatusId",p.getProfileStatus() != null ? p.getProfileStatus().getId() : null);
        m.put("profileStatus",p.getProfileStatus() != null ? p.getProfileStatus().getName() : null);

        // skills: store sorted lists for stable diffs
        m.put("primarySkills",
                p.getPrimarySkills() == null ? List.of() :
                        p.getPrimarySkills().stream()
                                .filter(Objects::nonNull)
                                .map(s -> s.getPrimarySkills())
                                .sorted()
                                .collect(Collectors.toList())
        );

        m.put("secondarySkills",
                p.getSecondarySkills() == null ? List.of() :
                        p.getSecondarySkills().stream()
                                .filter(Objects::nonNull)
                                .map(s -> s.getSecondarySkills())
                                .sorted()
                                .collect(Collectors.toList())
        );

        return m;
    }

}
