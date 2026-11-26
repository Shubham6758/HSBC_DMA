package com.coforge.hsbcdma.repository.ProfileRepositories.KaratRepositories;

import com.coforge.hsbcdma.entity.dropdownEntities.Profile.Karat.KaratTestResultMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KaratTestResultMasterRepository extends JpaRepository<KaratTestResultMaster, Long> {

    Optional<KaratTestResultMaster> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}