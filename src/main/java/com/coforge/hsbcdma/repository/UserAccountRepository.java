package com.coforge.hsbcdma.repository;

import com.coforge.hsbcdma.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This is a User Repository which fetches data from database
 * @author Vandana Pal
 */

@Repository
public interface UserAccountRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"role","country","location","department","subdepartment"})
    @Query("select u from User u")
    List<User> findAllWithRefs();

    @EntityGraph(attributePaths = {"role","country","location","department","subdepartment"})
    Optional<User> findById(Long id);

    Optional<User> findByUserId(String userId);

    Optional<User> findByUserIdAndEmailId(String userId, String emailId);

    Optional<User> findByEmailId(String emailId);
    List<User> findByUserIdIn(Collection<String> userIds);
}
