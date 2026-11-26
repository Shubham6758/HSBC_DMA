package com.coforge.hsbcdma.repository;

import com.coforge.hsbcdma.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Query("select u from User u where u.role.role <> 'SYSTEM'")
    List<User> findAllWithRefs();

    @EntityGraph(attributePaths = {"role","country","location","department","subdepartment"})
    Optional<User> findById(Long id);

    Optional<User> findByUserId(String userId);

    Optional<User> findByUserIdAndEmailId(String userId, String emailId);

    Optional<User> findByEmailId(String emailId);
    List<User> findByUserIdIn(Collection<String> userIds);

    @Query("""
        SELECT u
        FROM User u
        JOIN FETCH u.role r
        WHERE u.userId = :userId
          AND u.active = true
          AND r.active = true
    """)
    Optional<User> findActiveUserWithActiveRole(
            @Param("userId") String userId
    );
}
