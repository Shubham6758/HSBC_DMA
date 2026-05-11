package com.coforge.hsbcdma.repository.UserManagementRepositories;

import com.coforge.hsbcdma.entity.UserManagementEntities.Action;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActionRepository extends JpaRepository<Action, Long> {

    Optional<Action> findByAction(String action);
    List<Action> findByActionIn(List<String> actions);
}
