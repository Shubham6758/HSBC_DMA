package com.coforge.hsbcdma.repository.ColumnRepository;

import com.coforge.hsbcdma.entity.ColumnPreference.UserColumnPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserColumnPreferenceRepository
        extends JpaRepository<UserColumnPreference, Long> {

    Optional<UserColumnPreference> findByUserIdAndPageName(
            String userId,
            String pageName
    );
}