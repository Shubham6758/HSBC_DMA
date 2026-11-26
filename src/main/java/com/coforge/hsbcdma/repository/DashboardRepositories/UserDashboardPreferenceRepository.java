package com.coforge.hsbcdma.repository.DashboardRepositories;

import com.coforge.hsbcdma.entity.UserDashboardPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDashboardPreferenceRepository
        extends JpaRepository<UserDashboardPreference, Long> {

    Optional<UserDashboardPreference>
    findByUserIdAndDashboardName(
            String userId,
            String dashboardName
    );
}