package com.sky.erm.repository;

import com.sky.erm.domain.ErmUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<ErmUser, Long> {
    Optional<ErmUser> findByName(String name);
    Optional<ErmUser> findByEmail(String email);
    
    @Query("SELECT u FROM ErmUser u LEFT JOIN FETCH u.externalProjects WHERE u.id = :userId")
    Optional<ErmUser> findByIdWithProjects(@Param("userId") Long userId);
}