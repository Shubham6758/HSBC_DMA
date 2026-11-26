package com.coforge.hsbcdma.repository.ColumnRepository;

import com.coforge.hsbcdma.entity.ColumnPreference.UserFilterPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserFilterPreferenceRepository extends JpaRepository<UserFilterPreference, Long> {
    Optional<UserFilterPreference> findByUserIdAndFilterName(
            Long userId,
            String filterName);
}
