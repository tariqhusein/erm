package com.sky.erm.repository;

import com.sky.erm.domain.ErmUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<ErmUser, Long> {
    Optional<ErmUser> findByName(String name);
    Optional<ErmUser> findByEmail(String email);
}